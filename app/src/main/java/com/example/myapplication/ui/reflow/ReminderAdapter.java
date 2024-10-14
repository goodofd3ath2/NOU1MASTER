package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private OnReminderClickListener listener;

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

        // Define o texto do lembrete
        holder.reminderText.setText(reminder.getText());

        // Define o horário do lembrete (se houver)
        holder.reminderTime.setText(formatTime(reminder.getTimeInMillis()));  // Usa um método para formatar a hora do lembrete

        // Se o lembrete for repetido, exibe o texto de repetição
        if (reminder.getRepeatInterval() > 0) {
            holder.reminderRepeat.setVisibility(View.VISIBLE);
            holder.reminderRepeat.setText(getRepeatLabel(reminder.getRepeatInterval()));
        } else {
            holder.reminderRepeat.setVisibility(View.GONE);  // Esconde o campo se não houver repetição
        }

        // Configura os cliques para editar ou excluir, como já configurado anteriormente
        holder.itemView.setOnClickListener(v -> listener.onEdit(position, reminder));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(position, reminder);
            return true;
        });
    }

    // Método para formatar a hora do lembrete
    private String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timeInMillis));
    }

    // Método para determinar o rótulo de repetição
    private String getRepeatLabel(long repeatInterval) {
        if (repeatInterval == AlarmManager.INTERVAL_DAY) {
            return "Diariamente";
        } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 7) {
            return "Semanalmente";
        } else {
            return "Sem repetição";
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // Classe ViewHolder dentro do adaptador
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView reminderText;
        public TextView reminderTime;
        public TextView reminderRepeat;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderText = itemView.findViewById(R.id.reminderText);  // Certifique-se de que o ID está correto no layout
            reminderTime = itemView.findViewById(R.id.reminderTime);  // Agora o reminderTime está vinculado
            reminderRepeat = itemView.findViewById(R.id.reminderRepeat);  // Para exibir a repetição, se houver
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
}
