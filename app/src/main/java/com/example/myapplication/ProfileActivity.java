package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameText, userEmailText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Name not set");
        String userEmail = sharedPreferences.getString("userEmail", "Email not set");

        userNameText.setText(userName);
        userEmailText.setText(userEmail);

        changePasswordButton.setOnClickListener(v -> openChangePasswordDialog());
    }

    private void openChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        final EditText input = new EditText(this);
        input.setHint("New Password");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPassword = input.getText().toString();
            updatePassword(newPassword);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePassword(String newPassword) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userPassword", newPassword);
        editor.apply();
    }
}
