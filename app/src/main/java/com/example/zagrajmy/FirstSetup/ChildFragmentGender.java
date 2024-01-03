package com.example.zagrajmy.FirstSetup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.zagrajmy.R;

public class ChildFragmentGender extends Fragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_gender, container, false);


        return view;
    }

    public interface ChildFragmentGenderListener {
        void onGenderSelected();
    }

    void onButtonPressed() {
        ChildFragmentGenderListener listener = (ChildFragmentGenderListener) getParentFragment();
        if (listener != null) {
            listener.onGenderSelected(/* get gender input */);
        }
    }
}
