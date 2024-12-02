package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ui.database.ReminderDatabase;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configuração do ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Inicializando o UserDao no ViewModel
        ReminderDatabase database = ReminderDatabase.getInstance(getApplicationContext());
        loginViewModel.initUserDao(database);

        // Referências aos campos e botão da interface
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Observando o estado de login
        loginViewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                Log.d("LoginActivity", "Navegando para a próxima tela após login bem-sucedido");
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                navigateToNextScreen();
            }
        });

        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Log.d("LoginActivity", "Erro no login: " + error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Configuração do botão de login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.login(email, password);
        });
    }

    // Método para redirecionar à próxima tela
    private void navigateToNextScreen() {
        Intent intent = new Intent(this, MainActivity.class); // Substitua 'MainActivity' pela sua atividade principal.
        startActivity(intent);
        finish(); // Finaliza a tela de login.
    }
}
