package com.example.acm4ap_arabela_frankow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity implements verificarContraFragment.OnAccountDeletedListener {

    private TextView userName, userEmail;
    private Button btnEditUser, btnDelete, btnExit;
    private LinearLayout profileViewContainer;
    private FrameLayout fragmentContainer;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

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
        currentUser = mAuth.getCurrentUser();

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        btnEditUser = findViewById(R.id.btnEditUser);
        btnDelete = findViewById(R.id.btnDelete);
        btnExit = findViewById(R.id.btnExit);
        profileViewContainer = findViewById(R.id.profile_view_container);
        fragmentContainer = findViewById(R.id.edit_user_fragment_container);

        loadUserData();

        btnExit.setOnClickListener(v -> {
            mAuth.signOut();
            goToLogin();
        });

        btnEditUser.setOnClickListener(v -> showFragment(EditarUsuarioFragment.newInstance()));

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar Cuenta")
                    .setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Se te pedirá la contraseña para confirmar. Esta acción no se puede deshacer.")
                    .setPositiveButton("Continuar", (dialog, which) -> {
                        showFragment(verificarContraFragment.newInstance());
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
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

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                showProfileView();
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            userEmail.setText(currentUser.getEmail());
            mFirestore.collection("user").document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        processUserData(task.getResult());
                    } else {
                        mFirestore.collection("user").document(currentUser.getUid()).get()
                            .addOnCompleteListener(fallbackTask -> {
                                if (fallbackTask.isSuccessful() && fallbackTask.getResult().exists()) {
                                    processUserData(fallbackTask.getResult());
                                } else {
                                    userName.setText(R.string.no_user_name);
                                }
                            });
                    }
                });
        } else {
            goToLogin();
        }
    }

    private void processUserData(DocumentSnapshot document) {
        String name = document.getString("name");
        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            userName.setText(R.string.no_user_name);
        }
    }

    private void showFragment(Fragment fragment) {
        profileViewContainer.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.edit_user_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showProfileView() {
        profileViewContainer.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        loadUserData();
    }

    @Override
    public void onAccountDeleted() {
        goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
