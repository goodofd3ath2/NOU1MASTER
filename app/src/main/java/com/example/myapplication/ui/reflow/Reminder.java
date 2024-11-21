package com.example.myapplication.ui.reflow;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Reminder {

    @PrimaryKey
    @NonNull
    private String id;
    private String text;
    private long timeInMillis;
    private String priority;
    private long repeatInterval;

    public Reminder(String text, long timeInMillis, String priority, long repeatInterval) {
        this.id = java.util.UUID.randomUUID().toString();
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.priority = priority;
        this.repeatInterval = repeatInterval;
    }

    public Reminder(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.optString("id", java.util.UUID.randomUUID().toString());
        this.text = jsonObject.getString("text");
        this.timeInMillis = jsonObject.getLong("timeInMillis");
        this.priority = jsonObject.getString("priority");
        this.repeatInterval = jsonObject.optLong("repeatInterval", 0);
    }

    public static Reminder fromJson(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.optString("id", java.util.UUID.randomUUID().toString());
        String text = jsonObject.getString("text");
        long timeInMillis = jsonObject.getLong("timeInMillis");
        String priority = jsonObject.getString("priority");
        long repeatInterval = jsonObject.optLong("repeatInterval", 0);

        Reminder reminder = new Reminder(text, timeInMillis, priority, repeatInterval);
        reminder.setId(id);
        return reminder;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("text", text);
            jsonObject.put("timeInMillis", timeInMillis);
            jsonObject.put("priority", priority);
            jsonObject.put("repeatInterval", repeatInterval);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

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
}
