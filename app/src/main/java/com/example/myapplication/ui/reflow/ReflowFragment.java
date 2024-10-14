package com.example.myapplication.ui.reflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ReflowFragment extends Fragment {

    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    private Button addReminderButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View rootView = inflater.inflate(R.layout.fragment_reflow, container, false);

        // Inicializa o RecyclerView e o botão de adicionar lembrete
        reminderRecyclerView = rootView.findViewById(R.id.reminderRecyclerView);
        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        reminderList = new ArrayList<>();

        // Teste de lembrete manual
        reminderList.add(new Reminder("Teste 1", System.currentTimeMillis(), "Normal", 0));
        reminderList.add(new Reminder("Teste 2", System.currentTimeMillis(), "Alta", 0));

        // Configura o RecyclerView
        setupRecyclerView();

        // Configura o botão de adicionar lembrete
        addReminderButton.setOnClickListener(v -> {
            // Exibe uma mensagem para confirmar que o botão está funcionando
            Toast.makeText(getContext(), "Adicionar Lembrete pressionado!", Toast.LENGTH_SHORT).show();

            // Aqui você pode adicionar a lógica para abrir o diálogo de adição de lembrete
            ReminderDialogFragment dialogFragment = ReminderDialogFragment.newInstance(2024, 9, 1);
            dialogFragment.setTargetFragment(ReflowFragment.this, 0);
            dialogFragment.show(getParentFragmentManager(), "ReminderDialog");
        });

        return rootView;  // Retorna a view raiz do fragmento
    }

    private void setupRecyclerView() {
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                // Abrir um diálogo para editar o lembrete
                showEditReminderDialog(position, reminder);
            }

            @Override
            public void onDelete(int position, Reminder reminder) {
                // Excluir o lembrete após confirmação
                showDeleteConfirmationDialog(position, reminder);
            }
        });
        reminderRecyclerView.setAdapter(reminderAdapter);
    }

    // Método para mostrar o diálogo de edição de lembrete
    private void showEditReminderDialog(int position, Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Lembrete");

        // Adiciona um campo de texto para editar o lembrete
        final EditText input = new EditText(getContext());
        input.setText(reminder.getText());
        builder.setView(input);

        // Botão "Salvar" no diálogo
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newText = input.getText().toString();
            reminderList.get(position).setText(newText);
            reminderAdapter.notifyItemChanged(position);  // Atualiza o item no RecyclerView
        });

        // Botão "Cancelar" no diálogo
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Método para mostrar o diálogo de confirmação de exclusão de lembrete
    private void showDeleteConfirmationDialog(int position, Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Excluir Lembrete");
        builder.setMessage("Tem certeza que deseja excluir este lembrete?");

        // Botão "Excluir"
        builder.setPositiveButton("Excluir", (dialog, which) -> {
            reminderList.remove(position);
            reminderAdapter.notifyItemRemoved(position);  // Remove o item do RecyclerView
            Toast.makeText(getContext(), "Lembrete excluído", Toast.LENGTH_SHORT).show();
        });

        // Botão "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
