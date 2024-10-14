package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private OnReminderClickListener listener;

    // Interface para os cliques de edição e exclusão
    public interface OnReminderClickListener {
        void onEdit(int position, Reminder reminder);
        void onDelete(int position, Reminder reminder);
    }

    // Construtor do adaptador
    public ReminderAdapter(List<Reminder> reminderList, OnReminderClickListener listener) {
        this.reminderList = reminderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.reminderText.setText(reminder.getText());

        // Configura os cliques para editar o lembrete
        holder.itemView.setOnClickListener(v -> listener.onEdit(position, reminder));

        // Configura os cliques longos para excluir o lembrete
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(position, reminder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // ViewHolder para o RecyclerView
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView reminderText;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderText = itemView.findViewById(R.id.reminderText);  // Certifique-se de que o ID está correto no layout
        }
    }

    // Remove um lembrete e atualiza o RecyclerView
    public void removeReminder(int position) {
        if (position >= 0 && position < reminderList.size()) {
            reminderList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Atualizar a lista completa de lembretes
    public void updateData(List<Reminder> newReminders) {
        this.reminderList = newReminders;
        notifyDataSetChanged();
    }

    // Adicionar um novo lembrete e notificar o RecyclerView
    public void addReminder(Reminder reminder) {
        this.reminderList.add(reminder);
        notifyItemInserted(reminderList.size() - 1);
    }

    // Lógica para exibir o label de repetição (diário/semanal)
    private String getRepeatLabel(long repeatInterval) {
        if (repeatInterval == AlarmManager.INTERVAL_DAY) {
            return "Diariamente";
        } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 7) {
            return "Semanalmente";
        }
        return "Sem repetição";
    }
}
