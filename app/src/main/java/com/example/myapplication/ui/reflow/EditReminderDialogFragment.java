package com.example.myapplication.ui.reflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditReminderDialogFragment extends DialogFragment {
    private static final String ARG_REMINDER_TEXT = "reminder_text";
    private EditReminderDialogListener listener;

    public interface EditReminderDialogListener {
        void onEdit(int position, Reminder reminder);

        void onReminderUpdated(String updatedReminder);
    }

    // Static factory method to create an instance of the dialog
    public static EditReminderDialogFragment newInstance(String reminderText) {
        EditReminderDialogFragment fragment = new EditReminderDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REMINDER_TEXT, reminderText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditReminderDialogListener) context; // Fragment or Activity must implement this interface
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditReminderDialogListener");
        }
    }

    // Call this method when the reminder is updated and saved in the dialog
    private void saveReminder(Reminder updatedReminder) {
        listener.onReminderUpdated(String.valueOf(updatedReminder));
        dismiss(); // Close the dialog after saving
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String reminderText = getArguments() != null ? getArguments().getString(ARG_REMINDER_TEXT) : "";
        EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText(reminderText);

        return new AlertDialog.Builder(requireActivity()) // Use requireActivity() for better null safety
                .setTitle("Editar Lembrete")
                .setView(editText)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String updatedReminder = editText.getText().toString();
                    if (listener != null) {
                        listener.onReminderUpdated(updatedReminder);
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
