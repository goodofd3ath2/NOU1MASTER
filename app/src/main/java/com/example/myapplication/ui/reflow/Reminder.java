package com.example.myapplication.ui.reflow;

import org.json.JSONException;
import org.json.JSONObject;

public class Reminder {
    private String text;
    private long timeInMillis;
    private String priority;
    private long repeatInterval; // Intervalo de repetição

    // Construtor
    public Reminder(String text, long timeInMillis, String priority, long repeatInterval) {
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
        this.repeatInterval = repeatInterval;
    }

    // Constructor from JSONObject
    public Reminder(JSONObject jsonObject) throws JSONException {
        this.text = jsonObject.getString("text");
        this.timeInMillis = jsonObject.getLong("timeInMillis");
        this.priority = jsonObject.getString("priority");
        this.repeatInterval = jsonObject.optLong("repeatInterval", 0);  // Default to 0 if not present
    }

    // Getter for reminder text
    public String getText() {
        return text;
    }

    // Getter for time in milliseconds
    public long getTimeInMillis() {
        return timeInMillis;
    }

    // Getter for priority
    public String getPriority() {
        return priority;
    }

    // Getter for repeat interval
    public long getRepeatInterval() {
        return repeatInterval;
    }

    // Convert to JSONObject
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("timeInMillis", timeInMillis);
            jsonObject.put("priority", priority);
            jsonObject.put("repeatInterval", repeatInterval);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Convert from JSON
    public static Reminder fromJson(JSONObject jsonObject) throws JSONException {
        String text = jsonObject.getString("text");
        long timeInMillis = jsonObject.getLong("timeInMillis");
        String priority = jsonObject.getString("priority");
        long repeatInterval = jsonObject.optLong("repeatInterval", 0);  // Handle missing repeatInterval
        return new Reminder(text, timeInMillis, priority, repeatInterval);
    }
}
