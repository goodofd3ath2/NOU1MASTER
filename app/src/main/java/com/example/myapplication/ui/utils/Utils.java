package com.example.myapplication.ui.utils;

import java.util.Calendar;

public class Utils {

    /**
     * Retorna o início do dia em milissegundos para a data especificada.
     *
     * @param year       Ano
     * @param month      Mês (0-based, janeiro = 0)
     * @param dayOfMonth Dia do mês
     * @return Início do dia em milissegundos
     */
    public static long getStartOfDayInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Retorna o fim do dia em milissegundos para a data especificada.
     *
     * @param year       Ano
     * @param month      Mês (0-based, janeiro = 0)
     * @param dayOfMonth Dia do mês
     * @return Fim do dia em milissegundos
     */
    public static long getEndOfDayInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
