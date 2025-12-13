package com.example.acm4ap_arabela_frankow.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.example.acm4ap_arabela_frankow.R;
import com.example.acm4ap_arabela_frankow.verificarContraFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Locale;

public class PetAdapter extends FirestoreRecyclerAdapter<Pet, PetAdapter.ViewHolder> {

    public interface OnPetInteractionListener {
        void onEditPet(String petId);
        void onViewPet(String petId);
    }

    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final Activity activity;
    private final OnPetInteractionListener listener;

    public PetAdapter(@NonNull FirestoreRecyclerOptions<Pet> options, Activity activity, OnPetInteractionListener listener) {
        super(options);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Pet pet) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            return; 
        }

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(pet.getName());
        viewHolder.tipoMascota.setText(pet.getTipoMascota());
        viewHolder.age.setText(String.format(Locale.getDefault(), "Edad: %s", pet.getAge()));
        viewHolder.btn_delete.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("Eliminar mascota")
                    .setMessage("¿Estás seguro de que quieres eliminar esta mascota? Esta acción no se puede deshacer.")
                    .setPositiveButton("Continuar", (dialog, which) -> {
                        deletePet(id);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        viewHolder.btn_edit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditPet(id);
            }
        });
        viewHolder.btn_ver.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewPet(id);
            }
        });
        viewHolder.name.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewPet(id);
            }
        });
        viewHolder.icon_pet.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewPet(id);
            }
        });
    }

    private void deletePet(String id) {
        mFirestore.collection("pet").document(id).delete()
                .addOnSuccessListener(unused -> Toast.makeText(activity, "Eliminado exitosamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_single, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, tipoMascota, age;
        ImageView btn_delete, btn_edit, btn_ver, icon_pet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nombreView);
            tipoMascota = itemView.findViewById(R.id.tipoMascotaView);
            age = itemView.findViewById(R.id.edadView);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);
            btn_ver = itemView.findViewById(R.id.btn_ver);
            icon_pet = itemView.findViewById(R.id.icon_pet);
        }
    }
}
