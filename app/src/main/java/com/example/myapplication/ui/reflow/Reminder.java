package com.example.myapplication.ui.reflow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Reminder {
    private String id; // ID único para cada lembrete
    private String text; // Texto do lembrete
    private long timeInMillis; // Tempo em milissegundos
    private String priority; // Prioridade do lembrete (ex: Alta, Normal, Baixa)
    private long repeatInterval; // Intervalo de repetição (diário, semanal, etc.)

    // Construtor
    public Reminder(String text, long timeInMillis, String priority, long repeatInterval) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
        this.repeatInterval = repeatInterval;
    }

    // Construtor a partir de JSONObject
    public Reminder(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.optString("id", UUID.randomUUID().toString()); // Gera ID caso não esteja presente
        this.text = jsonObject.getString("text");
        this.timeInMillis = jsonObject.getLong("timeInMillis");
        this.priority = jsonObject.getString("priority");
        this.repeatInterval = jsonObject.optLong("repeatInterval", 0);  // Padrão é 0 caso não esteja presente
    }

    // Getters e Setters

    // Retorna o ID do lembrete
    public String getId() {
        return id;
    }

    // Define um ID (usado principalmente para persistência e compatibilidade)
    public void setId(String id) {
        this.id = id;
    }

    // Retorna o texto do lembrete
    public String getText() {
        return text;
    }

    // Define o texto do lembrete
    public void setText(String text) {
        this.text = text;
    }

    // Retorna o tempo do lembrete em milissegundos
    public long getTimeInMillis() {
        return timeInMillis;
    }

    // Retorna a prioridade do lembrete
    public String getPriority() {
        return priority;
    }

    // Retorna o intervalo de repetição (em milissegundos)
    public long getRepeatInterval() {
        return repeatInterval;
    }

    // Métodos para conversão JSON

    // Converte o objeto Reminder para JSONObject
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id); // ID único
            jsonObject.put("text", text); // Texto do lembrete
            jsonObject.put("timeInMillis", timeInMillis); // Data/hora do lembrete
            jsonObject.put("priority", priority); // Prioridade do lembrete
            jsonObject.put("repeatInterval", repeatInterval); // Intervalo de repetição
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Cria um objeto Reminder a partir de um JSONObject
    public static Reminder fromJson(JSONObject jsonObject) throws JSONException {
        String text = jsonObject.getString("text");
        long timeInMillis = jsonObject.getLong("timeInMillis");
        String priority = jsonObject.getString("priority");
        long repeatInterval = jsonObject.optLong("repeatInterval", 0); // Intervalo de repetição (padrão é 0)
        return new Reminder(text, timeInMillis, priority, repeatInterval);
    }
}
