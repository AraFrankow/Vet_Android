package com.example.acm4ap_arabela_frankow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PetDetailFragment extends Fragment {

    private static final String ARG_PET_ID = "pet_id";

    private TextView nombreDetail, infoBasicaDetail, antiparasitarioDetail, antipulgasDetail;
    private LinearLayout vacunasContainer;
    private Button btnVolver;
    private FirebaseFirestore mFirestore;
    private String petId;

    public static PetDetailFragment newInstance(String petId) {
        PetDetailFragment fragment = new PetDetailFragment();
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
        return inflater.inflate(R.layout.fragment_pet_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        loadPetDetails();

        btnVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupViews(View view) {
        nombreDetail = view.findViewById(R.id.nombre_detail);
        infoBasicaDetail = view.findViewById(R.id.info_basica_detail);
        vacunasContainer = view.findViewById(R.id.vacunas_container_detail);
        antiparasitarioDetail = view.findViewById(R.id.antiparasitario_detail);
        antipulgasDetail = view.findViewById(R.id.antipulgas_detail);
        btnVolver = view.findViewById(R.id.btn_volver);
    }

    private void loadPetDetails() {
        if (petId == null) return;

        mFirestore.collection("pet").document(petId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Pet pet = documentSnapshot.toObject(Pet.class);
                if (pet != null) {
                    populateBasicInfo(pet);
                    populateVaccineInfo(pet.getTipoMascota(), documentSnapshot);
                    calculateNextTreatmentDates(pet);
                }
            } else {
                Toast.makeText(getContext(), "Mascota no encontrada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateBasicInfo(Pet pet) {
        nombreDetail.setText(pet.getName());
        String basicInfo = String.format(Locale.getDefault(),"%s, %s años, %skg, %s",
                pet.getTipoMascota(), pet.getAge(), pet.getWeight(), pet.getGenre());
        infoBasicaDetail.setText(basicInfo);
    }

    private void populateVaccineInfo(String petType, DocumentSnapshot snapshot) {
        vacunasContainer.removeAllViews();
        Map<String, String> vaccines = getVaccineMap(petType);

        for (Map.Entry<String, String> entry : vaccines.entrySet()) {
            String vaccineField = entry.getKey();
            String vaccineName = entry.getValue();
            Boolean isApplied = snapshot.getBoolean(vaccineField);

            if (isApplied != null && isApplied) {
                String revacunaDate = snapshot.getString(vaccineField + "_revacuna");
                addVaccineTextView(vaccineName, revacunaDate);
            }
        }
    }

    private void addVaccineTextView(String vaccineName, String date) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(String.format(Locale.getDefault()," • %s - Próxima dosis: %s", vaccineName, (date != null && !date.isEmpty() ? date : "N/A")));
        textView.setTextSize(16f);
        vacunasContainer.addView(textView);
    }

    private void calculateNextTreatmentDates(Pet pet) {
        final int diasParaAntiparasitario = 90;
        final int diasParaAntipulgasPerro = 30;
        final int diasParaAntipulgasGato = 120;

        calculateCountdown(antiparasitarioDetail, "Próximo antiparasitario en:", pet.getDateAntiparasitario(), diasParaAntiparasitario);

        int diasParaAntipulgas;
        if ("Perro".equals(pet.getTipoMascota())) {
            diasParaAntipulgas = diasParaAntipulgasPerro;
        } else {
            diasParaAntipulgas = diasParaAntipulgasGato;
        }

        calculateCountdown(antipulgasDetail, "Próximo antipulgas en:", pet.getDateAntipulgas(), diasParaAntipulgas);
    }

    private void calculateCountdown(TextView textView, String prefix, String lastDateStr, int daysToAdd) {
        if (lastDateStr == null || lastDateStr.isEmpty()) {
            textView.setText(prefix + " (no hay fecha registrada)");
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date lastDate = formatter.parse(lastDateStr);
            Calendar calendar = Calendar.getInstance();
            if (lastDate != null) {
                calendar.setTime(lastDate);
            }
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
            Date nextDate = calendar.getTime();

            Date today = new Date();
            long diffInMillis = nextDate.getTime() - today.getTime();
            long daysRemaining = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black)); // Reset color
            if (daysRemaining > 0) {
                textView.setText(String.format(Locale.getDefault(),"%s %d días", prefix, daysRemaining));
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                textView.setText(prefix + " ¡Vencido!");
            }
        } catch (ParseException e) {
            textView.setText(prefix + " (fecha inválida)");
        }
    }

    private Map<String, String> getVaccineMap(String petType) {
        Map<String, String> vaccines = new LinkedHashMap<>();
        if ("Perro".equals(petType)) {
            vaccines.put("vacuna_rabia", "Rabia");
            vaccines.put("vacuna_parvovirus", "Parvovirus");
            vaccines.put("vacuna_moquillo", "Moquillo");
            vaccines.put("vacuna_hepatitis", "Hepatitis");
            vaccines.put("vacuna_leptospirosis", "Leptospirosis");
        } else if ("Gato".equals(petType)) {
            vaccines.put("vacuna_trivalente", "Trivalente Felina");
            vaccines.put("vacuna_leucemia", "Leucemia Felina");
            vaccines.put("vacuna_rabia_gato", "Rabia");
        }
        return vaccines;
    }
}
