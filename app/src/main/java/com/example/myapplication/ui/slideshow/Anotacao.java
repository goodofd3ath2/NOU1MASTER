package com.example.myapplication.ui.slideshow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "anotacoes")
public class Anotacao {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String texto;
    private String disciplina;
    private String dataHoraCriacao;
    private String dataHoraEdicao;
    private boolean editado;

    public Anotacao(String texto, String disciplina, String dataHoraCriacao, boolean editado) {
        this.texto = texto;
        this.disciplina = disciplina;
        this.dataHoraCriacao = dataHoraCriacao;
        this.editado = editado;
        this.dataHoraEdicao = null;
    }

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
