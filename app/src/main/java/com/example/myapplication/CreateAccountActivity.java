package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button createAccountButton;
    private ImageButton backButton; // Botão de voltar
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Vinculando os componentes do layout
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton); // Inicializa o botão de voltar

        // Inicializa FirebaseAuth e FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Listener do botão de criar conta
        createAccountButton.setOnClickListener(v -> createAccount());

        // Listener do botão de voltar
        backButton.setOnClickListener(v -> onBackPressed()); // Chama a ação padrão de voltar
    }

    private void createAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validação dos campos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criação de conta no Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        // Salvar informações adicionais no Firebase Database
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("uid", user.getUid());

                        // Salva o usuário no Firebase Realtime Database
                        databaseReference.child(user.getUid()).setValue(userMap)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(CreateAccountActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

                                        // Redirecionar para a LoginActivity
                                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish(); // Finaliza a CreateAccountActivity
                                    } else {
                                        Toast.makeText(CreateAccountActivity.this, "Falha ao salvar informações: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Tratamento específico para erro de e-mail já utilizado
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null && errorMessage.contains("The email address is already in use by another account.")) {
                                Toast.makeText(CreateAccountActivity.this, "Este e-mail já está em uso. Tente outro.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "Falha na criação da conta: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CreateAccountActivity.this, "Falha na criação da conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
