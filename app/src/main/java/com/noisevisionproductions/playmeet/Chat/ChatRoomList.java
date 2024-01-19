package com.noisevisionproductions.playmeet.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.Adapters.ListOfChatRoomsAdapter;
import com.noisevisionproductions.playmeet.R;

public class ChatRoomList extends Fragment {
    private ListOfChatRoomsAdapter listOfChatRoomsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_of_chatrooms, container, false);

        setupFirebase(view);

        return view;
    }

    public void setupFirebase(View view) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();
            DatabaseReference chatRoomsReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId).child("ChatRooms");
            FirebaseRecyclerOptions<ChatRoomModel> options = new FirebaseRecyclerOptions.Builder<ChatRoomModel>()
                    .setQuery(chatRoomsReference, ChatRoomModel.class)
                    .build();

            listOfChatRoomsAdapter = new ListOfChatRoomsAdapter(this::onChatClicked, options, getContext());
            setRecyclerView(view);
        }
    }

    public void setRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChatRoomList);
        recyclerView.setAdapter(listOfChatRoomsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void onChatClicked(ChatRoomModel chat) {
        // gdy użytkownik kliknie w wybrany czat room z listy, to go do niego przenosi
        Intent intent = new Intent(requireView().getContext(), ChatActivity.class);
        intent.putExtra("roomId", chat.getRoomId());
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (listOfChatRoomsAdapter != null) {
            listOfChatRoomsAdapter.startListening();
        }
    }
}

