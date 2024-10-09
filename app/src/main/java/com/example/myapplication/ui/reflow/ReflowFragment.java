package com.example.myapplication.ui.reflow;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentReflowBinding;

import java.util.ArrayList;
import java.util.List;

public class ReflowFragment extends Fragment implements ReminderDialogFragment.OnReminderSavedListener {

    private FragmentReflowBinding binding;
    private CalendarView calendarView;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter adapter;
    private ReflowViewModel viewModel;

    private int selectedYear, selectedMonth, selectedDayOfMonth;
    private List<Reminder> reminderList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModel para carregar e salvar lembretes
        viewModel = new ViewModelProvider(this).get(ReflowViewModel.class);

        // Inicializar os componentes visuais
        initializeViews(root);
        setupRecyclerView();
        setupCalendarView();
        observeReminders();  // Observar mudanças nos lembretes

        return root;
    }

    @Override
    public void onReminderUpdated(String updatedReminderText) {
        // Atualizar o lembrete editado e recarregar os lembretes para a data selecionada
        Reminder updatedReminder = new Reminder(updatedReminderText, System.currentTimeMillis(), "Normal", 0);
        viewModel.updateReminder(selectedYear, selectedMonth, selectedDayOfMonth, updatedReminder, getContext());

        // Recarregar os lembretes após a atualização
        loadRemindersForSelectedDate();
    }

    private void initializeViews(View root) {
        calendarView = root.findViewById(R.id.calendarView1);
        reminderRecyclerView = root.findViewById(R.id.reminderRecyclerView);

        Button addReminderButton = root.findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(v -> showAddReminderDialog(selectedYear, selectedMonth, selectedDayOfMonth));
    }

    private void setupRecyclerView() {
        // Configuração do RecyclerView com adaptador e layout manager
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                EditReminderDialogFragment dialogFragment = EditReminderDialogFragment.newInstance(reminder.getText());
                dialogFragment.show(getParentFragmentManager(), "EditReminderDialog");
            }

            @Override
            public void onDelete(int position, Reminder reminder) {
                viewModel.deleteReminder(selectedYear, selectedMonth, selectedDayOfMonth, reminder, getContext());
                adapter.removeReminder(position); // Atualizar RecyclerView
            }
        });
        reminderRecyclerView.setAdapter(adapter);
    }

    private void setupCalendarView() {
        // Quando o usuário seleciona uma data no CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDayOfMonth = dayOfMonth;
            loadRemindersForSelectedDate();  // Carregar lembretes da data selecionada
        });
    }

    private void loadRemindersForSelectedDate() {
        // Carregar lembretes da data selecionada e atualizar a RecyclerView
        LiveData<List<Reminder>> remindersLiveData = viewModel.loadReminders(selectedYear, selectedMonth, selectedDayOfMonth, getContext());
        remindersLiveData.observe(getViewLifecycleOwner(), reminders -> {
            reminderList.clear();
            if (reminders != null && !reminders.isEmpty()) {  // Corrigido para verificar se a lista não é nula e se está vazia
                reminderList.addAll(reminders);
            }
            adapter.notifyDataSetChanged();  // Atualizar a RecyclerView
        });
    }

    private void observeReminders() {
        // Observar mudanças nos lembretes e atualizar o RecyclerView
        viewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            reminderList.clear();
            if (reminders != null && !reminders.isEmpty()) {  // Certifique-se de que `reminders` não é nulo
                reminderList.addAll(reminders);
            }
            adapter.notifyDataSetChanged();  // Atualizar RecyclerView
        });
    }

    private void showAddReminderDialog(int year, int month, int dayOfMonth) {
        ReminderDialogFragment dialogFragment = ReminderDialogFragment.newInstance(year, month, dayOfMonth);
        dialogFragment.show(getParentFragmentManager(), "ReminderDialog");
    }

    @Override
    public void onReminderSaved(int year, int month, int dayOfMonth, String reminderText, boolean notify, String priority) {
        if (reminderText == null || reminderText.trim().isEmpty()) {
            Toast.makeText(getContext(), "Por favor, insira um lembrete válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salvar o lembrete e recarregar os lembretes da data
        Reminder reminder = new Reminder(reminderText, System.currentTimeMillis(), priority, 0);
        viewModel.saveReminder(year, month, dayOfMonth, reminder, getContext());

        Toast.makeText(getContext(), "Lembrete adicionado!", Toast.LENGTH_SHORT).show();
        loadRemindersForSelectedDate();  // Recarregar a lista de lembretes após salvar
    }

    private void setAlarm(Reminder reminder) {
        // Configurar alarme para o lembrete
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("reminderText", reminder.getText());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(getContext(), "Permissão para alarmes exatos não concedida!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
