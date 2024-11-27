package com.example.myapplication.ui.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reminder {

    @PrimaryKey
    @NonNull
    private String id; // ID único gerado automaticamente
    private String text; // Texto do lembrete
    private long timeInMillis; // Tempo em milissegundos
    private String priority; // Prioridade do lembrete (Normal ou Importante)
    private long repeatInterval; // Intervalo de repetição (em milissegundos)
    private boolean notify; // Se deve notificar ou não

    // Construtor principal
    public Reminder(String text, long timeInMillis, String priority, boolean notify, long repeatInterval) {
        this.id = java.util.UUID.randomUUID().toString(); // Gera um UUID único
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
        this.repeatInterval = repeatInterval;
        this.notify = notify;
    }

    // Getters e Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
