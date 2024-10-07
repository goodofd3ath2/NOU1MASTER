package com.example.myapplication.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private static final int PICK_IMAGE = 1; // Constante para escolher a imagem

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Use o ID correto aqui
        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Referências aos componentes da UI
        EditText editTextName = binding.editTextName;
        EditText editTextPassword = binding.editTextPassword;
        ImageView imageViewProfile = binding.imageViewProfile;
        Button buttonUpdate = binding.buttonUpdate;
        Button buttonChangePhoto = binding.buttonChangePhoto;
        TextView textViewMoodle = binding.textViewMoodle;

        // Configurar o botão de atualizar
        buttonUpdate.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            String newPassword = editTextPassword.getText().toString();
            // Aqui você pode implementar a lógica de atualização do nome e senha
            Toast.makeText(getContext(), "Nome e senha atualizados!", Toast.LENGTH_SHORT).show();
        });

        // Configurar o botão para trocar a foto
        buttonChangePhoto.setOnClickListener(v -> {
            // Abrir galeria para escolher uma imagem
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Configurar o clique para acessar o Moodle
        textViewMoodle.setOnClickListener(v -> {
            // Aqui você deve adicionar a URL do Moodle
            String moodleUrl = "https://moodle.example.com"; // Substitua pela URL real
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(moodleUrl));
            startActivity(webIntent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            binding.imageViewProfile.setImageURI(imageUri); // Atualiza a imagem da foto de perfil
        }
    }
}
