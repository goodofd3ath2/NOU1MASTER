package com.example.myapplication.ui.slideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentSlideshowBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private HashMap<String, List<Anotacao>> anotacoesPorDisciplina = new HashMap<>();
    private Uri fotoSelecionadaUri;
    private Uri arquivoSelecionadoUri;

    // Atividade para selecionar uma imagem
    private final ActivityResultLauncher<String> selecionarImagemLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fotoSelecionadaUri = uri;
                    Toast.makeText(getContext(), "Foto selecionada!", Toast.LENGTH_SHORT).show();
                }
            });

    // Atividade para selecionar um arquivo
    private final ActivityResultLauncher<String> selecionarArquivoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    arquivoSelecionadoUri = uri;
                    Toast.makeText(getContext(), "Arquivo selecionado!", Toast.LENGTH_SHORT).show();
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinnerDisciplinas = binding.spinnerDisciplinas;
        EditText editTextAnotacoes = binding.editTextAnotacoes;
        Button buttonSalvarAnotacao = binding.buttonSalvarAnotacao;
        Button buttonAdicionarFoto = binding.buttonAdicionarFoto;
        Button buttonAdicionarArquivo = binding.buttonAdicionarArquivo;
        LinearLayout linearLayoutAnotacoes = binding.linearLayoutAnotacoes;
        TextView textAnotacoesSalvas = binding.textAnotacoesSalvas;

        // Lista de cadeiras do curso de Sistemas de Informação
        String[] disciplinas = {
                "Algoritmos",
                "Estruturas de Dados",
                "Engenharia de Software",
                "Banco de Dados",
                "Redes de Computadores",
                "Inteligência Artificial",
                "Segurança da Informação"
        };

        // Configurando o Spinner com as disciplinas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, disciplinas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplinas.setAdapter(adapter);

        // Botão para selecionar uma foto
        buttonAdicionarFoto.setOnClickListener(v -> {
            selecionarImagemLauncher.launch("image/*");
        });

        // Botão para selecionar um arquivo
        buttonAdicionarArquivo.setOnClickListener(v -> {
            selecionarArquivoLauncher.launch("*/*");
        });

        buttonSalvarAnotacao.setOnClickListener(v -> {
            String disciplinaSelecionada = spinnerDisciplinas.getSelectedItem().toString();
            String anotacaoTexto = editTextAnotacoes.getText().toString();

            if (TextUtils.isEmpty(anotacaoTexto)) {
                Toast.makeText(requireContext(), "Escreva uma anotação antes de salvar!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criando uma nova anotação
            Anotacao anotacao = new Anotacao(anotacaoTexto, fotoSelecionadaUri, arquivoSelecionadoUri);

            // Salvando a anotação para a disciplina selecionada
            if (!anotacoesPorDisciplina.containsKey(disciplinaSelecionada)) {
                anotacoesPorDisciplina.put(disciplinaSelecionada, new ArrayList<>());
            }
            anotacoesPorDisciplina.get(disciplinaSelecionada).add(anotacao);

            // Limpar a EditText e as URIs após salvar
            editTextAnotacoes.setText("");
            fotoSelecionadaUri = null;
            arquivoSelecionadoUri = null;

            // Atualizar o layout com as novas anotações
            atualizarAnotacoesSalvas(disciplinaSelecionada, linearLayoutAnotacoes, textAnotacoesSalvas);
        });

        return root;
    }

    private void atualizarAnotacoesSalvas(String disciplina, LinearLayout linearLayoutAnotacoes, TextView textAnotacoesSalvas) {
        linearLayoutAnotacoes.removeAllViews();

        List<Anotacao> anotacoes = anotacoesPorDisciplina.get(disciplina);
        if (anotacoes != null && !anotacoes.isEmpty()) {
            textAnotacoesSalvas.setVisibility(View.VISIBLE);

            for (Anotacao anotacao : anotacoes) {
                // Exibir o texto da anotação
                TextView textViewAnotacao = new TextView(requireContext());
                textViewAnotacao.setText(anotacao.getTexto());
                textViewAnotacao.setPadding(0, 10, 0, 10);
                linearLayoutAnotacoes.addView(textViewAnotacao);

                // Exibir a foto (se houver)
                if (anotacao.getFotoUri() != null) {
                    ImageView imageView = new ImageView(requireContext());
                    imageView.setImageURI(anotacao.getFotoUri());
                    imageView.setPadding(0, 10, 0, 10);
                    linearLayoutAnotacoes.addView(imageView);
                }

                // Exibir o arquivo (se houver)
                if (anotacao.getArquivoUri() != null) {
                    TextView textViewArquivo = new TextView(requireContext());
                    textViewArquivo.setText("Arquivo anexado: " + anotacao.getArquivoUri().getLastPathSegment());
                    textViewArquivo.setPadding(0, 10, 0, 10);
                    linearLayoutAnotacoes.addView(textViewArquivo);
                }
            }
        } else {
            textAnotacoesSalvas.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Classe para representar uma anotação
    private static class Anotacao {
        private final String texto;
        private final Uri fotoUri;
        private final Uri arquivoUri;

        public Anotacao(String texto, Uri fotoUri, Uri arquivoUri) {
            this.texto = texto;
            this.fotoUri = fotoUri;
            this.arquivoUri = arquivoUri;
        }

        public String getTexto() {
            return texto;
        }

        public Uri getFotoUri() {
            return fotoUri;
        }

        public Uri getArquivoUri() {
            return arquivoUri;
        }
    }
}
