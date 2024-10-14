package com.example.myapplication.ui.reflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        // Inicializa o RecyclerView e o adapter
        reminderRecyclerView = rootView.findViewById(R.id.reminderRecyclerView);
        addReminderButton = rootView.findViewById(R.id.addReminderButton);  // Adiciona o botão para adicionar lembretes
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
        // Configura o LayoutManager e o Adapter do RecyclerView
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                // Lógica para editar o lembrete
                Toast.makeText(getContext(), "Editar lembrete: " + reminder.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(int position, Reminder reminder) {
                reminderList.remove(position);  // Remove o lembrete da lista
                reminderAdapter.notifyItemRemoved(position);  // Notifica o RecyclerView que o item foi removido
                Toast.makeText(getContext(), "Lembrete excluído: " + reminder.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        // Associa o adapter ao RecyclerView
        reminderRecyclerView.setAdapter(reminderAdapter);
    }
}
