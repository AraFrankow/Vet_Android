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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditarUsuarioFragment extends Fragment {

    private TextInputEditText editName, currentPassword, editPassword, confirmPassword;
    private Button btnSaveChanges, btnCancel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    public EditarUsuarioFragment() {
        // Required empty public constructor
    }

    public static EditarUsuarioFragment newInstance() {
        return new EditarUsuarioFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.edit_name);
        currentPassword = view.findViewById(R.id.current_password);
        editPassword = view.findViewById(R.id.edit_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
        btnCancel = view.findViewById(R.id.btn_cancel);

        loadUserData();

        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void loadUserData() {
        if (currentUser != null) {
            mFirestore.collection("user").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    editName.setText(name);
                }
            });
        }
    }

    private void saveChanges() {
        String newName = editName.getText().toString().trim();
        String currentPass = currentPassword.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        boolean isNameChanged = !newName.isEmpty();
        boolean isPasswordChanged = !newPassword.isEmpty();

        if (!isNameChanged && !isPasswordChanged) {
            Toast.makeText(getContext(), "No hay cambios que guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPasswordChanged) {
            if (currentPass.isEmpty()) {
                currentPassword.setError("Introduce tu contraseña actual para cambiarla");
                return;
            }
            if (!newPassword.equals(confirmPass)) {
                confirmPassword.setError("Las contraseñas no coinciden");
                return;
            }
            if (newPassword.length() < 6) {
                editPassword.setError("La contraseña debe tener al menos 6 caracteres");
                return;
            }

            reauthenticateAndProceed(currentPass, newName, newPassword);

        } else {
            updateNameOnly(newName);
        }
    }

    private void reauthenticateAndProceed(String currentPass, String newName, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPass);

        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!newName.isEmpty()) {
                    updateNameOnly(newName);
                }
                updatePasswordOnly(newPassword);
                Toast.makeText(getContext(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();

            } else {
                currentPassword.setError("La contraseña actual es incorrecta");
                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateNameOnly(String newName) {
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("name", newName);

        mFirestore.collection("user").document(currentUser.getUid())
                .set(nameMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Nombre actualizado con éxito", Toast.LENGTH_SHORT).show();
                    if (editPassword.getText().toString().trim().isEmpty()) {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar el nombre: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void updatePasswordOnly(String newPassword) {
        currentUser.updatePassword(newPassword).addOnFailureListener(e -> 
            Toast.makeText(getContext(), "Error interno al actualizar contraseña: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
