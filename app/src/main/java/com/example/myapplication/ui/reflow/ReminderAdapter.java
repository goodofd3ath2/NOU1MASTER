package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;
    private OnReminderClickListener onReminderClickListener;

    public interface OnReminderClickListener {
        void onEdit(int position, Reminder reminder);
        void onDelete(int position, Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminders, OnReminderClickListener listener) {
        this.reminders = reminders;
        this.onReminderClickListener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.reminderText.setText(reminder.getText());

        Date date = new Date(reminder.getTimeInMillis());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        holder.reminderTime.setText(dateFormat.format(date));

        if (reminder.getRepeatInterval() > 0) {
            holder.reminderRepeat.setVisibility(View.VISIBLE);
            holder.reminderRepeat.setText(holder.itemView.getContext().getString(R.string.repeat_label, getRepeatLabel(reminder.getRepeatInterval())));
        } else {
            holder.reminderRepeat.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> onReminderClickListener.onEdit(position, reminder));
        holder.itemView.setOnLongClickListener(v -> {
            onReminderClickListener.onDelete(position, reminder);
            return true;
        });
    }

    private String getRepeatLabel(long repeatInterval) {
        if (repeatInterval == AlarmManager.INTERVAL_DAY) {
            return "Diariamente";
        } else if (repeatInterval == AlarmManager.INTERVAL_DAY * 7) {
            return "Semanalmente";
        }
        return "Sem repetição";
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderText, reminderTime, reminderRepeat;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderText = itemView.findViewById(R.id.reminderText);
            reminderTime = itemView.findViewById(R.id.reminderTime);
            reminderRepeat = itemView.findViewById(R.id.reminderRepeat);
        }
    }

    public void updateData(List<Reminder> newReminders) {
        this.reminders = newReminders;
        notifyDataSetChanged();
    }

    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
        notifyItemInserted(reminders.size() - 1);
    }
}
