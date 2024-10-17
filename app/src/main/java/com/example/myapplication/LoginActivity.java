package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView forgotPasswordLink;
    private RequestQueue requestQueue; // Fila de requisições Volley
    private LoginViewModel loginViewModel; // ViewModel para gerenciar o estado do login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referenciando os componentes do layout
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        // Inicializando a fila de requisições do Volley
        requestQueue = Volley.newRequestQueue(this);

        // Inicializando o ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observando o resultado do login no ViewModel
        loginViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                // Redireciona para a tela principal se o login foi bem-sucedido
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Exibe uma mensagem de erro se o login falhar
                Toast.makeText(LoginActivity.this, "Falha na autenticação. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });

        // Ação do botão de login
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                // Autenticação usando a função com Volley
                authenticateUser(username, password);
            }
        });

        // Ação do link "Esqueceu a senha"
        forgotPasswordLink.setOnClickListener(v -> {
            // Exibe uma mensagem e navega para a tela de recuperação de senha
            Toast.makeText(LoginActivity.this, "Recuperação de senha", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)); // Descomente para usar a tela de recuperação
        });
    }

    // Método para autenticar o usuário
    private void authenticateUser(final String username, final String password) {
        String url = "https://seu-servidor.com/api/login";  // Substitua pela URL correta da API

        // Criação do JSON de dados para a requisição
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Criação da requisição POST com Volley
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                // Processar login bem-sucedido
                                loginViewModel.loginSuccess();
                            } else {
                                Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tratar erros da requisição
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("LoginError", "Status code: " + statusCode + ", Response body: " + responseBody);
                        }
                        Toast.makeText(LoginActivity.this, "Erro na comunicação com o servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Adicionar a requisição à fila de requisições
        requestQueue.add(loginRequest);
    }
}
