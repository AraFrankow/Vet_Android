package com.example.acm4ap_arabela_frankow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

    private TextView nombreDetail, infoBasicaDetail, antiparasitarioDetail, antipulgasDetail, antiparasitarioUso, antipulgasUso;
    private LinearLayout vacunasContainer;
    private Button btnVolver;
    private FirebaseFirestore mFirestore;
    private String petId;
    private ProgressBar progressBar;
    private ScrollView contentScrollView;

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
        antiparasitarioUso = view.findViewById(R.id.antiparasitario_uso);
        antipulgasUso = view.findViewById(R.id.antipulgas_uso);
        btnVolver = view.findViewById(R.id.btn_volver);
        progressBar = view.findViewById(R.id.progressBar_detail);
        contentScrollView = view.findViewById(R.id.content_scroll_view);
    }

    private void loadPetDetails() {
        if (petId == null) return;
        progressBar.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.INVISIBLE);

        mFirestore.collection("pet").document(petId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Pet pet = documentSnapshot.toObject(Pet.class);
                if (pet != null) {
                    populateBasicInfo(pet);
                    populateVaccineInfo(pet.getTipoMascota(), documentSnapshot);
                    calculateNextTreatmentDates(pet);
                    progressBar.setVisibility(View.GONE);
                    contentScrollView.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Mascota no encontrada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateBasicInfo(Pet pet) {
        nombreDetail.setText(pet.getName());
        String basicInfo = String.format(Locale.getDefault(),"%s, %s, %s años, %skg, %s",
                pet.getTipoMascota(), pet.getRace(), pet.getAge(), pet.getWeight(), pet.getGenre());
        infoBasicaDetail.setText(basicInfo);

        antiparasitarioUso.setText(String.format(Locale.getDefault(), "Última aplicación: %s", pet.getDateAntiparasitario()));
        antipulgasUso.setText(String.format(Locale.getDefault(), "Última aplicación: %s", pet.getDateAntipulgas()));
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

                TextView lastDateTextView = new TextView(getContext());
                lastDateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                lastDateTextView.setText(String.format(Locale.getDefault(), "Última %s: %s", vaccineName, revacunaDate));
                lastDateTextView.setTextSize(16f);
                if(vacunasContainer.getChildCount() > 0) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lastDateTextView.getLayoutParams();
                    params.setMargins(0, 16, 0, 0);
                    lastDateTextView.setLayoutParams(params);
                }
                vacunasContainer.addView(lastDateTextView);

                TextView countdownTextView = new TextView(getContext());
                countdownTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                countdownTextView.setTextSize(16f);
                vacunasContainer.addView(countdownTextView);

                calculateRevaccinationCountdown(countdownTextView, "Próximo " + vaccineName, revacunaDate);
            }
        }
    }

    private void calculateNextTreatmentDates(Pet pet) {
        final int diasParaAntiparasitario = 90;
        final int diasParaAntipulgasPerro = 30;
        final int diasParaAntipulgasGato = 120;

        calculateCountdown(antiparasitarioDetail, "Próximo antiparasitario:", pet.getDateAntiparasitario(), diasParaAntiparasitario);

        int diasParaAntipulgas;
        if ("Perro".equals(pet.getTipoMascota())) {
            diasParaAntipulgas = diasParaAntipulgasPerro;
        } else {
            diasParaAntipulgas = diasParaAntipulgasGato;
        }

        calculateCountdown(antipulgasDetail, "Próximo antipulgas:", pet.getDateAntipulgas(), diasParaAntipulgas);
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

            calculateCountdownToDate(textView, prefix, formatter.format(nextDate));

        } catch (ParseException e) {
            textView.setText(prefix + " (fecha inválida)");
        }
    }

    private void calculateRevaccinationCountdown(TextView textView, String prefix, String lastDateStr) {
        if (lastDateStr == null || lastDateStr.isEmpty()) {
            textView.setText(prefix + ": (no hay fecha registrada)");
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date lastDate = formatter.parse(lastDateStr);
            Calendar calendar = Calendar.getInstance();
            if (lastDate != null) {
                calendar.setTime(lastDate);
            }
            calendar.add(Calendar.YEAR, 1);
            Date nextDate = calendar.getTime();

            calculateCountdownToDate(textView, prefix, formatter.format(nextDate));

        } catch (ParseException e) {
            textView.setText(prefix + " (fecha inválida)");
        }
    }


    private void calculateCountdownToDate(TextView textView, String prefix, String futureDateStr) {
        if (futureDateStr == null || futureDateStr.isEmpty()) {
            textView.setText(prefix + ": (no hay fecha registrada)");
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date nextDate = formatter.parse(futureDateStr);

            Calendar calToday = Calendar.getInstance();
            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);
            calToday.set(Calendar.SECOND, 0);
            calToday.set(Calendar.MILLISECOND, 0);
            Date today = calToday.getTime();

            long diffInMillis = nextDate.getTime() - today.getTime();
            long daysRemaining = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black)); // Reset color
            if (daysRemaining > 0) {
                textView.setText(String.format(Locale.getDefault(), "%s en %d días", prefix, daysRemaining));
            } else if (daysRemaining == 0) {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                textView.setText(prefix + " ¡Vence hoy!");
            } else {
                long daysExpired = -daysRemaining;
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                textView.setText(String.format(Locale.getDefault(), "%s vencido hace %d días", prefix, daysExpired));
            }
        } catch (ParseException e) {
            textView.setText(prefix + ": (fecha inválida)");
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
