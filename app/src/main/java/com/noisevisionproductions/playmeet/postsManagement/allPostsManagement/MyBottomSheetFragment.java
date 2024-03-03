package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.postsManagement.PostInfo;
import com.noisevisionproductions.playmeet.userManagement.EditableField;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.ToastManager;
import com.noisevisionproductions.playmeet.utilities.UserModelDecryptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private FirebaseHelper firebaseHelper;
    private AppCompatButton savePostButton, chatButton;
    private AppCompatTextView aboutGameText, aboutUserText, signedInUsersText, noUsersSignedUpInfo;
    private PostInfo postInfo;
    @Nullable
    private UserModel userModel;
    private EditableField[] editableFieldsUserInfo, editableFieldsPostInfo;

    @NonNull
    public static MyBottomSheetFragment newInstance(PostInfo postInfo) {
        MyBottomSheetFragment fragment = new MyBottomSheetFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setPostCreating(postInfo);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_all_posts, container, false);
        setupView(view);

        return view;
    }

    private void switchToScrollPriority() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
            FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (frameLayout != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                bottomSheetBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);
                bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private void setupView(@NonNull View view) {
        firebaseHelper = new FirebaseHelper();
        savePostButton = view.findViewById(R.id.savePostButton);
        chatButton = view.findViewById(R.id.chatButtonSavedPosts);

        aboutGameText = view.findViewById(R.id.aboutGameText);
        aboutUserText = view.findViewById(R.id.aboutUserText);
        signedInUsersText = view.findViewById(R.id.signedInUsersText);
        noUsersSignedUpInfo = view.findViewById(R.id.noUsersSignedUpInfo);

        switchToScrollPriority();

        getUserDataFromFirebase();
        setupEditableFieldsPostInfo(view, postInfo);
        signedUpUsersList();

        handleButtons(view);
    }

    private void setupEditableFieldsPostInfo(@NonNull View view, @NonNull PostInfo postInfo) {
        // pokazuje informacje o grze
        if (this.postInfo != null) {
            editableFieldsPostInfo = new EditableField[]{
                    // pola związane z aktywnością
                    new EditableField("Sport:", postInfo.getSportType(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Miasto:", postInfo.getCityName(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Data:", postInfo.getDateTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Godzina:", postInfo.getHourTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Post ID:", postInfo.getPostId(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Info:", postInfo.getAdditionalInfo(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW)
            };
        }
        RecyclerView recyclerViewPostInfo = view.findViewById(R.id.recycler_view_post_info);
        recyclerViewPostInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterPostExtendedInfoFields adapterPost = new AdapterPostExtendedInfoFields(editableFieldsPostInfo);
        recyclerViewPostInfo.setAdapter(adapterPost);

        // po kliknięciu w text, rozwijam lub zwijam informacje
        aboutGameText.setOnClickListener(new View.OnClickListener() {
            boolean isListExpanded = false; //śledzę stan rozwinięcia listy

            @Override
            public void onClick(View v) {
                if (isListExpanded) {
                    collapseAboutInfo(recyclerViewPostInfo);
                    aboutGameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_down, 0);
                } else {
                    expandAboutInfo(recyclerViewPostInfo);
                    aboutGameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_up, 0);
                }
                isListExpanded = !isListExpanded;
            }
        });
    }

    private void getUserDataFromFirebase() {
        if (this.postInfo != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(postInfo.getUserId());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            try {
                                UserModel decryptedUserModel = UserModelDecryptor.decryptUserModel(getContext(), userModel);

                                if (getContext() == null) {
                                    ToastManager.showToast(getContext(), getString(R.string.error));
                                } else {
                                    editableFieldsUserInfo = new EditableField[]{
                                            new EditableField(getString(R.string.provideName), decryptedUserModel.getName(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                            new EditableField(getString(R.string.provideNick), userModel.getNickname(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                            new EditableField(getString(R.string.provideAge), decryptedUserModel.getAge(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                            new EditableField(getString(R.string.provideCity), decryptedUserModel.getLocation(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                            new EditableField(getString(R.string.provideGender), decryptedUserModel.getGender(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                            new EditableField(getString(R.string.provideAboutYou), decryptedUserModel.getAboutMe(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    };
                                    RecyclerView recyclerViewUserInfo = requireView().findViewById(R.id.recycler_view_user_info);
                                    recyclerViewUserInfo.setLayoutManager(new LinearLayoutManager(getContext()));
                                    AdapterPostExtendedInfoFields adapterUser = new AdapterPostExtendedInfoFields(editableFieldsUserInfo);
                                    recyclerViewUserInfo.setAdapter(adapterUser);

                                    handleAboutUserTextClick(recyclerViewUserInfo);
                                }
                            } catch (Exception e) {
                                Log.e("Decryption error", "Error decrypting user data in user post " + e.getMessage());
                            }
                        }
                    } else {
                        ToastManager.showToast(requireContext(), "Błąd podczas pobierania danych użytkownika");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase RealmTime Database error", "Downloading user data from DB where more info about post is " + error.getMessage());
                }
            });
        }
    }

    private void handleAboutUserTextClick(RecyclerView recyclerViewUserInfo) {
        aboutUserText.setOnClickListener(new View.OnClickListener() {
            boolean isListExpanded = false; //śledzę stan rozwinięcia listy

            @Override
            public void onClick(View v) {
                if (isListExpanded) {
                    collapseAboutInfo(recyclerViewUserInfo);
                    aboutUserText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_down, 0);
                } else {
                    expandAboutInfo(recyclerViewUserInfo);
                    aboutUserText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_up, 0);
                }
                isListExpanded = !isListExpanded;
            }
        });
    }

    private void signedUpUsersList() {
        if (this.postInfo != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postInfo.getPostId()).child("signedUpUserIds");
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> userIdsSignedUp = new ArrayList<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot userIdSnapshot : snapshot.getChildren()) {
                            String userId = userIdSnapshot.getValue(String.class);
                            if (userId != null) {
                                userIdsSignedUp.add(userId);
                            }
                        }
                    }
                    fetchUserInformation(userIdsSignedUp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase RealmTime Database error", "Signed up user list posts " + error.getMessage());
                }
            });
        }
    }

    private void fetchUserInformation(@NonNull List<String> userIdsSingedUp) {
        List<UserModel> signedUpUsers = new ArrayList<>();
        RecyclerView recyclerViewSignedUsers = requireView().findViewById(R.id.recycler_view_signed_users);
        AdapterSignedUpUsers adapterSignedUpUsers = new AdapterSignedUpUsers(signedUpUsers, getContext());
        recyclerViewSignedUsers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSignedUsers.setAdapter(adapterSignedUpUsers);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("UserModel");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String userId : userIdsSingedUp) {
                    UserModel userModel = snapshot.child(userId).getValue(UserModel.class);
                    if (userModel != null) {
                        signedUpUsers.add(userModel);
                        adapterSignedUpUsers.notifyItemInserted(signedUpUsers.size() - 1);
                    }
                }
                updateNoUsersSignedUpInfoVisibility(signedUpUsers, recyclerViewSignedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Realtime Database error", "User Info all post list " + error.getMessage());
            }
        };

        // Dodaj listenera dla wszystkich użytkowników jednocześnie.
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        signedInUsersText.setOnClickListener(new View.OnClickListener() {
            boolean isListExpanded = false;

            @Override
            public void onClick(View v) {
                if (isListExpanded) {
                    collapseAboutInfo(recyclerViewSignedUsers);
                    signedInUsersText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_down, 0);
                } else {
                    expandAboutInfo(recyclerViewSignedUsers);
                    signedInUsersText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_up, 0);
                }
                isListExpanded = !isListExpanded;
                updateNoUsersSignedUpInfoVisibility(signedUpUsers, recyclerViewSignedUsers);
            }
        });
    }

    private void updateNoUsersSignedUpInfoVisibility(@NonNull List<UserModel> signedUpUsers, @NonNull RecyclerView recyclerView) {
        if (signedUpUsers.isEmpty() && recyclerView.getVisibility() == View.VISIBLE) {
            noUsersSignedUpInfo.setVisibility(View.VISIBLE);
        } else {
            noUsersSignedUpInfo.setVisibility(View.GONE);
        }
    }

    private void handleButtons(@NonNull View view) {
        if (this.postInfo != null) {
            SavePostHandler savePostHandler = new SavePostHandler(view, postInfo.getPostId());
            if (firebaseHelper.getCurrentUser() != null) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(firebaseHelper.getCurrentUser().getUid());
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            String nickname = userModel.getNickname();
                            // przyciski do zapisania się do postu oraz stworzenia czatu jedynie, gdy użytkownik ustawi nick
                            savePostButton.setOnClickListener(v -> {
                                if (nickname == null || nickname.isEmpty()) {
                                    DialogFragment dialogFragment = new ContainerForDialogFragment();
                                    dialogFragment.show(getChildFragmentManager(), "my_dialog");
                                } else {
                                    savePostHandler.handleSavePostButton();
                                }
                            });
                            chatButton.setOnClickListener(v -> {
                                if (nickname == null || nickname.isEmpty()) {
                                    DialogFragment dialogFragment = new ContainerForDialogFragment();
                                    dialogFragment.show(getChildFragmentManager(), "my_dialog");
                                } else {
                                    ButtonsPostsAdapters.handleChatButtonClick(view, postInfo.getUserId());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase Save Error", "Checking if logged in user has nickName " + Objects.requireNonNull(error.getMessage()));
                    }
                });
            }
        }
    }

    private void expandAboutInfo(@NonNull RecyclerView recyclerView) {
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void collapseAboutInfo(@NonNull RecyclerView recyclerView) {
        recyclerView.setVisibility(View.GONE);
    }

    public void setPostCreating(PostInfo postInfo) {
        this.postInfo = postInfo;
    }
}