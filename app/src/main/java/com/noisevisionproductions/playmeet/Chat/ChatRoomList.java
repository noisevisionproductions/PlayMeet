package com.noisevisionproductions.playmeet.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.noisevisionproductions.playmeet.Adapters.ListOfChatRoomsAdapter;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;

public class ChatRoomList extends Fragment {
    private ListOfChatRoomsAdapter listOfChatRoomsAdapter;
    private ProgressBar loadMorePostsIndicator;
    private AppCompatTextView noChatRoomsFound;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_of_chatrooms, container, false);
        loadMorePostsIndicator = view.findViewById(R.id.loadMorePostsIndicator);
        loadMorePostsIndicator.setVisibility(View.VISIBLE);
        noChatRoomsFound = view.findViewById(R.id.noChatRoomsFound);

        showChatRooms();
        setRecyclerView(view);

        return view;
    }

    public void setRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChatRoomList);

        loadMorePostsIndicator.setVisibility(View.GONE);
        recyclerView.setAdapter(listOfChatRoomsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void showChatRooms() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();

        Query query = firebaseHelper.getDatabaseReference().child("ChatRooms").orderByChild("participants/" + currentUserId).equalTo(true);
        FirebaseRecyclerOptions<ChatRoomModel> options = new FirebaseRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class)
                .build();

        listOfChatRoomsAdapter = new ListOfChatRoomsAdapter(this::onChatClicked, options, getContext());

        listOfChatRoomsAdapter.startListening();

        listOfChatRoomsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                loadMorePostsIndicator.setVisibility(View.GONE);
                if (listOfChatRoomsAdapter.getItemCount() == 0) {
                    noChatRoomsFound.setVisibility(View.VISIBLE);
                } else {
                    noChatRoomsFound.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onChatClicked(ChatRoomModel chat) {
        // gdy użytkownik kliknie w wybrany czat room z listy, to go do niego przenosi
        Intent intent = new Intent(requireView().getContext(), ChatActivity.class);
        intent.putExtra("roomId", chat.getRoomId());
        startActivity(intent);
    }
}

