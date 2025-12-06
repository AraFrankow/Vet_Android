package com.example.acm4ap_arabela_frankow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acm4ap_arabela_frankow.Adapter.PetAdapter;
import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecycler;
    PetAdapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;


    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Bienvenido");
        }

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemAnimator(null);

        Query query = mFirestore.collection("pet");
        FirestoreRecyclerOptions<Pet> FirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Pet>().setQuery(query, Pet.class).build();

        mAdapter = new PetAdapter(FirestoreRecyclerOptions, this, getSupportFragmentManager());
        mRecycler.setAdapter(mAdapter);
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