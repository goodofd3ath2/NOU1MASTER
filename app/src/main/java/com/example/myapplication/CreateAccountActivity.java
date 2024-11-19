package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button createAccountButton;
    private ImageButton backButton;
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
        backButton = findViewById(R.id.backButton);

        // Inicializa FirebaseAuth e FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Listener do botão de criar conta
        createAccountButton.setOnClickListener(v -> createAccount());

        // Listener do botão de voltar
        backButton.setOnClickListener(v -> onBackPressed());
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
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Insira um e-mail válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Criando conta...");
        progressDialog.show();

        // Criação de conta no Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            // Salvar informações adicionais no Firebase Database
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("created_at", System.currentTimeMillis());

                            databaseReference.child(user.getUid()).setValue(userMap)
                                    .addOnCompleteListener(dbTask -> {
                                        progressDialog.dismiss();
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(CreateAccountActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(CreateAccountActivity.this, "Falha ao salvar informações: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        if (task.getException() != null) {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_WEAK_PASSWORD":
                                    Toast.makeText(this, "A senha é muito fraca.", Toast.LENGTH_SHORT).show();
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    Toast.makeText(this, "E-mail inválido.", Toast.LENGTH_SHORT).show();
                                    break;
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    Toast.makeText(this, "Este e-mail já está em uso.", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(this, "Erro: " + errorCode, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Falha na criação da conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
