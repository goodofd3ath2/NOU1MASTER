package com.example.myapplication.ui.slideshow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnotacaoDao {
    @Insert
    void insert(Anotacao anotacao);

    @Update
    void update(Anotacao anotacao);

    @Query("SELECT * FROM anotacoes WHERE disciplina = :disciplina ORDER BY id DESC")
    List<Anotacao> getAnotacoesPorDisciplina(String disciplina);


    @Query("SELECT DISTINCT disciplina FROM anotacoes")
    List<String> getAllDisciplinas();

    @Query("SELECT * FROM anotacoes WHERE disciplina = :disciplina")
    List<Anotacao> getAnotacoesByDisciplina(String disciplina);
}

