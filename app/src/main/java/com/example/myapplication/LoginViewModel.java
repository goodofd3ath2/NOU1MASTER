package com.example.myapplication;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myapplication.ui.database.UserDao;
import com.example.myapplication.ui.database.UserDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(Context context) {
        UserDatabase database = UserDatabase.getInstance(context); // Instância do banco
        this.userDao = database.userDao(); // DAO para interagir com a tabela de usuários
        this.executorService = Executors.newSingleThreadExecutor(); // Executor para operações assíncronas
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String email, String password) {
        executorService.execute(() -> {
            com.example.myapplication.database.User user = userDao.authenticate(email, password);
            if (user != null) {
                loginSuccess.postValue(true); // Login bem-sucedido
            } else {
                errorMessage.postValue("Usuário ou senha incorretos."); // Falha no login
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // Encerra o executor
    }
}
