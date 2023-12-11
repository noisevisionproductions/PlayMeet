package com.example.zagrajmy.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.R;

import io.realm.Realm;

public class ChatRoomList extends Fragment {
    private Realm realm;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.activity_list_of_chatrooms, container, false);

        randomChatButton(currentView);
        return currentView;
    }

    public void randomChatButton(View view) {
        AppCompatButton button = view.findViewById(R.id.randomChatButton);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            startActivity(intent);
        });
    }
}
