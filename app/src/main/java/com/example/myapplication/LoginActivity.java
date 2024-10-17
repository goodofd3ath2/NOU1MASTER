package com.example.myapplication;

import android.content.Intent; // Import necessário para navegação
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView forgotPasswordLink;
    private RequestQueue requestQueue; // Fila de requisições Volley

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

        // Ação do botão de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Lógica de autenticação com servidor
                    authenticateUser(username, password);
                }
            }
        });

        // Ação do link "Esqueceu a senha"
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de recuperação de senha
                Toast.makeText(LoginActivity.this, "Recuperação de senha", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Função de autenticação usando Volley
    private void authenticateUser(final String username, final String password) {
        String url = "https://seu-servidor.com/api/login"; // Substitua com o URL da sua API

        // Criar o JSON que será enviado na requisição
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Criar uma requisição POST para autenticar o usuário
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();

                                // Navegar para a tela principal após o login bem-sucedido
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Substitua "MainActivity" com a Activity da sua tela principal
                                startActivity(intent);
                                finish(); // Finaliza a LoginActivity para que o usuário não possa voltar para ela com o botão de voltar
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
                        Toast.makeText(LoginActivity.this, "Erro na comunicação com o servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Adicionar a requisição à fila de requisições do Volley
        requestQueue.add(loginRequest);
    }
}
