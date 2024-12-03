package com.example.myapplication.ui.transform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.slideshow.Anotacao;
import com.example.myapplication.ui.slideshow.AnotacaoAdapter;

import java.util.HashMap;
import java.util.List;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.ViewHolder> {

    private HashMap<String, List<Anotacao>> disciplinas;

    public DisciplinaAdapter(HashMap<String, List<Anotacao>> disciplinas) {
        this.disciplinas = disciplinas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disciplina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String disciplina = (String) disciplinas.keySet().toArray()[position];
        List<Anotacao> anotacoes = disciplinas.get(disciplina);

        holder.bind(disciplina, anotacoes);
    }

    @Override
    public int getItemCount() {
        return disciplinas.size();
    }

    public void atualizarDisciplinas(HashMap<String, List<Anotacao>> disciplinas) {
        this.disciplinas = disciplinas;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDisciplina;
        private final RecyclerView recyclerViewAnotacoes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDisciplina = itemView.findViewById(R.id.text_disciplina);
            recyclerViewAnotacoes = itemView.findViewById(R.id.recyclerViewAnotacoes);
        }

        public void bind(String disciplina, List<Anotacao> anotacoes) {
            textDisciplina.setText(disciplina);

            AnotacaoAdapter anotacaoAdapter = new AnotacaoAdapter(anotacoes, anotacao -> {
                // Handle click event on anotacao if needed
            });
            recyclerViewAnotacoes.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewAnotacoes.setAdapter(anotacaoAdapter);
        }
    }
}
