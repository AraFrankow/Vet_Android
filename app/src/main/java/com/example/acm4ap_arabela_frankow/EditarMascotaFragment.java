package com.example.acm4ap_arabela_frankow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarMascotaFragment extends Fragment {

    private static final String ARG_PET_ID = "pet_id";

    private TextInputEditText nombre, tipoMascota, edad, peso, genero, nombreVacuna, fechaVacuna, fechaAntiparasitario, fechaAntipulgas;
    private Button btnActualizar, btnCancelar;
    private FirebaseFirestore mFirestore;
    private String petId;

    public EditarMascotaFragment() {
        // Required empty public constructor
    }

    public static EditarMascotaFragment newInstance(String petId) {
        EditarMascotaFragment fragment = new EditarMascotaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PET_ID, petId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            petId = getArguments().getString(ARG_PET_ID);
        }
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_mascota, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nombre = view.findViewById(R.id.nombre);
        tipoMascota = view.findViewById(R.id.tipoMascota);
        edad = view.findViewById(R.id.edad);
        peso = view.findViewById(R.id.peso);
        genero = view.findViewById(R.id.genero);
        nombreVacuna = view.findViewById(R.id.nombreVacuna);
        fechaVacuna = view.findViewById(R.id.fechaVacuna);
        fechaAntiparasitario = view.findViewById(R.id.fechaAntiparasitario);
        fechaAntipulgas = view.findViewById(R.id.fechaAntipulgas);
        btnActualizar = view.findViewById(R.id.btn_actualizar);
        btnCancelar = view.findViewById(R.id.btn_cancelar);

        loadPetData();

        btnActualizar.setOnClickListener(v -> updatePet());
        btnCancelar.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void loadPetData() {
        if (petId != null) {
            mFirestore.collection("pet").document(petId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Pet pet = documentSnapshot.toObject(Pet.class);
                    if (pet != null) {
                        nombre.setText(pet.getName());
                        tipoMascota.setText(pet.getTipoMascota());
                        edad.setText(pet.getAge());
                        peso.setText(pet.getWeight());
                        genero.setText(pet.getGenre());
                        nombreVacuna.setText(pet.getNameVacuna());
                        fechaVacuna.setText(pet.getDateVacuna());
                        fechaAntiparasitario.setText(pet.getDateAntiparasitario());
                        fechaAntipulgas.setText(pet.getDateAntipulgas());
                    }
                }
            });
        }
    }

    private void updatePet() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", nombre.getText().toString());
        map.put("tipoMascota", tipoMascota.getText().toString());
        map.put("age", edad.getText().toString());
        map.put("weight", peso.getText().toString());
        map.put("genre", genero.getText().toString());
        map.put("nameVacuna", nombreVacuna.getText().toString());
        map.put("dateVacuna", fechaVacuna.getText().toString());
        map.put("dateAntiparasitario", fechaAntiparasitario.getText().toString());
        map.put("dateAntipulgas", fechaAntipulgas.getText().toString());

        mFirestore.collection("pet").document(petId).update(map)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Mascota actualizada", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
    }
}
