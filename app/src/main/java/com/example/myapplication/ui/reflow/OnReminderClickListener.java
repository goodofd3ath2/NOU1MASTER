// Arquivo separado: OnReminderClickListener.java
package com.example.myapplication.ui.reflow;

import com.example.myapplication.ui.database.Reminder;

// Interface funcional com um único método abstrato
public interface OnReminderClickListener {
    void onReminderClick(Reminder reminder);
}

