package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.myapplication.ui.reflow.Reminder;
import com.example.myapplication.ui.reflow.ReminderAdapter;
import com.example.myapplication.ui.reflow.ReminderDao;
import com.example.myapplication.ui.reflow.ReminderDatabase;
import com.example.myapplication.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private ReminderDatabase reminderDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura o DrawerLayout e o NavigationView
        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Inicializa o banco de dados
        reminderDatabase = Room.databaseBuilder(getApplicationContext(),
                ReminderDatabase.class, "reminder_database").build();
        ReminderDao reminderDao = reminderDatabase.reminderDao();
        // Configura o ActionBarDrawerToggle para sincronizar o ícone do menu (hamburger)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();  // Sincroniza o estado do ícone do menu

        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        // Initialize adapter
        reminderAdapter = new ReminderAdapter();
        reminderRecyclerView.setAdapter(reminderAdapter);

        // Load reminders from database
        loadReminders();

        // Configura a navegação no Drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                // Navegar para a Activity de perfil
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            } else if (id == R.id.nav_settings) {
                // Substituir o fragmento pelo SettingsFragment
                loadFragment(new SettingsFragment());
                return true;
            } else if (id == R.id.nav_logout) {
                // Executar logout e redirecionar para a tela de login
                logoutUser();
                return true;
            }

            return false;
        });
    }

    private void loadReminders() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Verifica se o banco de dados foi inicializado
                if (reminderDatabase != null) {
                    // Carregar lembretes do banco de dados
                    List<Reminder> loadedReminders = reminderDatabase.reminderDao().getReminders();


                    // Atualiza o RecyclerView na thread principal
                    runOnUiThread(() -> reminderAdapter.setReminders(loadedReminders));
                } else {
                    Log.e("MainActivity", "Reminder database is not initialized.");
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error loading reminders: ", e);
            }
        });


    }


    // Método para carregar um Fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, fragment);
        transaction.addToBackStack(null);  // Permite voltar ao fragmento anterior com o botão "Voltar"
        transaction.commit();
    }

    // Método para logout
    private void logoutUser() {
        // Limpar as preferências do usuário (sessão) e redirecionar para a tela de login
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Redirecionar para a LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Finaliza a MainActivity
    }

    @Override
    public void onBackPressed() {
        // Fecha o Drawer se estiver aberto, caso contrário, permite que o back funcione normalmente
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
