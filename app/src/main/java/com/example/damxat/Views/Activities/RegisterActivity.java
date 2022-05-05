package com.example.damxat.Views.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damxat.Model.User;
import com.example.damxat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.registerButton);
        EditText registerUsername = findViewById(R.id.registerUsername);
        EditText registerEmail = findViewById(R.id.registerEmail);
        EditText registerPassword = findViewById(R.id.registerPassword);

        //Creem una instancia de firebase
        auth = FirebaseAuth.getInstance();

        //Al premer aques button agafara el user, email y password per crear l'usuari a firebase
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = registerUsername.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();

                //Aqui es fa una verificacio de que els camps no estiguin buits y si esta correcte procedeix a fer el register
                if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(username, email, password);
                }

            }
        });
    }

    //Funcio per crear l'usuari una vegada han polsat el boto register
    public void registerUser(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       //Despres de crear l'usuari en firebase fem una verificacio de que s'ha realitzat correctament
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            //Agafem l'id de l'usuari creat al firebase y el creem el seu objecte User
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            User user = new User(userId, username, "offline");

                            //Una vegada creat l'objecte li posem la resta d'informacio dins de la base de dades
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Verifiquem si s'ha insertat correctament les dades i iniciem el login activity
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegisterActivity.this, "You can't register with this email", Toast.LENGTH_SHORT).show();
                        }
                   }
               });
    }
}