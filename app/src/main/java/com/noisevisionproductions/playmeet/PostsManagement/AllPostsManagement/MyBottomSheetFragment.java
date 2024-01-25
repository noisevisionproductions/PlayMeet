package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.EditableField;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private FirebaseHelper firebaseHelper;
    private AppCompatButton savePostButton, chatButton;
    private AppCompatTextView aboutGameText, aboutUserText, signedInUsersText, noUsersSignedUpInfo;
    private PostCreating postCreating;
    private UserModel userModel;
    private EditableField[] editableFieldsUserInfo, editableFieldsPostInfo;

    public static MyBottomSheetFragment newInstance(PostCreating postCreating) {
        MyBottomSheetFragment fragment = new MyBottomSheetFragment();
        fragment.setPostCreating(postCreating);
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

    private void setupView(View view) {
        firebaseHelper = new FirebaseHelper();
        savePostButton = view.findViewById(R.id.savePostButton);
        chatButton = view.findViewById(R.id.chatButton);

        aboutGameText = view.findViewById(R.id.aboutGameText);
        aboutUserText = view.findViewById(R.id.aboutUserText);
        signedInUsersText = view.findViewById(R.id.signedInUsersText);
        noUsersSignedUpInfo = view.findViewById(R.id.noUsersSignedUpInfo);

        switchToScrollPriority();

        getUserDataFromFirebase();
        setupEditableFieldsPostInfo(view);
        signedUpUsersList();

        handleButtons(view);
    }

    private void setupEditableFieldsPostInfo(View view) {
        // pokazuje informacje o grze
        if (postCreating != null) {
            editableFieldsPostInfo = new EditableField[]{
                    // pola związane z aktywnością
                    new EditableField("Data:", postCreating.getDateTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Godzina:", postCreating.getHourTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Post ID:", postCreating.getPostId(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW)
            };
        }
        RecyclerView recyclerViewPostInfo = view.findViewById(R.id.recycler_view_post_info);
        recyclerViewPostInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterPostExtendedInfoFields adapterPost = new AdapterPostExtendedInfoFields(editableFieldsPostInfo);
        recyclerViewPostInfo.setAdapter(adapterPost);

        // po kliknięciu w text, rozwijam lub zwijam informacje
        aboutGameText.setOnClickListener(v -> expandAboutGameInfo(recyclerViewPostInfo));
    }

    private void expandAboutGameInfo(RecyclerView recyclerViewPostInfo) {
        if (recyclerViewPostInfo.getVisibility() == View.VISIBLE) {
            recyclerViewPostInfo.setVisibility(View.GONE);
        } else {
            recyclerViewPostInfo.setVisibility(View.VISIBLE);
        }
    }

    private void getUserDataFromFirebase() {
        if (postCreating != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(postCreating.getUserId());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userModel = snapshot.getValue(UserModel.class);

                        if (userModel == null) {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
                        } else {
                            editableFieldsUserInfo = new EditableField[]{
                                    new EditableField(getString(R.string.provideName), userModel.getName(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    new EditableField(getString(R.string.provideNick), userModel.getNickname(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    new EditableField(getString(R.string.provideAge), userModel.getAge(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    new EditableField(getString(R.string.provideCity), userModel.getLocation(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    new EditableField(getString(R.string.provideGender), userModel.getGender(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                                    new EditableField(getString(R.string.provideAboutYou), userModel.getAboutMe(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                            };
                            RecyclerView recyclerViewUserInfo = requireView().findViewById(R.id.recycler_view_user_info);
                            recyclerViewUserInfo.setLayoutManager(new LinearLayoutManager(getContext()));
                            AdapterPostExtendedInfoFields adapterUser = new AdapterPostExtendedInfoFields(editableFieldsUserInfo);
                            recyclerViewUserInfo.setAdapter(adapterUser);

                            aboutUserText.setOnClickListener(v -> expandAboutUserInfo(recyclerViewUserInfo));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase RealmTime Database error", "Downloading user data from DB where more info about post is " + error.getMessage());
                }
            });
        }
    }

    private void expandAboutUserInfo(RecyclerView recyclerViewUserInfo) {
        if (recyclerViewUserInfo.getVisibility() == View.VISIBLE) {
            recyclerViewUserInfo.setVisibility(View.GONE);
        } else {
            recyclerViewUserInfo.setVisibility(View.VISIBLE);
        }
    }

    private void signedUpUsersList() {
        if (postCreating != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postCreating.getPostId()).child("signedUpUserIds");
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

    private void fetchUserInformation(List<String> userIdsSingedUp) {
        List<UserModel> signedUpUsers = new ArrayList<>();
        if (!userIdsSingedUp.isEmpty()) {
            for (String userId : userIdsSingedUp) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            signedUpUsers.add(userModel);
                            // używam klasy UserModel w celu stworzenia informacji o użytkowniku

                        }
                        RecyclerView recyclerViewSignedUsers = requireView().findViewById(R.id.recycler_view_signed_users);
                        recyclerViewSignedUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                        AdapterSignedUpUsers adapterSignedUpUsers = new AdapterSignedUpUsers(signedUpUsers, getContext());
                        recyclerViewSignedUsers.setAdapter(adapterSignedUpUsers);

                        signedInUsersText.setOnClickListener(v -> expandUserSignedUpList(recyclerViewSignedUsers));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "User Info all post list " + error.getMessage());
                    }
                });
            }
        } else {
            signedInUsersText.setOnClickListener(v -> noUsersFoundText());
        }
    }

    private void noUsersFoundText() {
        if (noUsersSignedUpInfo.getVisibility() == View.VISIBLE) {
            noUsersSignedUpInfo.setVisibility(View.GONE);
        } else {
            noUsersSignedUpInfo.setVisibility(View.VISIBLE);
        }
    }

    private void expandUserSignedUpList(RecyclerView recyclerViewSignedUsers) {
        if (recyclerViewSignedUsers.getVisibility() == View.VISIBLE) {
            recyclerViewSignedUsers.setVisibility(View.GONE);
        } else {
            recyclerViewSignedUsers.setVisibility(View.VISIBLE);
        }
    }

    private void handleButtons(View view) {
        if (postCreating != null) {
            SavePostHandler savePostHandler = new SavePostHandler(view, postCreating.getPostId());
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
                                ChatButtonHandler.handleChatButtonClick(view, postCreating.getUserId(), getChildFragmentManager());
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

    public void setPostCreating(PostCreating postCreating) {
        this.postCreating = postCreating;
    }

    public interface OnDataPass {
        void onDataPass(String data);
    }

    OnDataPass dataPass;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPass = (OnDataPass) context;
    }

    public void setDataPass(OnDataPass dataPass) {
        this.dataPass = dataPass;
    }
}
