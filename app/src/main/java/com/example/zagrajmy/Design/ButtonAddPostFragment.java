package com.example.zagrajmy.Design;

import android.content.Intent;
import android.widget.Toast;

import com.example.zagrajmy.PostsManagement.PostCreatingLogic;
import com.example.zagrajmy.R;

public class ButtonAddPostFragment extends BaseFragmentForAddPostButton {
    private Toast toast;

    @Override
    protected int getLayoutId() {
        return R.layout.button_fragment;
    }

    @Override
    protected void onButtonClicked() {
        Intent intent = new Intent(getActivity(), PostCreatingLogic.class);
        startActivity(intent);
    }

    @Override
    protected void showInfo() {
        toast = Toast.makeText(getActivity(), "Stwórz nowy post", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void hideInfo() {
        toast.cancel();
    }
}
