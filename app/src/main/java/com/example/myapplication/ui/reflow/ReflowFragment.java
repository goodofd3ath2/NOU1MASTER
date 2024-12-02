package com.example.myapplication.ui.reflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.database.Reminder;
import com.example.myapplication.ui.database.ReminderAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReflowFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    private Button addReminderButton;
    private ReflowViewModel reflowViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View rootView = inflater.inflate(R.layout.fragment_reflow, container, false);

        // Inicializa os componentes do layout
        calendarView = rootView.findViewById(R.id.calendarView);
        reminderRecyclerView = rootView.findViewById(R.id.reminderRecyclerView);
        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        Button toggleCalendarButton = rootView.findViewById(R.id.toggleCalendarButton);

        // Listener para alternar a visibilidade do calendário
        toggleCalendarButton.setOnClickListener(v -> {
            if (calendarView.getVisibility() == View.VISIBLE) {
                calendarView.setVisibility(View.GONE); // Esconde o calendário
                toggleCalendarButton.setText("Mostrar Calendário");
            } else {
                calendarView.setVisibility(View.VISIBLE); // Mostra o calendário
                toggleCalendarButton.setText("Esconder Calendário");
            }
        });

        // Inicializa o ViewModel com uma Factory personalizada
        reflowViewModel = new ViewModelProvider(this, new ReflowViewModelFactory(requireContext())).get(ReflowViewModel.class);

        // Inicializa a lista de lembretes
        reminderList = new ArrayList<>();

        // Configura o RecyclerView
        setupRecyclerView();

        // Listener para o CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Carregar lembretes para a data selecionada
            loadRemindersForDate(year, month, dayOfMonth);
        });

        // Botão de adicionar lembrete
        addReminderButton.setOnClickListener(v -> openAddReminderDialog());

        return rootView; // Retorna a view raiz do fragmento
    }

    private void loadRemindersForDate(int year, int month, int dayOfMonth) {
        // Conecta ao ViewModel para carregar lembretes para a data selecionada
        reflowViewModel.loadReminders(year, month, dayOfMonth)
                .observe(getViewLifecycleOwner(), reminders -> {
                    reminderList.clear();
                    reminderList.addAll(reminders);
                    reminderAdapter.notifyDataSetChanged();
                });
    }

    private void openAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Adicionar Lembrete");

        // Infla o layout do diálogo
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_reminder, null);
        builder.setView(dialogView);

        EditText reminderText = dialogView.findViewById(R.id.reminderText);
        CheckBox notifyCheckBox = dialogView.findViewById(R.id.checkbox_notify);
        RadioGroup priorityGroup = dialogView.findViewById(R.id.radio_group_priority);
        Spinner repeatSpinner = dialogView.findViewById(R.id.repeat_spinner);
        Button saveButton = dialogView.findViewById(R.id.btn_save);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String text = reminderText.getText().toString();
            boolean notify = notifyCheckBox.isChecked();
            int priorityId = priorityGroup.getCheckedRadioButtonId();
            String priority = (priorityId == R.id.radio_priority_important) ? "Importante" : "Normal";
            long repeatInterval = parseRepeatInterval(repeatSpinner.getSelectedItem().toString());

            if (!text.isEmpty()) {
                Reminder newReminder = new Reminder(text, System.currentTimeMillis(), priority, notify, repeatInterval);
                reflowViewModel.insertReminder(newReminder); // Salva no banco de dados
                Toast.makeText(getContext(), "Lembrete adicionado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Digite um lembrete", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private long parseRepeatInterval(String repeatOption) {
        switch (repeatOption) {
            case "Diário":
                return 86400000L; // 1 dia em milissegundos
            case "Semanal":
                return 604800000L; // 1 semana em milissegundos
            case "Mensal":
                return 2592000000L; // 30 dias em milissegundos (aproximado)
            default:
                return 0; // Sem repetição
        }
    }

    private void setupRecyclerView() {
        // Configura o RecyclerView com um LayoutManager e o Adapter
        if (reminderRecyclerView != null) {
            reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
                @Override
                public void onEdit(int position, Reminder reminder) {
                    showEditReminderDialog(position, reminder);
                }

                @Override
                public void onDelete(int position, Reminder reminder) {
                    showDeleteConfirmationDialog(position, reminder);
                }
            });
            reminderRecyclerView.setAdapter(reminderAdapter);
        }
    }

    private void showEditReminderDialog(int position, Reminder reminder) {
        // Diálogo para editar lembretes
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Lembrete");

        final EditText input = new EditText(getContext());
        input.setText(reminder.getText());
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newText = input.getText().toString();
            reminder.setText(newText);
            reflowViewModel.updateReminder(reminder); // Atualiza no banco de dados
            reminderAdapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmationDialog(int position, Reminder reminder) {
        // Diálogo para confirmar exclusão de lembretes
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Excluir Lembrete");
        builder.setMessage("Tem certeza que deseja excluir este lembrete?");

        builder.setPositiveButton("Excluir", (dialog, which) -> {
            reflowViewModel.deleteReminder(reminder); // Excluir do banco de dados
            reminderList.remove(position);
            reminderAdapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Lembrete excluído", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
