package com.example.myapplication.ui.reflow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class ReminderDialogFragment extends DialogFragment {

    private OnReminderSavedListener listener;
    private int year, month, dayOfMonth;

    // Interface para ser implementada pela atividade
    public interface OnReminderSavedListener {
        void onReminderSaved(int year, int month, int dayOfMonth, String reminder, boolean notify, String priority);
    }

    // Construtor para passar os parâmetros de data
    public ReminderDialogFragment(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Infla o layout personalizado
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_reminder, null);  // Supondo que seu arquivo XML seja chamado reminder_dialog_layout.xml

        // Referência para os componentes do layout
        EditText editTextReminder = view.findViewById(R.id.edit_text_reminder);
        CheckBox checkBoxNotify = view.findViewById(R.id.checkbox_notify);
        RadioGroup radioGroupPriority = view.findViewById(R.id.radio_group_priority);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Configuração do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);  // Define o layout personalizado no AlertDialog

        // Listener para o botão de salvar
        btnSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString();
            boolean notify = checkBoxNotify.isChecked();
            String priority = "Normal";
            if (radioGroupPriority.getCheckedRadioButtonId() == R.id.radio_priority_important) {
                priority = "Importante";
            }

            // Chame o listener para salvar o lembrete
            if (listener != null) {
                listener.onReminderSaved(year, month, dayOfMonth, reminderText, notify, priority);
            }
            dismiss();
        });

        // Listener para o botão de cancelar
        btnCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    // Configure o listener corretamente no onAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Verifique se o contexto implementa OnReminderSavedListener
            listener = (OnReminderSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnReminderSavedListener");
        }
    }
}
