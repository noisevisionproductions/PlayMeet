package com.example.zagrajmy.FirstSetup;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zagrajmy.R;

import java.util.Objects;

import javax.annotation.Nullable;

public class ContainerForDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.container_dialog_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        ChildFragmentNickname childFragmentNickname = new ChildFragmentNickname();
        fragmentTransaction.replace(R.id.fragment_container, childFragmentNickname);
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

        return dialog;
    }
}

