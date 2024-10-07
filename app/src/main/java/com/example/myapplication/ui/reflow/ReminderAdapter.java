package com.example.myapplication.ui.reflow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private final OnReminderClickListener listener;

    public interface OnReminderClickListener {
        void onReminderClick(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminders, OnReminderClickListener listener) {
        this.reminders = reminders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder, listener);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void updateData(List<Reminder> newReminders) {
        reminders = newReminders;
        notifyDataSetChanged();
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        notifyItemInserted(reminders.size() - 1);
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {

        private final TextView reminderTextView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderTextView = itemView.findViewById(R.id.reminderTextView);
        }

        public void bind(Reminder reminder, OnReminderClickListener listener) {
            reminderTextView.setText(reminder.getText() + " (Prioridade: " + reminder.getPriority() + ")");
            itemView.setOnClickListener(v -> listener.onReminderClick(reminder));
        }
    }
}
