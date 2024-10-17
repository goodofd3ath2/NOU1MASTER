package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {

    private final FirebaseAuth auth;  // Instância do FirebaseAuth
    private final MutableLiveData<FirebaseUser> userLiveData;  // LiveData para o usuário autenticado
    private final MutableLiveData<String> errorLiveData;  // LiveData para mensagens de erro
    private final MutableLiveData<Boolean> loginStatusLiveData;  // LiveData para monitorar o estado do login (sucesso ou falha)

    public LoginViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();  // Inicializa FirebaseAuth
        userLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        loginStatusLiveData = new MutableLiveData<>();
    }

    // Método para autenticar o usuário com e-mail e senha
    public void login(String email, String password) {
        Log.d("LoginViewModel", "Tentando fazer login com: " + email);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        userLiveData.postValue(user);  // Se o login foi bem-sucedido, atualiza o LiveData com o usuário
                        loginSuccess();
                    } else {
                        loginFailure();
                        errorLiveData.postValue("Autenticação falhou.");  // Se falhar, atualiza o LiveData com a mensagem de erro
                    }
                });
    }

    // Retorna LiveData para o FirebaseUser
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    // Retorna LiveData para mensagens de erro
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Retorna LiveData para o estado do login (sucesso ou falha)
    public LiveData<Boolean> getLoginStatusLiveData() {
        return loginStatusLiveData;
    }

    // Método chamado quando o login é bem-sucedido
    public void loginSuccess() {
        loginStatusLiveData.postValue(true);  // Atualiza o estado para indicar que o login foi bem-sucedido
    }

    // Método chamado quando o login falha
    public void loginFailure() {
        loginStatusLiveData.postValue(false);  // Atualiza o estado para indicar falha no login
    }
}
