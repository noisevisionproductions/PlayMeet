package com.example.zagrajmy.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.ListOfChatRoomsAdapter;
import com.example.zagrajmy.DataManagement.ChatRoomDiffUtil;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChatRoomList extends Fragment {
    private Realm realm;
    private final List<PrivateChatModel> chatRoomsList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.activity_list_of_chatrooms, container, false);

        createChatRoomList(currentView);
        randomChatButton(currentView);
        return currentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void onChatClicked(PrivateChatModel chat) {
        Intent intent = new Intent(requireView().getContext(), ChatActivity.class);
        intent.putExtra("roomId", chat.getRoomId());
        startActivity(intent);
    }

    public void createChatRoomList(@NonNull View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChatRoomList);
        ListOfChatRoomsAdapter listOfChatRoomsAdapter = new ListOfChatRoomsAdapter(chatRoomsList, this::onChatClicked);
        recyclerView.setAdapter(listOfChatRoomsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        RealmResults<PrivateChatModel> chatsListFromRealm = realm.where(PrivateChatModel.class)
                .beginGroup()
                .equalTo("userIdThatCreatedPost", user.getUid())
                .or()
                .equalTo("user2", user.getUid())
                .endGroup()
                .findAll();

        List<PrivateChatModel> newChatRoomList = new ArrayList<>(realm.copyFromRealm(chatsListFromRealm));
        updateChatRoomListWithDiffUtil(newChatRoomList, listOfChatRoomsAdapter);
    }

    public void updateChatRoomListWithDiffUtil(List<PrivateChatModel> newChatRoomList, ListOfChatRoomsAdapter listOfChatRoomsAdapter) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChatRoomDiffUtil(chatRoomsList, newChatRoomList));
        chatRoomsList.clear();
        chatRoomsList.addAll(newChatRoomList);
        diffResult.dispatchUpdatesTo(listOfChatRoomsAdapter);
    }

    public void randomChatButton(View view) {
        AppCompatButton button = view.findViewById(R.id.randomChatButton);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            startActivity(intent);
        });
    }
}