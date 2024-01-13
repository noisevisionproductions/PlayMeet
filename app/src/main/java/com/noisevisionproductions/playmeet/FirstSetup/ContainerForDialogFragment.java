package com.noisevisionproductions.playmeet.FirstSetup;

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

import com.noisevisionproductions.playmeet.R;

import java.util.Objects;

import javax.annotation.Nullable;

//klasa, która przechowuje DialogFragment w kontekście tworzenia profilu dla nowo zalogowanych użytkowników
public class ContainerForDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_container_first_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // tworzenie pierwszego fragmentu po wywolaniu DialogFragment
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        ChildFragmentNickname childFragmentNickname = new ChildFragmentNickname();
       // fragment do ustawiania nicku pojawia się jako pierwszy fragmentTransaction.replace(R.id.fragment_container, childFragmentNickname, "tag_child_fragment_nickname");
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
        dialog.setContentView(R.layout.dialog_fragment_container_first_login);

        // Ustawiam na przezroczystość, aby nie było białego tła na fragmentach
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Zapobieganie zniknięciu DialogFragment po kliknięciu poza niego
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}

