package com.example.acm4ap_arabela_frankow.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acm4ap_arabela_frankow.Model.Pet;
import com.example.acm4ap_arabela_frankow.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PetAdapter extends FirestoreRecyclerAdapter<Pet, PetAdapter.ViewHolder> {
    public PetAdapter(@NonNull FirestoreRecyclerOptions<Pet> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Pet pet) {
        viewHolder.name.setText(pet.getName());
        viewHolder.age.setText(pet.getAge());
        viewHolder.color.setText(pet.getColor());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, color;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nombreView);
            age = itemView.findViewById(R.id.colorView);
            color = itemView.findViewById(R.id.edadView);
        }
    }
}
