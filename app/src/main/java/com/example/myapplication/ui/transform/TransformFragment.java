package com.example.myapplication.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentTransformBinding;
import com.example.myapplication.ui.slideshow.Anotacao;
import com.example.myapplication.ui.slideshow.AnotacaoDao;
import com.example.myapplication.ui.slideshow.AppDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;
    private DisciplinaAdapter adapter;
    private AppDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = AppDatabase.getInstance(requireContext());
        configurarRecyclerView();

        carregarDisciplinas();

        return root;
    }

    private void configurarRecyclerView() {
        adapter = new DisciplinaAdapter(new HashMap<>());
        binding.recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewDisciplinas.setAdapter(adapter);
    }

    private void carregarDisciplinas() {
        TransformViewModel viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        return (T) new TransformViewModel(AppDatabase.getInstance(requireContext()));
                    }
                }).get(TransformViewModel.class);

        viewModel.getDisciplinasAnotacoes().observe(getViewLifecycleOwner(), mapaDisciplinas -> {
            if (mapaDisciplinas != null) {
                adapter.atualizarDisciplinas(mapaDisciplinas);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
