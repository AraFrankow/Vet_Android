package com.example.acm4ap_arabela_frankow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MascotaActivity extends AppCompatActivity {

    private Button btn_agregar;
    private TextInputEditText name, age, weight, fechaAntiparasitario, fechaAntipulgas;
    private AutoCompleteTextView tipoMascota, genre;
    private LinearLayout vacunasPerroContainer, vacunasGatoContainer;
    private TextView labelRevacunaPerro, labelRevacunaGato;

    // --- Perros ---
    private CheckBox cbRabiaPerro, cbParvovirus, cbMoquillo, cbHepatitis, cbLeptospirosis;
    private TextInputLayout layoutRevacunaRabiaPerro, layoutRevacunaParvovirus, layoutRevacunaMoquillo, layoutRevacunaHepatitis, layoutRevacunaLeptospirosis;
    private TextInputEditText fechaRevacunaRabiaPerro, fechaRevacunaParvovirus, fechaRevacunaMoquillo, fechaRevacunaHepatitis, fechaRevacunaLeptospirosis;

    // --- Gatos ---
    private CheckBox cbTrivalente, cbLeucemia, cbRabiaGato;
    private TextInputLayout layoutRevacunaTrivalente, layoutRevacunaLeucemia, layoutRevacunaRabiaGato;
    private TextInputEditText fechaRevacunaTrivalente, fechaRevacunaLeucemia, fechaRevacunaRabiaGato;

    private FirebaseFirestore mfirestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascota);

        setupViews();
        setupBottomNavigation();
        setupActionBar();
        setupPetTypeDropdown();
        setupPetGenreDropdown();
        setupVaccineListeners();

        mfirestore = FirebaseFirestore.getInstance();

        btn_agregar.setOnClickListener(v -> collectAndPostData());
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.nombre);
        tipoMascota = findViewById(R.id.tipoMascota);
        age = findViewById(R.id.edad);
        genre = findViewById(R.id.genero);
        weight = findViewById(R.id.peso);
        fechaAntiparasitario = findViewById(R.id.fechaAntiparasitario);
        fechaAntipulgas = findViewById(R.id.fechaAntipulgas);
        btn_agregar = findViewById(R.id.btn_registro);

        vacunasPerroContainer = findViewById(R.id.vacunas_perro_container);
        vacunasGatoContainer = findViewById(R.id.vacunas_gato_container);
        labelRevacunaPerro = findViewById(R.id.label_revacuna_perro);
        labelRevacunaGato = findViewById(R.id.label_revacuna_gato);


        // Vistas de vacunas para perros
        cbRabiaPerro = findViewById(R.id.cb_rabia_perro);
        layoutRevacunaRabiaPerro = findViewById(R.id.layout_revacuna_rabia_perro);
        fechaRevacunaRabiaPerro = findViewById(R.id.fecha_revacuna_rabia_perro);
        cbParvovirus = findViewById(R.id.cb_parvovirus);
        layoutRevacunaParvovirus = findViewById(R.id.layout_revacuna_parvovirus);
        fechaRevacunaParvovirus = findViewById(R.id.fecha_revacuna_parvovirus);
        cbMoquillo = findViewById(R.id.cb_moquillo);
        layoutRevacunaMoquillo = findViewById(R.id.layout_revacuna_moquillo);
        fechaRevacunaMoquillo = findViewById(R.id.fecha_revacuna_moquillo);
        cbHepatitis = findViewById(R.id.cb_hepatitis);
        layoutRevacunaHepatitis = findViewById(R.id.layout_revacuna_hepatitis);
        fechaRevacunaHepatitis = findViewById(R.id.fecha_revacuna_hepatitis);
        cbLeptospirosis = findViewById(R.id.cb_leptospirosis);
        layoutRevacunaLeptospirosis = findViewById(R.id.layout_revacuna_leptospirosis);
        fechaRevacunaLeptospirosis = findViewById(R.id.fecha_revacuna_leptospirosis);

        // Vistas de vacunas para gatos
        cbTrivalente = findViewById(R.id.cb_trivalente);
        layoutRevacunaTrivalente = findViewById(R.id.layout_revacuna_trivalente);
        fechaRevacunaTrivalente = findViewById(R.id.fecha_revacuna_trivalente);
        cbLeucemia = findViewById(R.id.cb_leucemia);
        layoutRevacunaLeucemia = findViewById(R.id.layout_revacuna_leucemia);
        fechaRevacunaLeucemia = findViewById(R.id.fecha_revacuna_leucemia);
        cbRabiaGato = findViewById(R.id.cb_rabia_gato);
        layoutRevacunaRabiaGato = findViewById(R.id.layout_revacuna_rabia_gato);
        fechaRevacunaRabiaGato = findViewById(R.id.fecha_revacuna_rabia_gato);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_add);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(MascotaActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                return true;
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(MascotaActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Agregar Mascota");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setupPetTypeDropdown() {
        String[] petTypes = new String[]{"Perro", "Gato"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, petTypes);
        tipoMascota.setAdapter(adapter);

        tipoMascota.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if ("Perro".equals(selected)) {
                vacunasPerroContainer.setVisibility(View.VISIBLE);
                vacunasGatoContainer.setVisibility(View.GONE);
            } else if ("Gato".equals(selected)) {
                vacunasGatoContainer.setVisibility(View.VISIBLE);
                vacunasPerroContainer.setVisibility(View.GONE);
            }
        });
    }

    private void setupPetGenreDropdown() {
        String[] petGenre = new String[]{"Macho", "Hembra"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, petGenre);
        genre.setAdapter(adapter);
    }

    private void setupVaccineListeners() {
        // Listeners para perros
        cbRabiaPerro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaRabiaPerro.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbParvovirus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaParvovirus.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbMoquillo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaMoquillo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbHepatitis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaHepatitis.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbLeptospirosis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaLeptospirosis.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });

        // Listeners para gatos
        cbTrivalente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaTrivalente.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
        cbLeucemia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaLeucemia.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
        cbRabiaGato.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRevacunaRabiaGato.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
    }

    private void updatePerroLabelVisibility() {
        boolean anyCheckboxChecked = cbRabiaPerro.isChecked() || cbParvovirus.isChecked() || cbMoquillo.isChecked() || cbHepatitis.isChecked() || cbLeptospirosis.isChecked();
        labelRevacunaPerro.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }

    private void updateGatoLabelVisibility() {
        boolean anyCheckboxChecked = cbTrivalente.isChecked() || cbLeucemia.isChecked() || cbRabiaGato.isChecked();
        labelRevacunaGato.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }


    private void collectAndPostData() {
        String namepet = name.getText().toString().trim();
        String tipoMascotapet = tipoMascota.getText().toString().trim();
        String agepet = age.getText().toString().trim();
        String genrepet = genre.getText().toString().trim();
        String weightpet = weight.getText().toString().trim();
        String dateAntiparasitariopet = fechaAntiparasitario.getText().toString().trim();
        String dateAntipulgaspet = fechaAntipulgas.getText().toString().trim();

        if (namepet.isEmpty() || tipoMascotapet.isEmpty() || agepet.isEmpty() || genrepet.isEmpty() || weightpet.isEmpty() || dateAntiparasitariopet.isEmpty() || dateAntipulgaspet.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingresar todos los datos básicos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", namepet);
        map.put("tipoMascota", tipoMascotapet);
        map.put("age", agepet);
        map.put("genre", genrepet);
        map.put("weight", weightpet);
        map.put("dateAntiparasitario", dateAntiparasitariopet);
        map.put("dateAntipulgas", dateAntipulgaspet);

        if ("Perro".equals(tipoMascotapet)) {
            addVaccineDataToMap(map, "vacuna_rabia", cbRabiaPerro, fechaRevacunaRabiaPerro);
            addVaccineDataToMap(map, "vacuna_parvovirus", cbParvovirus, fechaRevacunaParvovirus);
            addVaccineDataToMap(map, "vacuna_moquillo", cbMoquillo, fechaRevacunaMoquillo);
            addVaccineDataToMap(map, "vacuna_hepatitis", cbHepatitis, fechaRevacunaHepatitis);
            addVaccineDataToMap(map, "vacuna_leptospirosis", cbLeptospirosis, fechaRevacunaLeptospirosis);
        } else if ("Gato".equals(tipoMascotapet)) {
            addVaccineDataToMap(map, "vacuna_trivalente", cbTrivalente, fechaRevacunaTrivalente);
            addVaccineDataToMap(map, "vacuna_leucemia", cbLeucemia, fechaRevacunaLeucemia);
            addVaccineDataToMap(map, "vacuna_rabia_gato", cbRabiaGato, fechaRevacunaRabiaGato);
        }

        postPet(map);
    }

    private void addVaccineDataToMap(Map<String, Object> map, String vaccineName, CheckBox checkBox, TextInputEditText dateInput) {
        map.put(vaccineName, checkBox.isChecked());
        if (checkBox.isChecked()) {
            map.put(vaccineName + "_revacuna", dateInput.getText().toString().trim());
        } else {
            map.put(vaccineName + "_revacuna", null);
        }
    }

    private void postPet(Map<String, Object> map) {
        progressBar.setVisibility(View.VISIBLE);
        mfirestore.collection("pet").add(map).addOnSuccessListener(documentReference -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
        });
    }
}
