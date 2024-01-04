package com.example.zagrajmy.FirstSetup;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zagrajmy.R;

import java.util.Objects;

import javax.annotation.Nullable;

public class ContainerForDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // tworzenie pierwszego fragmentu po wywolaniu DialogFragment
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        ChildFragmentNickname childFragmentNickname = new ChildFragmentNickname();
        fragmentTransaction.replace(R.id.fragment_container, childFragmentNickname, "tag_child_fragment_nickname");
        fragmentTransaction.commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Animacja pokazywania sie DialogFragment
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogSlideAnim;

        // Dodanie mojego DialogFragment
        dialog.setContentView(R.layout.container_dialog_fragment);

        // Ustawiam na przezroczystość, aby nie było białego tła na fragmentach
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Zapobieganie zniknięciu DialogFragment po kliknięciu poza niego
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}

