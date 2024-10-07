package com.example.myapplication.ui.reflow;

import static com.example.myapplication.ui.reflow.ReflowViewModel.PREFS_NAME;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentReflowBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReflowFragment extends Fragment implements ReminderDialogFragment.OnReminderSavedListener {

    private FragmentReflowBinding binding;
    private CalendarView calendarView;
    private TextView reminderTextView;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter adapter;
    private static final String TAG = "ReflowFragment";
    private ReflowViewModel viewModel;

    private int selectedYear, selectedMonth, selectedDayOfMonth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ReflowViewModel.class);

        initializeViews(root);
        setupRecyclerView();
        setupCalendarView();
        setupObservers();
        requestNotificationPermission();
        return root;
    }

    private void loadReminders(int year, int month, int dayOfMonth) {
        viewModel.loadReminders(year, month, dayOfMonth, getContext());  // Pass context here
    }

    private void setupCalendarView() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDayOfMonth = dayOfMonth;
            updateReminderTextView(year, month, dayOfMonth);
            loadReminders(year, month, dayOfMonth);
        });
    }

    private void initializeViews(View root) {
        calendarView = root.findViewById(R.id.calendarView1);
        reminderTextView = root.findViewById(R.id.reminderTextView);
        reminderRecyclerView = root.findViewById(R.id.reminderRecyclerView);

        Button toggleCalendarButton = root.findViewById(R.id.toggleCalendarButton);
        toggleCalendarButton.setOnClickListener(v -> toggleCalendarView());

        binding.addReminderButton.setOnClickListener(v -> showAddReminderDialog(selectedYear, selectedMonth, selectedDayOfMonth));
    }

    private void setupRecyclerView() {
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReminderAdapter(new ArrayList<>(), this::onReminderClick);
        reminderRecyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            if (reminders.isEmpty()) {
                adapter.updateData(new ArrayList<>());
                adapter.addReminder(new Reminder("Nenhum lembrete.", System.currentTimeMillis(), 0));
            } else {
                adapter.updateData(reminders);
            }
        });
    }

    private void toggleCalendarView() {
        calendarView.setVisibility(calendarView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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

    private void showAddReminderDialog(int year, int month, int dayOfMonth) {
        ReminderDialogFragment dialogFragment = new ReminderDialogFragment(year, month, dayOfMonth);
        dialogFragment.show(getParentFragmentManager(), "ReminderDialog");
    }

    @Override
    public void onReminderSaved(int year, int month, int dayOfMonth, String reminderText, boolean notify, String priority) {
        if (reminderText == null || reminderText.trim().isEmpty()) {
            Toast.makeText(getContext(), "Por favor, insira um lembrete válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminderObj = new Reminder(reminderText, System.currentTimeMillis(), Integer.parseInt(priority));
        viewModel.saveReminder(year, month, dayOfMonth, reminderObj, getContext());

        if (notify) {
            setAlarm(reminderObj);
        }

        Toast.makeText(getContext(), "Lembrete adicionado!", Toast.LENGTH_SHORT).show();
        updateReminderTextView(year, month, dayOfMonth);
    }

    private void setAlarm(Reminder reminder) {
        // Your existing alarm setting logic...
    }

    private void updateReminderTextView(int year, int month, int dayOfMonth) {
        reminderTextView.setText(String.format("Lembretes para %d/%d/%d", dayOfMonth, month + 1, year));
        loadReminders(year, month, dayOfMonth);
    }

    private void onReminderClick(Reminder reminder) {
        EditReminderDialogFragment dialogFragment = EditReminderDialogFragment.newInstance(reminder.getText());
        dialogFragment.show(getParentFragmentManager(), "EditReminderDialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permissão para notificações concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permissão para notificações negada. As notificações não funcionarão.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
