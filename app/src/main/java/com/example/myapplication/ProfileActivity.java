package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.ui.database.ReminderDatabase;
import com.example.myapplication.database.User;

import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameText, userEmailText;
    private Button changePasswordButton;

    private ReminderDatabase database;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Vincula os componentes do layout
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Inicializa o banco de dados
        database = Room.databaseBuilder(
                getApplicationContext(),
                ReminderDatabase.class,
                "reminder_database"
        ).build();

        // Obtém os dados do usuário logado
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Nome não definido");
        loggedInUserEmail = sharedPreferences.getString("userEmail", "Email não definido");

        // Atualiza as informações do perfil na interface
        userNameText.setText(userName);
        userEmailText.setText(loggedInUserEmail);

        // Listener para o botão de mudar senha
        changePasswordButton.setOnClickListener(v -> openChangePasswordDialog());
    }

    /**
     * Abre o diálogo para alteração de senha.
     */
    private void openChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Senha");

        // Campo de entrada para a nova senha
        final EditText input = new EditText(this);
        input.setHint("Nova senha");
        builder.setView(input);

        // Botão "Salvar"
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newPassword = input.getText().toString().trim();
            if (newPassword.isEmpty() || newPassword.length() < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(newPassword);
            }
        });

        // Botão "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Atualiza a senha do usuário no banco de dados.
     *
     * @param newPassword A nova senha a ser configurada.
     */
    private void updatePassword(String newPassword) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Obtém o usuário logado do banco de dados
                User currentUser = database.userDao().getUserByEmail(loggedInUserEmail);

                if (currentUser != null) {
                    // Atualiza a senha
                    currentUser.setPassword(newPassword);
                    database.userDao().updateUser(currentUser);

                    runOnUiThread(() -> Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Usuário não encontrado. Tente novamente.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao atualizar a senha.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
            }
        });
    }
}
