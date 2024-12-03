package com.example.myapplication.ui.slideshow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class AnotacaoAdapter extends RecyclerView.Adapter<AnotacaoAdapter.AnotacaoViewHolder> {

    private List<Anotacao> anotacoes;
    private final OnAnotacaoClickListener listener;

    public interface OnAnotacaoClickListener {
        void onAnotacaoClick(Anotacao anotacao); // Defina claramente o método esperado
    }

    public AnotacaoAdapter(List<Anotacao> anotacoes, OnAnotacaoClickListener listener) {
        this.anotacoes = anotacoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnotacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anotacao, parent, false);
        return new AnotacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnotacaoViewHolder holder, int position) {
        Anotacao anotacao = anotacoes.get(position);
        holder.bind(anotacao, listener);
    }

    @Override
    public int getItemCount() {
        return anotacoes.size();
    }

    public void atualizarLista(List<Anotacao> novasAnotacoes) {
        this.anotacoes = novasAnotacoes;
        notifyDataSetChanged();
    }

    public static class AnotacaoViewHolder extends RecyclerView.ViewHolder {
        TextView textAnotacao, textData;

        public AnotacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnotacao = itemView.findViewById(R.id.text_anotacao);
            textData = itemView.findViewById(R.id.text_data);
        }

        public void bind(Anotacao anotacao, OnAnotacaoClickListener listener) {
            textAnotacao.setText(anotacao.getTexto());
            textData.setText("Criado em: " + anotacao.getDataHoraCriacao() + (anotacao.isEditado() ? " *editado*" : ""));
            itemView.setOnClickListener(v -> listener.onAnotacaoClick(anotacao));
        }
    }
}

