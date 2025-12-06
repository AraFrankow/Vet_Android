package com.example.acm4ap_arabela_frankow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {

    private TextView userName, userEmail;
    private Button btnEditUser, btnDelete, btnExit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Perfil");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        btnEditUser = findViewById(R.id.btnEditUser);
        btnDelete = findViewById(R.id.btnDelete);
        btnExit = findViewById(R.id.btnExit);

        if (currentUser != null) {
            userEmail.setText(currentUser.getEmail());
            mFirestore.collection("user").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    if (name != null && !name.isEmpty()) {
                        userName.setText(name);
                    } else {
                        userName.setText(R.string.no_user_name);
                    }
                } else {
                    userName.setText(R.string.no_user_name);
                }
            });
        } else {
            startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
            finish();
        }

        btnExit.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
            finishAffinity();
        });

        btnEditUser.setOnClickListener(v -> {
            Toast.makeText(this, "Función aún no implementada", Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> {
            Toast.makeText(this, "Función aún no implementada", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_user);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(PerfilActivity.this, MascotaActivity.class));
                return true;
            } else if (itemId == R.id.nav_user) {
                return true;
            }
            return false;
        });
    }
}
