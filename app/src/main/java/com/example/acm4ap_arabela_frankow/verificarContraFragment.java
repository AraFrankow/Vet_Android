package com.example.acm4ap_arabela_frankow;

import android.content.Context;
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

public class verificarContraFragment extends Fragment {

    public interface OnAccountDeletedListener {
        void onAccountDeleted();
    }

    private OnAccountDeletedListener mListener;

    private TextInputEditText passwordInput;
    private Button btnConfirmDelete, btnCancel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    public verificarContraFragment() {
        // Required empty public constructor
    }

    public static verificarContraFragment newInstance() {
        return new verificarContraFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountDeletedListener) {
            mListener = (OnAccountDeletedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAccountDeletedListener");
        }
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
        return inflater.inflate(R.layout.fragment_verificar_contra, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordInput = view.findViewById(R.id.password_input);
        btnConfirmDelete = view.findViewById(R.id.btn_confirm_delete);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnConfirmDelete.setOnClickListener(v -> confirmAndDelete());
    }

    private void confirmAndDelete() {
        String password = passwordInput.getText().toString();
        if (password.isEmpty()) {
            passwordInput.setError("La contraseña es necesaria");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteUserFromFirestore();
            } else {
                passwordInput.setError("La contraseña es incorrecta");
                Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserFromFirestore() {
        String uid = currentUser.getUid();
        mFirestore.collection("user").document(uid).delete()
                .addOnCompleteListener(task -> {
                    mFirestore.collection("user").document(uid).delete()
                            .addOnCompleteListener(fallbackTask -> {
                                deleteUserFromAuth();
                            });
                });
    }

    private void deleteUserFromAuth() {
        currentUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show();
                if (mListener != null) {
                    mListener.onAccountDeleted();
                }
            } else {
                Toast.makeText(getContext(), "Error al eliminar la cuenta.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}