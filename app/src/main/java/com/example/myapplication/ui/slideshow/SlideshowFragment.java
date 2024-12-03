package com.example.myapplication.ui.slideshow;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentSlideshowBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private AnotacaoAdapter adapter;
    private String disciplinaAtual;
    private AppDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = AppDatabase.getDatabase(requireContext());
        configurarSpinner();
        configurarRecyclerView();
        configurarBotoes();

        return root;
    }

    private void configurarSpinner() {
        String[] disciplinas = {"Algoritmos", "Estruturas de Dados", "Engenharia de Software"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, disciplinas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDisciplinas.setAdapter(adapter);

        binding.spinnerDisciplinas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                disciplinaAtual = disciplinas[position];
                carregarAnotacoes(disciplinaAtual);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void configurarRecyclerView() {
        adapter = new AnotacaoAdapter(new ArrayList<>(), anotacao -> abrirDialogoEditar(anotacao));
        binding.recyclerViewAnotacoes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewAnotacoes.setAdapter(adapter);
    }


    private void configurarBotoes() {
        binding.buttonSalvarAnotacao.setOnClickListener(v -> salvarAnotacao());
    }

    private void salvarAnotacao() {
        String texto = binding.editTextAnotacoes.getText().toString();
        if (TextUtils.isEmpty(texto)) {
            Toast.makeText(requireContext(), "Escreva uma anotação!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        Anotacao novaAnotacao = new Anotacao(texto, disciplinaAtual, dataHora, false);

        Executors.newSingleThreadExecutor().execute(() -> {
            database.anotacaoDao().insert(novaAnotacao);
            carregarAnotacoes(disciplinaAtual);
            requireActivity().runOnUiThread(() -> binding.editTextAnotacoes.setText(""));
        });
    }

    private void carregarAnotacoes(String disciplina) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Anotacao> anotacoes = database.anotacaoDao().getAnotacoesPorDisciplina(disciplina);
            requireActivity().runOnUiThread(() -> adapter.atualizarLista(anotacoes));
        });
    }

    private void abrirDialogoEditar(Anotacao anotacao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Anotação");

        final EditText editText = new EditText(requireContext());
        editText.setText(anotacao.getTexto());
        builder.setView(editText);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoTexto = editText.getText().toString();
            String dataHoraEdicao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            anotacao.setTexto(novoTexto);
            anotacao.setEditado(true);
            anotacao.setDataHoraEdicao(dataHoraEdicao);

            Executors.newSingleThreadExecutor().execute(() -> {
                database.anotacaoDao().update(anotacao);
                carregarAnotacoes(disciplinaAtual);
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
