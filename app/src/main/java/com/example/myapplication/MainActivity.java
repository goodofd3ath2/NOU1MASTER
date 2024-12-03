package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.myapplication.ui.database.Reminder;
import com.example.myapplication.ui.database.ReminderAdapter;
import com.example.myapplication.ui.database.ReminderDatabase;
import com.example.myapplication.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100; // Código de solicitação de permissão

    private DrawerLayout drawerLayout;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private ReminderDatabase reminderDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Solicitar permissão de leitura de armazenamento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configura o NavController com o BottomNavigationView
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_transform, R.id.nav_reflow, R.id.nav_settings).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Configura o DrawerLayout e o NavigationView
        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Inicializa o banco de dados
        reminderDatabase = Room.databaseBuilder(
                getApplicationContext(),
                ReminderDatabase.class,
                "reminder_database"
        ).build();

        // Configura o ActionBarDrawerToggle para sincronizar o ícone do menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializa o RecyclerView
        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura o Adapter com listeners para edição e exclusão
        reminderAdapter = new ReminderAdapter(new ArrayList<>(), new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onEdit(int position, Reminder reminder) {
                Log.d("MainActivity", "Edit reminder at position: " + position);
                // A lógica de edição pode ser implementada aqui
            }

            @Override
            public void onDelete(int position, Reminder reminder) {
                deleteReminder(position, reminder);
            }
        });
        reminderRecyclerView.setAdapter(reminderAdapter);

        // Carrega os lembretes do banco de dados
        loadReminders();

        // Configura a navegação no Drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            } else if (id == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
                return true;
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            }

            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
                // Chamar métodos que dependem da permissão aqui
                exportDatabase();
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadReminders() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Reminder> loadedReminders = reminderDatabase.reminderDao().getAllReminders();
                runOnUiThread(() -> reminderAdapter.setReminders(loadedReminders));
                exportDatabase();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao carregar lembretes.", Toast.LENGTH_SHORT).show());
                Log.e("MainActivity", "Error loading reminders: ", e);
            }
        });
    }

    private void deleteReminder(int position, Reminder reminder) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                reminderDatabase.reminderDao().deleteReminder(reminder);
                runOnUiThread(() -> {
                    reminderAdapter.removeReminder(position);
                    Toast.makeText(MainActivity.this, "Lembrete excluído", Toast.LENGTH_SHORT).show();
                    exportDatabase();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao excluir lembrete.", Toast.LENGTH_SHORT).show());
                Log.e("MainActivity", "Error deleting reminder: ", e);
            }
        });
    }

    private void exportDatabase() {
        try {
            File currentDB = getDatabasePath("reminder_database");
            File backupDB = new File(getExternalFilesDir(null), "reminder_database_backup.db");

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();

            Log.i("MainActivity", "Backup criado em: " + backupDB.getAbsolutePath());
        } catch (Exception e) {
            Log.e("MainActivity", "Erro ao exportar banco de dados", e);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logoutUser() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
