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
import androidx.lifecycle.Observer;
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

        // Obter o ViewModel
        viewModel = new ViewModelProvider(this).get(ReflowViewModel.class);

        // Inicializar views e configurar componentes
        initializeViews(root);
        setupRecyclerView();
        setupCalendarView();
        setupObservers(); // Configurar observadores para LiveData
        requestNotificationPermission();

        return root;
    }

    private void initializeViews(View root) {
        calendarView = root.findViewById(R.id.calendarView1);
        reminderRecyclerView = root.findViewById(R.id.reminderRecyclerView);

        // Configuração do botão para adicionar lembretes
        root.findViewById(R.id.addReminderButton).setOnClickListener(v -> showAddReminderDialog(selectedYear, selectedMonth, selectedDayOfMonth));
    }

    private void setupRecyclerView() {
        // Configuração do RecyclerView
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                // Editar o lembrete
                EditReminderDialogFragment dialogFragment = EditReminderDialogFragment.newInstance(reminder.getText());
                dialogFragment.show(getParentFragmentManager(), "EditReminderDialog");
            }

            @Override
            public void onDelete(int position, Reminder reminder) {
                // Excluir o lembrete
                viewModel.deleteReminder(selectedYear, selectedMonth, selectedDayOfMonth, reminder, getContext());
                adapter.removeReminder(position);  // Atualizar a RecyclerView após exclusão
            }
        });
        reminderRecyclerView.setAdapter(adapter);
    }

    private void setupCalendarView() {
        // Listener para alteração de data no CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDayOfMonth = dayOfMonth;

            // Carrega os lembretes da data selecionada
            loadReminders(year, month, dayOfMonth);
        });
    }
    private void loadReminders(int year, int month, int dayOfMonth) {
        // Carregar lembretes da data selecionada
        viewModel.loadReminders(year, month, dayOfMonth, getContext());

        // Observa as mudanças no LiveData
        viewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            reminderList.clear();
            if (reminders != null && !reminders.isEmpty()) {
                reminderList.addAll(reminders);  // Usa addAll para adicionar todos os lembretes
            } else {
                reminderList.add(new Reminder("Nenhum lembrete.", System.currentTimeMillis(), "Normal", 0));
            }
            adapter.notifyDataSetChanged();  // Atualiza a RecyclerView
        });
    }


    private void setupObservers() {
        // Observando as mudanças no LiveData de lembretes
        viewModel.getReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                reminderList.clear();
                if (reminders.isEmpty()) {
                    reminderList.add(new Reminder("Nenhum lembrete.", System.currentTimeMillis(), "Normal", 0));
                } else {
                    reminderList.addAll(reminders);
                }
                adapter.notifyDataSetChanged(); // Atualizar a RecyclerView com os novos dados
            }
        });
    }

    private void showAddReminderDialog(int year, int month, int dayOfMonth) {
        // Mostrar o diálogo para adicionar lembretes
        ReminderDialogFragment dialogFragment = ReminderDialogFragment.newInstance(year, month, dayOfMonth);
        dialogFragment.show(getParentFragmentManager(), "ReminderDialog");
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    public void onReminderSaved(int year, int month, int dayOfMonth, String reminderText, boolean notify, String priority) {
        if (reminderText == null || reminderText.trim().isEmpty()) {
            Toast.makeText(getContext(), "Por favor, insira um lembrete válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salvar o lembrete e recarregar a lista de lembretes para o dia
        Reminder reminder = new Reminder(reminderText, System.currentTimeMillis(), priority, 0);
        viewModel.saveReminder(year, month, dayOfMonth, reminder, getContext());

        Toast.makeText(getContext(), "Lembrete adicionado!", Toast.LENGTH_SHORT).show();
        loadReminders(year, month, dayOfMonth);  // Recarregar a lista de lembretes após salvar
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
