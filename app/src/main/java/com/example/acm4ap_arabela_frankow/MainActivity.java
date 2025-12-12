package com.example.acm4ap_arabela_frankow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acm4ap_arabela_frankow.Adapter.PetAdapter;
import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements PetAdapter.OnPetInteractionListener {

    private RecyclerView mRecycler;
    private PetAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private FrameLayout fragmentContainer;

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigation();
        setupActionBar();

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recyclerViewSingle);
        fragmentContainer = findViewById(R.id.fragment_container);

        setupRecyclerView();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                showRecyclerView();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(MainActivity.this, MascotaActivity.class));
                return true;
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mis Mascotas");
        }
    }

    private void setupRecyclerView() {
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("pet");
        FirestoreRecyclerOptions<Pet> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Pet>()
                .setQuery(query, Pet.class)
                .build();

        mAdapter = new PetAdapter(firestoreRecyclerOptions, this, this);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onEditPet(String petId) {
        showFragment(EditarMascotaFragment.newInstance(petId));
    }

    @Override
    public void onViewPet(String petId) {
        showFragment(PetDetailFragment.newInstance(petId));
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        mRecycler.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        mRecycler.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
