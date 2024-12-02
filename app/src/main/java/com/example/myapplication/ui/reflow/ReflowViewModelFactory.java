package com.example.myapplication.ui.reflow;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ReflowViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ReflowViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReflowViewModel.class)) {
            return (T) new ReflowViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
