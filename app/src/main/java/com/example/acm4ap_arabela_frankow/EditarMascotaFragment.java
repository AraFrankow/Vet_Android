package com.example.acm4ap_arabela_frankow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditarMascotaFragment extends Fragment {

    private static final String ARG_PET_ID = "pet_id";

    private TextInputEditText nombre, tipoMascota, edad, peso, genero, fechaAntiparasitario, fechaAntipulgas, otraRaza;
    private AutoCompleteTextView race;
    private TextInputLayout layoutOtraRaza;
    private Button btnActualizar, btnCancelar;
    private FirebaseFirestore mFirestore;
    private String petId;
    private ProgressBar progressBar;

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

        setupViews(view);
        setupListeners();

        loadPetData();

        btnActualizar.setOnClickListener(v -> updatePet());
        btnCancelar.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupViews(View view) {
        nombre = view.findViewById(R.id.nombre);
        tipoMascota = view.findViewById(R.id.tipoMascota);
        edad = view.findViewById(R.id.edad);
        race = view.findViewById(R.id.raza_edit);
        layoutOtraRaza = view.findViewById(R.id.layout_otra_raza_edit);
        otraRaza = view.findViewById(R.id.otra_raza_edit);
        peso = view.findViewById(R.id.peso);
        genero = view.findViewById(R.id.genero);
        fechaAntiparasitario = view.findViewById(R.id.fechaAntiparasitario);
        fechaAntipulgas = view.findViewById(R.id.fechaAntipulgas);
        btnActualizar = view.findViewById(R.id.btn_actualizar);
        btnCancelar = view.findViewById(R.id.btn_cancelar);
        progressBar = view.findViewById(R.id.progressBar_edit);

        vacunasPerroContainer = view.findViewById(R.id.vacunas_perro_container_edit);
        vacunasGatoContainer = view.findViewById(R.id.vacunas_gato_container_edit);
        labelRevacunaPerro = view.findViewById(R.id.label_revacuna_perro_edit);
        labelRevacunaGato = view.findViewById(R.id.label_revacuna_gato_edit);

        // Vistas de vacunas para perros
        cbRabiaPerro = view.findViewById(R.id.cb_rabia_perro_edit);
        layoutRevacunaRabiaPerro = view.findViewById(R.id.layout_revacuna_rabia_perro_edit);
        fechaRevacunaRabiaPerro = view.findViewById(R.id.fecha_revacuna_rabia_perro_edit);
        cbParvovirus = view.findViewById(R.id.cb_parvovirus_edit);
        layoutRevacunaParvovirus = view.findViewById(R.id.layout_revacuna_parvovirus_edit);
        fechaRevacunaParvovirus = view.findViewById(R.id.fecha_revacuna_parvovirus_edit);
        cbMoquillo = view.findViewById(R.id.cb_moquillo_edit);
        layoutRevacunaMoquillo = view.findViewById(R.id.layout_revacuna_moquillo_edit);
        fechaRevacunaMoquillo = view.findViewById(R.id.fecha_revacuna_moquillo_edit);
        cbHepatitis = view.findViewById(R.id.cb_hepatitis_edit);
        layoutRevacunaHepatitis = view.findViewById(R.id.layout_revacuna_hepatitis_edit);
        fechaRevacunaHepatitis = view.findViewById(R.id.fecha_revacuna_hepatitis_edit);
        cbLeptospirosis = view.findViewById(R.id.cb_leptospirosis_edit);
        layoutRevacunaLeptospirosis = view.findViewById(R.id.layout_revacuna_leptospirosis_edit);
        fechaRevacunaLeptospirosis = view.findViewById(R.id.fecha_revacuna_leptospirosis_edit);

        // Vistas de vacunas para gatos
        cbTrivalente = view.findViewById(R.id.cb_trivalente_edit);
        layoutRevacunaTrivalente = view.findViewById(R.id.layout_revacuna_trivalente_edit);
        fechaRevacunaTrivalente = view.findViewById(R.id.fecha_revacuna_trivalente_edit);
        cbLeucemia = view.findViewById(R.id.cb_leucemia_edit);
        layoutRevacunaLeucemia = view.findViewById(R.id.layout_revacuna_leucemia_edit);
        fechaRevacunaLeucemia = view.findViewById(R.id.fecha_revacuna_leucemia_edit);
        cbRabiaGato = view.findViewById(R.id.cb_rabia_gato_edit);
        layoutRevacunaRabiaGato = view.findViewById(R.id.layout_revacuna_rabia_gato_edit);
        fechaRevacunaRabiaGato = view.findViewById(R.id.fecha_revacuna_rabia_gato_edit);
    }

    private void setupListeners() {
        race.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRace = (String) parent.getItemAtPosition(position);
            if ("Otro".equals(selectedRace)) {
                layoutOtraRaza.setVisibility(View.VISIBLE);
            } else {
                layoutOtraRaza.setVisibility(View.GONE);
            }
        });

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
                        fechaAntiparasitario.setText(pet.getDateAntiparasitario());
                        fechaAntipulgas.setText(pet.getDateAntipulgas());

                        String[] races;
                        if ("Perro".equals(pet.getTipoMascota())) {
                            races = new String[]{"Border Collie", "Labrador", "Braco", "Bulldog", "Caniche", "Chihuahua", "Cocker", "Golden Retriever", "Schnauzer", "Galgo", "Otro"};
                        } else {
                            races = new String[]{"Siamés", "Persa", "Bengala", "Maine Coon", "Criollo", "Korat", "Otro"};
                        }
                        ArrayAdapter<String> raceAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, races);
                        race.setAdapter(raceAdapter);

                        // Decir la otra raza del animal
                        if (Arrays.asList(races).contains(pet.getRace())) {
                            race.setText(pet.getRace(), false);
                        } else {
                            race.setText("Otro", false);
                            layoutOtraRaza.setVisibility(View.VISIBLE);
                            otraRaza.setText(pet.getRace());
                        }

                        if ("Perro".equals(pet.getTipoMascota())) {
                            vacunasPerroContainer.setVisibility(View.VISIBLE);
                            loadVaccineState(cbRabiaPerro, fechaRevacunaRabiaPerro, documentSnapshot, "vacuna_rabia");
                            loadVaccineState(cbParvovirus, fechaRevacunaParvovirus, documentSnapshot, "vacuna_parvovirus");
                            loadVaccineState(cbMoquillo, fechaRevacunaMoquillo, documentSnapshot, "vacuna_moquillo");
                            loadVaccineState(cbHepatitis, fechaRevacunaHepatitis, documentSnapshot, "vacuna_hepatitis");
                            loadVaccineState(cbLeptospirosis, fechaRevacunaLeptospirosis, documentSnapshot, "vacuna_leptospirosis");
                            updatePerroLabelVisibility();
                        } else if ("Gato".equals(pet.getTipoMascota())) {
                            vacunasGatoContainer.setVisibility(View.VISIBLE);
                            loadVaccineState(cbTrivalente, fechaRevacunaTrivalente, documentSnapshot, "vacuna_trivalente");
                            loadVaccineState(cbLeucemia, fechaRevacunaLeucemia, documentSnapshot, "vacuna_leucemia");
                            loadVaccineState(cbRabiaGato, fechaRevacunaRabiaGato, documentSnapshot, "vacuna_rabia_gato");
                            updateGatoLabelVisibility();
                        }
                    }
                }
            });
        }
    }

    private void loadVaccineState(CheckBox checkBox, TextInputEditText dateInput, com.google.firebase.firestore.DocumentSnapshot snapshot, String vaccineField) {
        Boolean isChecked = snapshot.getBoolean(vaccineField);
        checkBox.setChecked(isChecked != null && isChecked);
        if (checkBox.isChecked()) {
            dateInput.setText(snapshot.getString(vaccineField + "_revacuna"));
        }
    }

    private void updatePet() {
        if (nombre.getText().toString().isEmpty() || tipoMascota.getText().toString().isEmpty() || race.getText().toString().trim().isEmpty() || edad.getText().toString().isEmpty() || genero.getText().toString().isEmpty() || peso.getText().toString().isEmpty() || fechaAntiparasitario.getText().toString().isEmpty() || fechaAntipulgas.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Ingresar todos los datos básicos", Toast.LENGTH_SHORT).show();
            return;
        }
        int ageVal;
        double weightVal;

        try {
            ageVal = Integer.parseInt(edad.getText().toString());
            weightVal = Double.parseDouble(peso.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "La edad y el peso deben ser números válidos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Perro".equals(tipoMascota.getText().toString())) {
            if (ageVal > 30) {
                Toast.makeText(getContext(), "La edad no puede ser mayor a 30 años", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weightVal > 100) {
                Toast.makeText(getContext(), "El peso no puede ser mayor a 100 kg", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if ("Gato".equals(tipoMascota.getText().toString())) {
            if (ageVal > 30) {
                Toast.makeText(getContext(), "La edad no puede ser mayor a 30 años", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weightVal > 50) {
                Toast.makeText(getContext(), "El peso no puede ser mayor a 50 kg", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", nombre.getText().toString());
        map.put("age", edad.getText().toString());
        map.put("weight", peso.getText().toString());
        map.put("genre", genero.getText().toString());
        map.put("dateAntiparasitario", fechaAntiparasitario.getText().toString());
        map.put("dateAntipulgas", fechaAntipulgas.getText().toString());

        String raceSelected = race.getText().toString().trim();
        String finalRace;
        if ("Otro".equals(raceSelected)) {
            finalRace = otraRaza.getText().toString().trim();
        } else {
            finalRace = raceSelected;
        }
        map.put("race", finalRace);
        String petType = tipoMascota.getText().toString();

        if ("Perro".equals(petType)) {
            addVaccineDataToMap(map, "vacuna_rabia", cbRabiaPerro, fechaRevacunaRabiaPerro);
            addVaccineDataToMap(map, "vacuna_parvovirus", cbParvovirus, fechaRevacunaParvovirus);
            addVaccineDataToMap(map, "vacuna_moquillo", cbMoquillo, fechaRevacunaMoquillo);
            addVaccineDataToMap(map, "vacuna_hepatitis", cbHepatitis, fechaRevacunaHepatitis);
            addVaccineDataToMap(map, "vacuna_leptospirosis", cbLeptospirosis, fechaRevacunaLeptospirosis);
        } else if ("Gato".equals(petType)) {
            addVaccineDataToMap(map, "vacuna_trivalente", cbTrivalente, fechaRevacunaTrivalente);
            addVaccineDataToMap(map, "vacuna_leucemia", cbLeucemia, fechaRevacunaLeucemia);
            addVaccineDataToMap(map, "vacuna_rabia_gato", cbRabiaGato, fechaRevacunaRabiaGato);
        }

        progressBar.setVisibility(View.VISIBLE);
        mFirestore.collection("pet").document(petId).update(map)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Mascota actualizada", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
    }

    private void addVaccineDataToMap(Map<String, Object> map, String vaccineName, CheckBox checkBox, TextInputEditText dateInput) {
        map.put(vaccineName, checkBox.isChecked());
        if (checkBox.isChecked()) {
            map.put(vaccineName + "_revacuna", dateInput.getText().toString().trim());
        } else {
            map.put(vaccineName + "_revacuna", null);
        }
    }

    private void updatePerroLabelVisibility() {
        boolean anyCheckboxChecked = cbRabiaPerro.isChecked() || cbParvovirus.isChecked() || cbMoquillo.isChecked() || cbHepatitis.isChecked() || cbLeptospirosis.isChecked();
        labelRevacunaPerro.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }

    private void updateGatoLabelVisibility() {
        boolean anyCheckboxChecked = cbTrivalente.isChecked() || cbLeucemia.isChecked() || cbRabiaGato.isChecked();
        labelRevacunaGato.setVisibility(anyCheckboxChecked ? View.VISIBLE : View.GONE);
    }
}
