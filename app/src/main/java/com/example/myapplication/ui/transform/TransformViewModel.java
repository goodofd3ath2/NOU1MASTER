package com.example.myapplication.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.slideshow.Anotacao;
import com.example.myapplication.ui.slideshow.AnotacaoDao;
import com.example.myapplication.ui.slideshow.AppDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<HashMap<String, List<Anotacao>>> disciplinasAnotacoes = new MutableLiveData<>();

    public TransformViewModel(AppDatabase database) {
        carregarDisciplinas(database);
    }

    private void carregarDisciplinas(AppDatabase database) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AnotacaoDao anotacaoDao = database.anotacaoDao();
            List<String> disciplinas = anotacaoDao.getAllDisciplinas();
            HashMap<String, List<Anotacao>> mapaDisciplinas = new HashMap<>();

            for (String disciplina : disciplinas) {
                List<Anotacao> anotacoes = anotacaoDao.getAnotacoesByDisciplina(disciplina);
                mapaDisciplinas.put(disciplina, anotacoes);
            }

            disciplinasAnotacoes.postValue(mapaDisciplinas);
        });
    }

    public LiveData<HashMap<String, List<Anotacao>>> getDisciplinasAnotacoes() {
        return disciplinasAnotacoes;
    }
}
