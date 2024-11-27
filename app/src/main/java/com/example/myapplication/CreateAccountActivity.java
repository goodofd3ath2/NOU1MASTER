package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
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

        // Vinculando os componentes do layout
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);

        // Inicializa o banco de dados e o DAO
        ReminderDatabase database = Room.databaseBuilder(
                getApplicationContext(),
                ReminderDatabase.class,
                "reminder_database"
        ).build();
        userDao = database.userDao();

        // Listener do botão de criar conta
        createAccountButton.setOnClickListener(v -> createAccount(database));

        // Listener do botão de voltar
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void createAccount(ReminderDatabase database) {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validação dos campos
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

        // Exibe um ProgressDialog enquanto a conta está sendo criada
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Criando conta...");
        progressDialog.show();

        new Thread(() -> {
            // Verifica se o e-mail já está cadastrado
            User existingUser = userDao.getUserByEmail(email);

            if (existingUser != null) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "E-mail já cadastrado!", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Insere o novo usuário no banco de dados
            User newUser = new User(name, email, password);
            userDao.insertUser(newUser);

            // Atualiza a UI após o sucesso
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
