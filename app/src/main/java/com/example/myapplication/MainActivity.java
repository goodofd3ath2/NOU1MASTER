package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ui.reflow.EditReminderDialogFragment;
import com.example.myapplication.ui.reflow.ReminderDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        ReminderDialogFragment.OnReminderSavedListener,
        EditReminderDialogFragment.EditReminderDialogListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActivity";

    private TextView reminderTextView;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Configura a toolbar a partir do binding
        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);
        } else {
            Log.e(TAG, "Toolbar is null");
        }

        // Verifica se o usuário está autenticado
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Certifique-se de que você está retornando aqui
        }

        // Inicializa o CalendarView e TextView para lembretes
        CalendarView calendarView = binding.calendarView;
        reminderTextView = binding.reminderTextView;

        // Configura o listener para a seleção de data
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedYear = year;
                selectedMonth = month;
                selectedDayOfMonth = dayOfMonth;

                loadReminders(); // Carrega lembretes para a data selecionada

                ReminderDialogFragment dialog = new ReminderDialogFragment(year, month, dayOfMonth);
                dialog.show(getSupportFragmentManager(), "ReminderDialog");
            });
        }

        // Configura os componentes de navegação (Drawer, Bottom Navigation, etc.)
        setupNavigationComponents(binding);
    }

    private void setupNavigationComponents(ActivityMainBinding binding) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        // Verifica se o NavHostFragment não é nulo
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            Log.d(TAG, "NavController obtained: " + navController); // Aqui está a verificação do NavController

            NavigationView navigationView = binding.navView;

            if (navigationView != null) {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings)
                        .setOpenableLayout(binding.drawerLayout) // Certifique-se de que o ID está correto
                        .build();
                NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
            }

            BottomNavigationView bottomNavigationView = null;
            if (binding.appBarMain != null) {
                bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
            }
            if (bottomNavigationView != null) {
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
            }
        } else {
            Toast.makeText(this, "NavHostFragment not found!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onReminderSaved(int year, int month, int dayOfMonth, String reminder, boolean notify, String priority) {
        Log.d(TAG, "Reminder saved for " + dayOfMonth + "/" + (month + 1) + "/" + year + ": " + reminder);
        Toast.makeText(this, "Reminder saved for " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_LONG).show();

        // Salvar lembrete em SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = year + "-" + (month + 1) + "-" + dayOfMonth; // Chave única para cada data

        if (reminder != null) {
            editor.putString(key, reminder);
            editor.apply();
        } else {
            Log.e(TAG, "Reminder is null, not saving.");
        }

        loadReminders(); // Carrega lembretes após salvar
    }

    @SuppressLint("SetTextI18n")
    private void loadReminders() {
        SharedPreferences sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE);
        String key = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth; // Chave correta para a data selecionada
        String reminder = sharedPreferences.getString(key, null);

        if (reminder != null) {
            reminderTextView.setText(reminder);
        } else {
            reminderTextView.setText("No reminders found.");
        }
    }

    @Override
    public void onReminderUpdated(String updatedReminder) {
        Log.d(TAG, "Updated Reminder: " + updatedReminder);
        Toast.makeText(this, "Reminder updated to: " + updatedReminder, Toast.LENGTH_SHORT).show();
        // Lidar com o lembrete atualizado (ex: salvá-lo novamente ou atualizar a UI)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        if (item.getItemId() == R.id.nav_settings) {
            navController.navigate(R.id.nav_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}