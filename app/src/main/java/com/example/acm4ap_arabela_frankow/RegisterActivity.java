package com.example.acm4ap_arabela_frankow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText name, email, password;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Registro");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.nombre);
        email = findViewById(R.id.correo);
        password = findViewById(R.id.contrasena);
        btn_register = findViewById(R.id.btn_registro);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = name.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();

                if (nameUser.isEmpty() || emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else{
                    if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                        email.setError("Ingrese un email válido");
                        email.requestFocus();
                        return;
                    }
                    if (passUser.length() < 6) {
                        password.setError("La contraseña debe tener al menos 6 caracteres");
                        password.requestFocus();
                        return;
                    }
                    registerUser(nameUser, emailUser, passUser);
                }
            }
        });
    }

    private void registerUser(String nameUser, String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("name", nameUser);
                        map.put("password", passUser);
                        map.put("email", emailUser);

                        mFirestore.collection("user").document(id).set(map)
                                .addOnSuccessListener(unused -> {
                                    finish();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        String error = task.getException().getMessage();
                        if (error != null && error.contains("email address is already in use")) {
                            Toast.makeText(RegisterActivity.this, "El email ya está registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}