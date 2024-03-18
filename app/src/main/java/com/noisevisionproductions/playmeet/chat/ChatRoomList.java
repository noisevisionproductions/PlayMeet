package com.noisevisionproductions.playmeet.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.ListOfChatRoomsAdapter;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;

public class ChatRoomList extends Fragment {
    private ListOfChatRoomsAdapter listOfChatRoomsAdapter;
    private AppCompatTextView noChatRoomsFound;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_chatrooms, container, false);
        noChatRoomsFound = view.findViewById(R.id.noChatRoomsFound);

        setRecyclerView(view);

        return view;
    }

    public void setRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recyclerViewChatRoomList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (listOfChatRoomsAdapter == null) {
            initializeChatRoomsAdapter();
        }
        recyclerView.setAdapter(listOfChatRoomsAdapter);
    }

    private void initializeChatRoomsAdapter() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();

            Query query = firebaseHelper.getDatabaseReference()
                    .child("ChatRooms")
                    .orderByChild("participants/" + currentUserId)
                    .equalTo(true);
            checkForExistingRooms(query);

            FirebaseRecyclerOptions<ChatRoomModel> options = new FirebaseRecyclerOptions.Builder<ChatRoomModel>()
                    .setQuery(query, ChatRoomModel.class)
                    .build();

            listOfChatRoomsAdapter = new ListOfChatRoomsAdapter(this::onChatClicked, options, getContext());

            listOfChatRoomsAdapter.startListening();
        }
    }

    private void checkForExistingRooms(@NonNull Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    noChatRoomsFound.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    noChatRoomsFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onChatClicked(@NonNull ChatRoomModel chat) {
        // gdy użytkownik kliknie w wybrany czat room z listy, to go do niego przenosi
        Intent intent = new Intent(requireView().getContext(), ChatActivity.class);
        intent.putExtra("roomId", chat.getRoomId());
        startActivity(intent);
    }
}

