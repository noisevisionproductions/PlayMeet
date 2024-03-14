package com.noisevisionproductions.playmeet.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.chat.ChatRoomModel;
import com.noisevisionproductions.playmeet.userManagement.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseHelper {
    @NonNull
    private final DatabaseReference databaseReference;
    @Nullable
    private final FirebaseUser firebaseUser;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    // po wywołaniu tej metody, pobieram aktualnie zalogowanego użytkownika
    public FirebaseUser getCurrentUser() {
        return firebaseUser;
    }

    // pobieram dane z podanej referencji, (jesli jest ona powiązana z aktualnie zalogowanym użytkownikiem) z bazy danych
    public void getData(@NonNull ValueEventListener listener, @NonNull String reference) {
        if (firebaseUser != null) {
            DatabaseReference userReference = databaseReference.child(reference).child(firebaseUser.getUid());
            userReference.addListenerForSingleValueEvent(listener);
        }
    }

    public void getUserAvatar(@NonNull Context context, @NonNull String userId, @NonNull CircleImageView avatar) {
        DatabaseReference userReferenceForAvatar = databaseReference.child("UserModel").child(userId);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String userAvatar = userModel.getAvatar();
                        if (userAvatar != null) {
                            Uri avatarUri = Uri.parse(userAvatar);
                            Glide.with(context)
                                    .load(avatarUri)
                                    .into(avatar);
                        }
                    }
                    userReferenceForAvatar.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userReferenceForAvatar.removeEventListener(this);
                Log.e("Firebase RealmTime Database error", "Downloading userAvatar from DB " + error.getMessage());
            }
        };
        userReferenceForAvatar.addListenerForSingleValueEvent(listener);
    }

    public void getUserNickName(@NonNull String userId, @NonNull ValueEventListener listener) {
        DatabaseReference userReferenceForNickname = databaseReference.child("UserModel").child(userId).child("nickname");
        userReferenceForNickname.addListenerForSingleValueEvent(listener);
    }

    // tworzenie nowego ChatRoom po wywołaniu tej metody
    public void getExistingChatRoomId(String user1Id, String user2Id, @NonNull OnChatRoomIdFetched callback) {
        databaseReference.child("ChatRooms")
                .orderByChild("participants/" + user1Id).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String chatRoomId = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatRoomModel chatRoomModel = snapshot.getValue(ChatRoomModel.class);
                            if (chatRoomModel != null && chatRoomModel.getParticipants().containsKey(user2Id)) {
                                chatRoomId = snapshot.getKey();
                                break;
                            }
                        }

                        if (chatRoomId == null) {
                            chatRoomId = databaseReference.child("ChatRooms").push().getKey();
                            ChatRoomModel newChatRoom = new ChatRoomModel(chatRoomId);
                            newChatRoom.getParticipants().put(user1Id, true);
                            newChatRoom.getParticipants().put(user2Id, true);
                            // ustawiam aktualny czas stworzenia pokoju. W adapterze używam metody formatDate() w celu sformatowania daty,
                            // która jest łatwiejsza do odczytania
                            newChatRoom.setTimeStamp(System.currentTimeMillis());

                            if (chatRoomId != null) {
                                databaseReference.child("ChatRooms").child(chatRoomId).setValue(newChatRoom);
                            }
                        }
                        callback.onFetched(chatRoomId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Downloading and creating chat rooms " + error.getMessage());
                    }
                });
    }

    public interface OnChatRoomIdFetched {
        void onFetched(String chatRoomId);
    }
}
