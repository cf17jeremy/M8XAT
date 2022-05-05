package com.example.damxat.Views.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.damxat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        //Creem la instancia de firebase i si estaba logeiat ens porta al main activity
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startLogin = findViewById(R.id.startLogin);
        Button startRegister = findViewById(R.id.startRegister);

        //Al polsar el startLogin ens porta a l'activity de login
        startLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comentar
                Intent intentLogin = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        //Al polser register ens porta a l'activity de register
        startRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comentar
                Intent intentRegister = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }
}