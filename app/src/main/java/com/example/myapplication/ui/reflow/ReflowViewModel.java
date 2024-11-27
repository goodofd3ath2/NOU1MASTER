package com.example.myapplication.ui.reflow;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.database.Reminder;
import com.example.myapplication.ui.database.ReminderDao;
import com.example.myapplication.ui.database.ReminderDatabase;
import com.example.myapplication.ui.utils.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReflowViewModel extends ViewModel {

    private final ReminderDao reminderDao;
    private final ExecutorService executorService;

    public ReflowViewModel(Context context) {
        // Inicializa o ReminderDao usando o banco de dados
        ReminderDatabase database = ReminderDatabase.getInstance(context);
        reminderDao = database.reminderDao();

        // Executor para operações assíncronas
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Insere um lembrete no banco de dados.
     *
     * @param reminder O lembrete a ser inserido
     */
    public void insertReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.insertReminder(reminder));
    }

    /**
     * Carrega lembretes para uma data específica.
     *
     * @param year        Ano
     * @param month       Mês (0-based, janeiro = 0)
     * @param dayOfMonth  Dia do mês
     * @return LiveData contendo a lista de lembretes
     */
    public LiveData<List<Reminder>> loadReminders(int year, int month, int dayOfMonth) {
        long startOfDay = Utils.getStartOfDayInMillis(year, month, dayOfMonth);
        long endOfDay = Utils.getEndOfDayInMillis(year, month, dayOfMonth);

        return reminderDao.getRemindersByTimeRange(startOfDay, endOfDay);
    }

    /**
     * Deleta um lembrete específico.
     *
     * @param reminder Lembrete a ser deletado
     */
    public void deleteReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.deleteReminder(reminder));
    }

    /**
     * Atualiza um lembrete no banco de dados.
     *
     * @param reminder Lembrete a ser atualizado
     */
    public void updateReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.updateReminder(reminder));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
