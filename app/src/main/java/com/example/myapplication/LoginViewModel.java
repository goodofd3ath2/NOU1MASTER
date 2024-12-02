package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.database.ReminderDatabase;
import com.example.myapplication.ui.database.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {

    private UserDao userDao; // Inicializado via initUserDao
    private final ExecutorService executorService;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Construtor padrão, necessário para compatibilidade com ViewModelProvider
    public LoginViewModel() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Método para injetar o UserDao (manual)
    public void initUserDao(ReminderDatabase database) {
        this.userDao = database.userDao();
    }

    public void login(String email, String password) {
        executorService.execute(() -> {
            if (userDao == null) {
                errorMessage.postValue("Erro interno: UserDao não inicializado.");
                return;
            }
            try {
                com.example.myapplication.database.User user = userDao.authenticate(email, password);
                if (user != null) {
                    loginSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Usuário ou senha incorretos.");
                }
            } catch (Exception e) {
                errorMessage.postValue("Erro inesperado: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
