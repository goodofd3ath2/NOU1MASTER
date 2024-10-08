package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class ReminderDialogFragment extends DialogFragment {

    private OnReminderSavedListener listener;
    private int year, month, dayOfMonth;

    public interface OnReminderSavedListener {
        void onReminderSaved(int year, int month, int dayOfMonth, String reminder, boolean notify, String priority);
    }

    public ReminderDialogFragment(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_reminder_dialog, null);

        EditText editTextReminder = view.findViewById(R.id.edit_text_reminder);
        CheckBox checkBoxNotify = view.findViewById(R.id.checkbox_notify);
        RadioGroup radioGroupPriority = view.findViewById(R.id.radio_group_priority);
        Spinner repeatSpinner = view.findViewById(R.id.repeat_spinner);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.repeat_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        btnSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString();
            boolean notify = checkBoxNotify.isChecked();
            String priority = "Normal";
            if (radioGroupPriority.getCheckedRadioButtonId() == R.id.radio_priority_important) {
                priority = "Importante";
            }

            String repeatOption = repeatSpinner.getSelectedItem().toString();
            long repeatInterval = 0;
            if (repeatOption.equals("Diariamente")) {
                repeatInterval = AlarmManager.INTERVAL_DAY;
            } else if (repeatOption.equals("Semanalmente")) {
                repeatInterval = AlarmManager.INTERVAL_DAY * 7;
            }

            if (listener != null) {
                listener.onReminderSaved(year, month, dayOfMonth, reminderText, notify, priority);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnReminderSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnReminderSavedListener");
        }
    }
}
