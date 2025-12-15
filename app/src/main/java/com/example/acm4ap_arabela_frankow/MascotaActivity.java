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

import androidx.activity.OnBackPressedCallback;
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
    private TextInputEditText name, age, weight, fechaAntiparasitario, fechaAntipulgas, otraRaza;
    private AutoCompleteTextView tipoMascota, genre, race;
    private TextInputLayout layoutOtraRaza;
    private LinearLayout vacunasPerroContainer, vacunasGatoContainer;
    private TextView labelVacunaPerro, labelVacunaGato;

    // --- Perros ---
    private CheckBox cbRabiaPerro, cbParvovirus, cbMoquillo, cbHepatitis, cbLeptospirosis;
    private TextInputLayout layoutVacunaRabiaPerro, layoutVacunaParvovirus, layoutVacunaMoquillo, layoutVacunaHepatitis, layoutVacunaLeptospirosis;
    private TextInputEditText fechaVacunaRabiaPerro, fechaVacunaParvovirus, fechaVacunaMoquillo, fechaVacunaHepatitis, fechaVacunaLeptospirosis;

    // --- Gatos ---
    private CheckBox cbTrivalente, cbLeucemia, cbRabiaGato;
    private TextInputLayout layoutVacunaTrivalente, layoutVacunaLeucemia, layoutVacunaRabiaGato;
    private TextInputEditText fechaVacunaTrivalente, fechaVacunaLeucemia, fechaVacunaRabiaGato;

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MascotaActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.nombre);
        tipoMascota = findViewById(R.id.tipoMascota);
        age = findViewById(R.id.edad);
        genre = findViewById(R.id.genero);
        race = findViewById(R.id.raza);
        layoutOtraRaza = findViewById(R.id.layout_otra_raza);
        otraRaza = findViewById(R.id.otra_raza);
        weight = findViewById(R.id.peso);
        fechaAntiparasitario = findViewById(R.id.fechaAntiparasitario);
        fechaAntipulgas = findViewById(R.id.fechaAntipulgas);
        btn_agregar = findViewById(R.id.btn_registro);

        vacunasPerroContainer = findViewById(R.id.vacunas_perro_container);
        vacunasGatoContainer = findViewById(R.id.vacunas_gato_container);
        labelVacunaPerro = findViewById(R.id.label_vacuna_perro);
        labelVacunaGato = findViewById(R.id.label_vacuna_gato);


        // Vistas de vacunas para perros
        cbRabiaPerro = findViewById(R.id.cb_rabia_perro);
        layoutVacunaRabiaPerro = findViewById(R.id.layout_vacuna_rabia_perro);
        fechaVacunaRabiaPerro = findViewById(R.id.fecha_vacuna_rabia_perro);
        cbParvovirus = findViewById(R.id.cb_parvovirus);
        layoutVacunaParvovirus = findViewById(R.id.layout_vacuna_parvovirus);
        fechaVacunaParvovirus = findViewById(R.id.fecha_vacuna_parvovirus);
        cbMoquillo = findViewById(R.id.cb_moquillo);
        layoutVacunaMoquillo = findViewById(R.id.layout_vacuna_moquillo);
        fechaVacunaMoquillo = findViewById(R.id.fecha_vacuna_moquillo);
        cbHepatitis = findViewById(R.id.cb_hepatitis);
        layoutVacunaHepatitis = findViewById(R.id.layout_vacuna_hepatitis);
        fechaVacunaHepatitis = findViewById(R.id.fecha_vacuna_hepatitis);
        cbLeptospirosis = findViewById(R.id.cb_leptospirosis);
        layoutVacunaLeptospirosis = findViewById(R.id.layout_vacuna_leptospirosis);
        fechaVacunaLeptospirosis = findViewById(R.id.fecha_vacuna_leptospirosis);

        // Vistas de vacunas para gatos
        cbTrivalente = findViewById(R.id.cb_trivalente);
        layoutVacunaTrivalente = findViewById(R.id.layout_vacuna_trivalente);
        fechaVacunaTrivalente = findViewById(R.id.fecha_vacuna_trivalente);
        cbLeucemia = findViewById(R.id.cb_leucemia);
        layoutVacunaLeucemia = findViewById(R.id.layout_vacuna_leucemia);
        fechaVacunaLeucemia = findViewById(R.id.fecha_vacuna_leucemia);
        cbRabiaGato = findViewById(R.id.cb_rabia_gato);
        layoutVacunaRabiaGato = findViewById(R.id.layout_vacuna_rabia_gato);
        fechaVacunaRabiaGato = findViewById(R.id.fecha_vacuna_rabia_gato);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_add);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(MascotaActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

        String[] dogRaces = new String[]{"Border Collie", "Labrador", "Braco", "Bulldog", "Caniche", "Chihuahua", "Cocker", "Golden Retriever", "Schnauzer", "Galgo", "Otro"};
        String[] catRaces = new String[]{"Siamés", "Persa", "Bengala", "Maine Coon", "Criollo", "Korat", "Otro"};

        tipoMascota.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            ArrayAdapter<String> raceAdapter;
            if ("Perro".equals(selected)) {
                vacunasPerroContainer.setVisibility(View.VISIBLE);
                vacunasGatoContainer.setVisibility(View.GONE);
                raceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dogRaces);
            } else if ("Gato".equals(selected)) {
                vacunasGatoContainer.setVisibility(View.VISIBLE);
                vacunasPerroContainer.setVisibility(View.GONE);
                raceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, catRaces);
            } else {
                raceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{});
            }
            race.setAdapter(raceAdapter);
            race.setText("", false);
            layoutOtraRaza.setVisibility(View.GONE);
        });

        // Decir la otra raza del animal
        race.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRace = (String) parent.getItemAtPosition(position);
            if ("Otro".equals(selectedRace)) {
                layoutOtraRaza.setVisibility(View.VISIBLE);
            } else {
                layoutOtraRaza.setVisibility(View.GONE);
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
            layoutVacunaRabiaPerro.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbParvovirus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaParvovirus.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbMoquillo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaMoquillo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbHepatitis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaHepatitis.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });
        cbLeptospirosis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaLeptospirosis.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePerroLabelVisibility();
        });

        // Listeners para gatos
        cbTrivalente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaTrivalente.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
        cbLeucemia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaLeucemia.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
        cbRabiaGato.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutVacunaRabiaGato.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateGatoLabelVisibility();
        });
    }

    private void updatePerroLabelVisibility() {
        boolean anyCheckboxChecked = cbRabiaPerro.isChecked() || cbParvovirus.isChecked() || cbMoquillo.isChecked() || cbHepatitis.isChecked() || cbLeptospirosis.isChecked();
        labelVacunaPerro.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }

    private void updateGatoLabelVisibility() {
        boolean anyCheckboxChecked = cbTrivalente.isChecked() || cbLeucemia.isChecked() || cbRabiaGato.isChecked();
        labelVacunaGato.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }

    private void collectAndPostData() {
        String namepet = name.getText().toString().trim();
        String tipoMascotapet = tipoMascota.getText().toString().trim();
        String raceSelected = race.getText().toString().trim();
        String racepet;

        if ("Otro".equals(raceSelected)) {
            racepet = otraRaza.getText().toString().trim();
        } else {
            racepet = raceSelected;
        }

        String agepetStr = age.getText().toString().trim();
        String weightpetStr = weight.getText().toString().trim();
        String genrepet = genre.getText().toString().trim();
        String dateAntiparasitariopet = fechaAntiparasitario.getText().toString().trim();
        String dateAntipulgaspet = fechaAntipulgas.getText().toString().trim();

        if (namepet.isEmpty() || tipoMascotapet.isEmpty() || racepet.isEmpty() || agepetStr.isEmpty() || genrepet.isEmpty() || weightpetStr.isEmpty() || dateAntiparasitariopet.isEmpty() || dateAntipulgaspet.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingresar todos los datos básicos", Toast.LENGTH_SHORT).show();
            return;
        }

        int ageVal;
        double weightVal;

        try {
            ageVal = Integer.parseInt(agepetStr);
            weightVal = Double.parseDouble(weightpetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "La edad y el peso deben ser números válidos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Perro".equals(tipoMascotapet)) {
            if (ageVal > 30) {
                Toast.makeText(getApplicationContext(), "La edad no puede ser mayor a 30 años", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weightVal > 100) {
                Toast.makeText(getApplicationContext(), "El peso no puede ser mayor a 100 kg", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if ("Gato".equals(tipoMascotapet)) {
            if (ageVal > 30) {
                Toast.makeText(getApplicationContext(), "La edad no puede ser mayor a 30 años", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weightVal > 50) {
                Toast.makeText(getApplicationContext(), "El peso no puede ser mayor a 50 kg", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", namepet);
        map.put("tipoMascota", tipoMascotapet);
        map.put("race", racepet);
        map.put("age", agepetStr);
        map.put("genre", genrepet);
        map.put("weight", weightpetStr);
        map.put("dateAntiparasitario", dateAntiparasitariopet);
        map.put("dateAntipulgas", dateAntipulgaspet);

        if ("Perro".equals(tipoMascotapet)) {
            addVaccineDataToMap(map, "vacuna_rabia", cbRabiaPerro, fechaVacunaRabiaPerro);
            addVaccineDataToMap(map, "vacuna_parvovirus", cbParvovirus, fechaVacunaParvovirus);
            addVaccineDataToMap(map, "vacuna_moquillo", cbMoquillo, fechaVacunaMoquillo);
            addVaccineDataToMap(map, "vacuna_hepatitis", cbHepatitis, fechaVacunaHepatitis);
            addVaccineDataToMap(map, "vacuna_leptospirosis", cbLeptospirosis, fechaVacunaLeptospirosis);
        } else if ("Gato".equals(tipoMascotapet)) {
            addVaccineDataToMap(map, "vacuna_trivalente", cbTrivalente, fechaVacunaTrivalente);
            addVaccineDataToMap(map, "vacuna_leucemia", cbLeucemia, fechaVacunaLeucemia);
            addVaccineDataToMap(map, "vacuna_rabia_gato", cbRabiaGato, fechaVacunaRabiaGato);
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
            Intent intent = new Intent(MascotaActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
