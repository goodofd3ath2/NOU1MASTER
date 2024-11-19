package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginFirebaseButton, loginApiButton, signUpButton;
    private FirebaseAuth auth;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vinculando os componentes do layout
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginFirebaseButton = findViewById(R.id.loginFirebaseButton);
        loginApiButton = findViewById(R.id.loginApiButton);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            // Redireciona para a tela de criação de conta
            startActivity(new Intent(this, CreateAccountActivity.class));
        });

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Inicializa Volley
        requestQueue = Volley.newRequestQueue(this);

        // Listener para login com Firebase
        loginFirebaseButton.setOnClickListener(v -> loginWithFirebase());

        // Listener para login com API
        loginApiButton.setOnClickListener(v -> loginWithApi());

        // Redireciona para a tela de criação de conta
        signUpButton.setOnClickListener(v -> startActivity(new Intent(this, CreateAccountActivity.class)));
    }

    private void loginWithFirebase() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(this, "Bem-vindo, " + (user != null ? user.getEmail() : ""), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Falha no login: " + (task.getException() != null ? task.getException().getMessage() : "Erro desconhecido"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithApi() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://seu-servidor.com/api/login"; // Substitua pela URL correta da API

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("email", email);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("LoginError", "Status code: " + statusCode + ", Response body: " + responseBody);
                    }
                    Toast.makeText(LoginActivity.this, "Erro na comunicação com o servidor", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(loginRequest);
    }
}
