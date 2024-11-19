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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList = new ArrayList<>();
    private OnReminderClickListener listener;

    public ReminderAdapter() {
        this.listener = listener;
    }

    public interface OnReminderClickListener {
        void onEdit(int position, Reminder reminder);
        void onDelete(int position, Reminder reminder);
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
        holder.reminderTime.setText(formatTime(reminder.getTimeInMillis()));

        if (reminder.getRepeatInterval() > 0) {
            holder.reminderRepeat.setVisibility(View.VISIBLE);
            holder.reminderRepeat.setText(getRepeatLabel(reminder.getRepeatInterval()));
        } else {
            holder.reminderRepeat.setVisibility(View.GONE);
        }

        // Configura cliques de edição e exclusão
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(position, reminder);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDelete(position, reminder);
            return true;
        });
    }

    private String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timeInMillis));
    }

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

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView reminderText;
        public TextView reminderTime;
        public TextView reminderRepeat;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderText = itemView.findViewById(R.id.reminderText);
            reminderTime = itemView.findViewById(R.id.reminderTime);
            reminderRepeat = itemView.findViewById(R.id.reminderRepeat);
        }
    }

    // Remove um lembrete
    public void removeReminder(int position) {
        if (position >= 0 && position < reminderList.size()) {
            reminderList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Atualiza a lista de lembretes
    public void setReminders(List<Reminder> reminders) {
        this.reminderList = reminders;
        notifyDataSetChanged();
    }

    // Adiciona um novo lembrete
    public void addReminder(Reminder reminder) {
        this.reminderList.add(reminder);
        notifyItemInserted(reminderList.size() - 1);
    }
}
