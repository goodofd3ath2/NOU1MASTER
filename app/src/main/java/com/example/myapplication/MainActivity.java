package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ui.reflow.EditReminderDialogFragment;
import com.example.myapplication.ui.reflow.Reminder;
import com.example.myapplication.ui.reflow.ReminderDialogFragment;
import com.example.myapplication.ui.reflow.ReminderAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ReminderDialogFragment.OnReminderSavedListener,
        EditReminderDialogFragment.EditReminderDialogListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActivity";

    private TextView reminderTextView;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;

    private List<Reminder> reminderList;
    private int selectedPosition;
    private ReminderAdapter reminderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o layout
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Inicializar os elementos da interface
        reminderTextView = binding.reminderTextView;
        CalendarView calendarView = binding.calendarView;
        RecyclerView reminderListView = binding.reminderRecyclerView;


        // Verificar se o usuário selecionou "Me manter conectado"
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        boolean keepLoggedIn = preferences.getBoolean("keepLoggedIn", false);

        // Se o usuário não estiver logado e não escolheu "Me manter conectado", redireciona para o login
        if (!isLoggedIn && !keepLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Configurar o RecyclerView
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                // Abertura do diálogo de edição
                selectedPosition = position;
                EditReminderDialogFragment dialog = EditReminderDialogFragment.newInstance(reminder.getText());
                dialog.show(getSupportFragmentManager(), "EditReminderDialog");
            }
            @Override
            public void onDelete(int position, Reminder reminder) {
                // Remoção do lembrete
                reminderList.remove(position);
                reminderAdapter.notifyItemRemoved(position);
            }
        });


        RecyclerView recyclerView = binding.reminderRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reminderAdapter);

        // Carregar lembretes para o dia selecionado
        loadReminders();

        reminderListView.setLayoutManager(new LinearLayoutManager(this));
        reminderListView.setAdapter(reminderAdapter);

        // Listener de seleção de data no CalendarView
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedYear = year;
                selectedMonth = month;
                selectedDayOfMonth = dayOfMonth;

                // Carregar lembretes da data selecionada
                loadReminders();
            });
        }

        // Botão para adicionar lembretes
        Button addReminderButton = findViewById(R.id.addReminderButton);
        if (addReminderButton != null) {
            addReminderButton.setOnClickListener(v -> {
                ReminderDialogFragment dialog = ReminderDialogFragment.newInstance(selectedYear, selectedMonth, selectedDayOfMonth);
                dialog.show(getSupportFragmentManager(), "ReminderDialog");
            });
        }

        // Configurar navegação
        setupNavigationComponents(binding);

        // Carregar lembretes iniciais (se houver)
        loadReminders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);  // Inflando o menu de opções
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();  // Chama o método de logout
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Lógica de logout
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redireciona para a tela de login
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Finaliza a MainActivity para que o usuário não possa voltar sem fazer login
    }



    @Override
    public void onEdit(int position, Reminder reminder) {
        // Salvar a posição do lembrete clicado
        selectedPosition = position;

        // Criar uma instância do EditReminderDialogFragment, passando o texto do lembrete como argumento
        EditReminderDialogFragment dialog = EditReminderDialogFragment.newInstance(reminder.getText());

        // Mostrar o fragmento de diálogo de edição
        dialog.show(getSupportFragmentManager(), "EditReminderDialog");
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadReminders() {
        // Limpa a lista antes de carregar os lembretes
        reminderList.clear();

        // Exemplo de carregamento de lembretes usando SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE);
        String key = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
        String reminderText = sharedPreferences.getString(key, null);

        if (reminderText != null) {
            Reminder reminder = new Reminder(reminderText, System.currentTimeMillis(), "Normal", 0);
            reminderList.add(reminder);
        }

        // Notifica o adaptador sobre as mudanças na lista
        reminderAdapter.notifyDataSetChanged();
    }

    // Métodos auxiliares de navegação
    private void setupNavigationComponents(ActivityMainBinding binding) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            NavigationView navigationView = binding.navView;
            BottomNavigationView bottomNavigationView = binding.bottomNavView;

            if (navigationView != null) {
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings)
                        .setOpenableLayout(binding.drawerLayout)
                        .build();
                NavigationUI.setupWithNavController(navigationView, navController);
            }

            if (bottomNavigationView != null) {
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
            }
        } else {
            Toast.makeText(this, "NavHostFragment não encontrado!", Toast.LENGTH_SHORT).show();
        }
    }

    // Salvar lembretes
    @Override
    public void onReminderSaved(int year, int month, int dayOfMonth, String reminder, boolean notify, String priority) {
        SharedPreferences sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = year + "-" + (month + 1) + "-" + dayOfMonth;

        if (reminder != null) {
            editor.putString(key, reminder);
            editor.apply();
            loadReminders();
        } else {
            Log.e(TAG, "Lembrete é nulo, não pode ser salvo.");
        }
    }

    @Override
    public void onReminderUpdated(String updatedReminderText) {

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
