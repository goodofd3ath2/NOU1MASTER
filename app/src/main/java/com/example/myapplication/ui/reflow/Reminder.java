package com.example.myapplication.ui.reflow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Reminder {

    private String id; // Unique ID for each reminder
    private String text; // Reminder text
    private long timeInMillis; // Time in milliseconds
    private String priority; // Reminder priority (e.g., High, Normal, Low)
    private long repeatInterval; // Repeat interval (daily, weekly, etc.)

    // Constructor
    public Reminder(String text, long timeInMillis, String priority, long repeatInterval) {
        this.id = UUID.randomUUID().toString(); // Generates a unique ID
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
        this.repeatInterval = repeatInterval;
    }

    // Constructor from JSONObject
    public Reminder(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.optString("id", UUID.randomUUID().toString()); // Generates ID if not present
        this.text = jsonObject.getString("text");
        this.timeInMillis = jsonObject.getLong("timeInMillis");
        this.priority = jsonObject.getString("priority");
        this.repeatInterval = jsonObject.optLong("repeatInterval", 0); // Default is 0 if not present
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPriority() {
        return priority;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    // JSON conversion methods
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id); // Unique ID
            jsonObject.put("text", text); // Reminder text
            jsonObject.put("timeInMillis", timeInMillis); // Reminder date/time
            jsonObject.put("priority", priority); // Reminder priority
            jsonObject.put("repeatInterval", repeatInterval); // Repeat interval
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Reminder fromJson(JSONObject jsonObject) throws JSONException {
        String text = jsonObject.getString("text");
        long timeInMillis = jsonObject.getLong("timeInMillis");
        String priority = jsonObject.getString("priority");
        long repeatInterval = jsonObject.optLong("repeatInterval", 0); // Default repeat interval is 0
        return new Reminder(text, timeInMillis, priority, repeatInterval);
    }
}
