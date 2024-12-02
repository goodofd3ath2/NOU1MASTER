package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.database.User;
import com.example.myapplication.ui.database.ReminderDatabase;
import com.example.myapplication.ui.database.UserDao;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button createAccountButton;
    private ImageButton backButton;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);

        ReminderDatabase database = Room.databaseBuilder(
                getApplicationContext(),
                ReminderDatabase.class,
                "reminder_database"
        ).build();
        userDao = database.userDao();

        createAccountButton.setOnClickListener(v -> createAccount());
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void createAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validações básicas
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
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

        // Exibe um progresso enquanto a conta está sendo criada
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Criando conta...");
        progressDialog.show();

        // Operação em Thread separada
        new Thread(() -> {
            try {
                UserDao userDao = ReminderDatabase.getInstance(getApplicationContext()).userDao();

                // Verifica se o e-mail já está cadastrado
                Log.d("DatabaseDebug", "Checking user existence for email: " + email);
                User existingUser = userDao.getUserByEmail(email);
                Log.d("DatabaseDebug", "Existing user: " + (existingUser != null));

                if (existingUser != null) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "E-mail já cadastrado!", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Cria o novo usuário
                User newUser = new User(email, password, name);
                Log.d("DatabaseDebug", "Inserting new user: " + newUser);
                userDao.insertUser(newUser);

                // Valida a inserção
                User insertedUser = userDao.getUserByEmail(email);
                Log.d("DatabaseDebug", "Inserted user: " + (insertedUser != null));

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (insertedUser != null) {
                        Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a Activity e retorna
                    } else {
                        Toast.makeText(this, "Erro ao salvar a conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("DatabaseDebug", "Error during account creation: ", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}