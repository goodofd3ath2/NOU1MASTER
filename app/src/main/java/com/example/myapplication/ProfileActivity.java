package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameText, userEmailText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Vincula as views
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Obtém os dados do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Nome não definido");
        String userEmail = sharedPreferences.getString("userEmail", "Email não definido");

        // Exibe os dados
        userNameText.setText(userName);
        userEmailText.setText(userEmail);

        // Configura o clique no botão de mudar senha
        changePasswordButton.setOnClickListener(v -> openChangePasswordDialog());
    }

    private void openChangePasswordDialog() {
        // Abre um diálogo para alterar a senha
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Senha");

        final EditText input = new EditText(this);
        input.setHint("Nova Senha");
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newPassword = input.getText().toString();
            updatePassword(newPassword);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePassword(String newPassword) {
        // Salva a nova senha no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userPassword", newPassword);  // Armazena a nova senha
        editor.apply();
        Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
    }
}
