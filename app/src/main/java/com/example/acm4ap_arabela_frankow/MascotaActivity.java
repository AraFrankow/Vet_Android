package com.example.acm4ap_arabela_frankow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MascotaActivity extends AppCompatActivity {

    Button btn_agregar;
    EditText name, tipoMascota, age, weight, genre, nameVacuna, dateVacuna, dateAntiparasitario, dateAntipulgas;
    private FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascota);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Agregar Mascota");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mfirestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.nombre);
        tipoMascota = findViewById(R.id.tipoMascota);
        age = findViewById(R.id.edad);
        genre = findViewById(R.id.genero);
        weight = findViewById(R.id.peso);
        nameVacuna = findViewById(R.id.nombreVacuna);
        dateVacuna = findViewById(R.id.fechaVacuna);
        dateAntiparasitario = findViewById(R.id.fechaAntiparasitario);
        dateAntipulgas = findViewById(R.id.fechaAntipulgas);
        btn_agregar = findViewById(R.id.btn_registro);

        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namepet = name.getText().toString().trim();
                String tipoMascotapet = tipoMascota.getText().toString().trim();
                String agepet = age.getText().toString().trim();
                String genrepet = genre.getText().toString().trim();
                String weightpet = weight.getText().toString().trim();
                String nameVacunapet = nameVacuna.getText().toString().trim();
                String dateVacunapet = dateVacuna.getText().toString().trim();
                String dateAntiparasitariopet = dateAntiparasitario.getText().toString().trim();
                String dateAntipulgaspet = dateAntipulgas.getText().toString().trim();

                if (namepet.isEmpty() || tipoMascotapet.isEmpty() || agepet.isEmpty() || genrepet.isEmpty() || weightpet.isEmpty() || nameVacunapet.isEmpty() || dateVacunapet.isEmpty() || dateAntiparasitariopet.isEmpty() || dateAntipulgaspet.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else if (!isDateValid(dateVacunapet) || !isDateValid(dateAntiparasitariopet) || !isDateValid(dateAntipulgaspet)) {
                    Toast.makeText(getApplicationContext(), "Use el formato dd/mm/yyyy. La fecha no puede ser futura.", Toast.LENGTH_LONG).show();
                } else {
                    postPet(namepet, tipoMascotapet, agepet, genrepet, weightpet, nameVacunapet, dateVacunapet, dateAntiparasitariopet, dateAntipulgaspet);
                }

            }

        });
    }

    private void postPet(String namepet, String tipoMascotapet, String agepet, String genrepet, String weightpet, String nameVacunapet, String dateVacunapet, String dateAntiparasitariopet, String dateAntipulgaspet) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", namepet);
        map.put("tipoMascota", tipoMascotapet);
        map.put("age", agepet);
        map.put("genre", genrepet);
        map.put("weight", weightpet);
        map.put("nameVacuna", nameVacunapet);
        map.put("dateVacuna", dateVacunapet);
        map.put("dateAntiparasitario", dateAntiparasitariopet);
        map.put("dateAntipulgas", dateAntipulgaspet);

        mfirestore.collection("pet").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDateValid(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date enteredDate = sdf.parse(dateStr);
            return !enteredDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return false;
    }
}