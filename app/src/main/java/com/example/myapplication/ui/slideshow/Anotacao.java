package com.example.myapplication.ui.slideshow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "anotacoes") // Nome da tabela ajustado
public class Anotacao {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String texto;
    private String disciplina;
    private String dataHoraCriacao;
    private String dataHoraEdicao;
    private boolean editado;

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(String dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDataHoraEdicao() {
        return dataHoraEdicao;
    }

    public void setDataHoraEdicao(String dataHoraEdicao) {
        this.dataHoraEdicao = dataHoraEdicao;
    }

    public boolean isEditado() {
        return editado;
    }

    public void setEditado(boolean editado) {
        this.editado = editado;
    }
}

