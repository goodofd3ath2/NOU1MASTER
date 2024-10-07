package com.example.myapplication.ui.reflow;

import org.json.JSONException;
import org.json.JSONObject;

public class Reminder {
    private String text;          // Text of the reminder
    private long timeInMillis;    // Store time in milliseconds
    private int priority;         // Priority of the reminder

    // Constructor with all parameters
    public Reminder(String text, long timeInMillis, int priority) {
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
    }

    // Constructor with only text (default values for time and priority)
    public Reminder(String text) {
        this.text = text;
        this.timeInMillis = System.currentTimeMillis(); // Default to current time
        this.priority = 0; // Default priority
    }

    // Constructor from JSON object
    public Reminder(JSONObject jsonObject) throws JSONException {
        this.text = jsonObject.getString("text");
        this.timeInMillis = jsonObject.getLong("timeInMillis");
        this.priority = jsonObject.getInt("priority");
    }

    // Getters
    public String getText() {
        return text;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public int getPriority() {
        return priority;
    }

    // Method to create a Reminder from a JSON object
    public static Reminder fromJson(JSONObject jsonObject) {
        try {
            return new Reminder(jsonObject); // Use the constructor that accepts a JSONObject
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        }
    }

    // Method to convert Reminder object to JSON
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", text);
        jsonObject.put("timeInMillis", timeInMillis);
        jsonObject.put("priority", priority);
        return jsonObject;
    }
}
