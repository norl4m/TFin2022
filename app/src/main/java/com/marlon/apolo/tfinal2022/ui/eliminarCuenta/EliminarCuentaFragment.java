package com.marlon.apolo.tfinal2022.ui.eliminarCuenta;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;

import java.util.Objects;

public class EliminarCuentaFragment extends Fragment {

    private EliminarCuentaViewModel mViewModel;

    public static EliminarCuentaFragment newInstance() {
        return new EliminarCuentaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.eliminar_cuenta_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EliminarCuentaViewModel.class);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        filterProvider(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
//        firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Intent intent = new Intent(requireActivity(), MainNavigationActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    requireActivity().startActivity(intent);
//                } else {
//                    /*errores*/
//                }
//            }
//        });
        // TODO: Use the ViewModel
    }

    private void filterProvider(FirebaseUser firebaseUser) {
        String provider = firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId();
        if (provider.contains("google.com")) {
            //Toast.makeText(requireActivity(), "Google", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireActivity(), EliminarInfoGoogleActivity.class));
        }
        if (provider.contains("phone")) {
            //Toast.makeText(requireActivity(), "Phone", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireActivity(), EliminarInfoPhoneActivity.class));

        }
        if (provider.contains("password")) {
            //Toast.makeText(requireActivity(), "Password", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireActivity(), EliminarInfoEmailActivity.class));

        }
        requireActivity().finish();
    }

}