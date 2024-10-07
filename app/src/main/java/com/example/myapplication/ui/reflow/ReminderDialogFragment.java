package com.example.myapplication.ui.reflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class ReminderDialogFragment extends DialogFragment {
    private int year, month, dayOfMonth;
    private OnReminderSavedListener listener;
    private CheckBox notificationCheckBox;
    private Spinner prioritySpinner;

    // Constructor to initialize the date
    public ReminderDialogFragment(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup listener for the parent activity or fragment
        listener = (OnReminderSavedListener) getParentFragment();
        if (listener == null) {
            listener = (OnReminderSavedListener) getActivity();
        }

        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Adicionar Lembrete");

        // Create dialog layout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Text field for reminder description
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Escreva o lembrete");
        layout.addView(input);

        // Checkbox for notification
        notificationCheckBox = new CheckBox(getActivity());
        notificationCheckBox.setText("Receber notificação");
        layout.addView(notificationCheckBox);

        // Spinner for priority selection
        prioritySpinner = new Spinner(getActivity());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        layout.addView(prioritySpinner);

        // Set layout for the dialog
        builder.setView(layout);

        // Setup "Save" button
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String reminder = input.getText().toString().trim();
            boolean notify = notificationCheckBox.isChecked();
            String priority = prioritySpinner.getSelectedItem().toString();

            if (!reminder.isEmpty()) {
                // Notify the listener with reminder data
                listener.onReminderSaved(year, month, dayOfMonth, reminder, notify, priority);
            } else {
                // Optional: Show a Toast if the reminder is empty
                Toast.makeText(getActivity(), "Por favor, escreva um lembrete.", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup "Cancel" button
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    // Interface for communication with the parent activity or fragment
    public interface OnReminderSavedListener {
        void onReminderSaved(int year, int month, int dayOfMonth, String reminder, boolean notify, String priority);
    }
}
