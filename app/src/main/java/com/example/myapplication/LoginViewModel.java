package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginViewModel extends AndroidViewModel {

    private final FirebaseAuth auth;  // Instância do FirebaseAuth
    private final DatabaseReference usersRef;  // Referência para o Firebase Realtime Database

    private final MutableLiveData<FirebaseUser> userLiveData;  // LiveData para o usuário autenticado
    private final MutableLiveData<HashMap<String, String>> userDataLiveData;  // LiveData para dados do usuário
    private final MutableLiveData<String> errorLiveData;  // LiveData para mensagens de erro
    private final MutableLiveData<Boolean> loginStatusLiveData;  // LiveData para monitorar o estado do login

    public LoginViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();  // Inicializa FirebaseAuth
        usersRef = FirebaseDatabase.getInstance().getReference("Users");  // Inicializa a referência ao nó "Users"

        userLiveData = new MutableLiveData<>();
        userDataLiveData = new MutableLiveData<>();
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
                        if (user != null) {
                            userLiveData.postValue(user);  // Atualiza o LiveData com o usuário autenticado
                            fetchUserData(user.getUid());  // Busca os dados do usuário no banco de dados
                            loginSuccess();
                        }
                    } else {
                        loginFailure();
                        errorLiveData.postValue(task.getException() != null ? task.getException().getMessage() : "Erro desconhecido.");
                    }
                });
    }

    // Método para buscar dados do usuário no Realtime Database
    public void fetchUserData(String userId) {
        usersRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                HashMap<String, String> data = (HashMap<String, String>) task.getResult().getValue();
                userDataLiveData.postValue(data);  // Atualiza o LiveData com os dados do usuário
            } else {
                errorLiveData.postValue("Falha ao buscar dados do usuário.");
            }
        });
    }

    // Retorna LiveData para o FirebaseUser
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    // Retorna LiveData para os dados do usuário
    public LiveData<HashMap<String, String>> getUserDataLiveData() {
        return userDataLiveData;
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
    private void loginSuccess() {
        loginStatusLiveData.postValue(true);  // Atualiza o estado para indicar sucesso
    }

    // Método chamado quando o login falha
    private void loginFailure() {
        loginStatusLiveData.postValue(false);  // Atualiza o estado para indicar falha
    }
}
