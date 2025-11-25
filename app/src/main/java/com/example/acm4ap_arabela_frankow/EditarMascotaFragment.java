package com.example.acm4ap_arabela_frankow;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarMascotaFragment extends DialogFragment{

    String id_pet;
    Button btn_agregar;
    EditText name, tipoMascota, age, weight, genre, nameVacuna, dateVacuna, dateAntiparasitario, dateAntipulgas;
    private FirebaseFirestore mfirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            id_pet = getArguments().getString("id_pet");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editar_mascota, container, false);
        mfirestore = FirebaseFirestore.getInstance();

        name = v.findViewById(R.id.nombre);
        tipoMascota = v.findViewById(R.id.tipoMascota);
        age = v.findViewById(R.id.edad);
        genre = v.findViewById(R.id.genero);
        weight = v.findViewById(R.id.peso);
        nameVacuna = v.findViewById(R.id.nombreVacuna);
        dateVacuna = v.findViewById(R.id.fechaVacuna);
        dateAntiparasitario = v.findViewById(R.id.fechaAntiparasitario);
        dateAntipulgas = v.findViewById(R.id.fechaAntipulgas);
        btn_agregar = v.findViewById(R.id.btn_registro);

        if(id_pet==null || id_pet.isEmpty()){
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
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    }else{
                        postPet(namepet, tipoMascotapet, agepet, genrepet, weightpet, nameVacunapet, dateVacunapet, dateAntiparasitariopet, dateAntipulgaspet);
                    }
                }
            });
        }else{
            getPet();
            btn_agregar.setText("update");
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
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    } else if (!isDateValid(dateVacunapet) || !isDateValid(dateAntiparasitariopet) || !isDateValid(dateAntipulgaspet)) {
                        Toast.makeText(getContext(), "Use el formato dd/mm/yyyy. La fecha no puede ser futura.", Toast.LENGTH_LONG).show();
                    } else{
                        updatePet(namepet, tipoMascotapet, agepet, genrepet, weightpet, nameVacunapet, dateVacunapet, dateAntiparasitariopet, dateAntipulgaspet);
                    }

                }

            });
        }



        return v;
    }

    private void updatePet(String namepet, String tipoMascotapet, String agepet, String genrepet, String weightpet, String nameVacunapet, String dateVacunapet, String dateAntiparasitariopet, String dateAntipulgaspet) {
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

        mfirestore.collection("pet").document(id_pet).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPet(){
        mfirestore.collection("pet").document(id_pet).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String namepet = documentSnapshot.getString("name");
                String tipoMascotapet = documentSnapshot.getString("tipoMascota");
                String agepet = documentSnapshot.getString("age");
                String genrepet = documentSnapshot.getString("genre");
                String weightpet = documentSnapshot.getString("weight");
                String nameVacunapet = documentSnapshot.getString("nameVacuna");
                String dateVacunapet = documentSnapshot.getString("dateVacuna");
                String dateAntiparasitariopet = documentSnapshot.getString("dateAntiparasitario");
                String dateAntipulgaspet = documentSnapshot.getString("dateAntipulgas");
                name.setText(namepet);
                tipoMascota.setText(tipoMascotapet);
                age.setText(agepet);
                genre.setText(genrepet);
                weight.setText(weightpet);
                nameVacuna.setText(nameVacunapet);
                dateVacuna.setText(dateVacunapet);
                dateAntiparasitario.setText(dateAntiparasitariopet);
                dateAntipulgas.setText(dateAntipulgaspet);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
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
}