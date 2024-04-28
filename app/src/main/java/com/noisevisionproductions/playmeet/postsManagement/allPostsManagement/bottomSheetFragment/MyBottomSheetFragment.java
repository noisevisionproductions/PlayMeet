package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
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
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnUserModelCompleted;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostCompletionListenerList;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostInfo;
import com.noisevisionproductions.playmeet.userManagement.EditableField;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.userManagement.userProfile.ConstantUserId;
import com.noisevisionproductions.playmeet.userManagement.userProfile.UserProfile;
import com.noisevisionproductions.playmeet.utilities.admin.AdminTools;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private FirebaseHelper firebaseHelper;
    private AppCompatButton savePostButton, chatButton;
    private AppCompatTextView aboutGameText, signedInUsersText, noUsersSignedUpInfo;
    private PostInfo postInfo;
    private EditableField[] editableFieldsPostInfo;

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
        signedInUsersText = view.findViewById(R.id.signedInUsersText);
        noUsersSignedUpInfo = view.findViewById(R.id.noUsersSignedUpInfo);

        switchToScrollPriority();

        getCreatorUserData(view);
        setupEditableFieldsPostInfo(view, postInfo);
        signedUpUsersList();

        handleButtons(view);

        AdminTools adminTools = new AdminTools(postInfo, requireContext());
        adminTools.deletePostAsAdmin(view);
    }

    private void getCreatorUserData(View view) {
        String postCreatorUserId = this.postInfo.getUserId();
        new FirebaseUserRepository().getUserAllData(postCreatorUserId, new OnUserModelCompleted() {
            @Override
            public void onSuccess(UserModel userModel) {
                if (getActivity() == null) return;
                try {
                    getActivity().runOnUiThread(() -> {
                        CircleImageView userAvatarPostCreator = view.findViewById(R.id.userAvatarPostCreator);
                        new FirebaseHelper().getUserAvatar(requireContext(), postCreatorUserId, userAvatarPostCreator);

                        AppCompatTextView nicknameTextPostCreator = view.findViewById(R.id.nicknameTextPostCreator);
                        nicknameTextPostCreator.setText(userModel.getNickname());

                        openCreatorProfile(view);
                    });
                } catch (Exception e) {
                    Log.e("Decryption error", "Error decrypting user data in user post " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void openCreatorProfile(View view) {
        LinearLayoutCompat postCreatorProfileLayout = view.findViewById(R.id.postCreatorProfileLayout);
        postCreatorProfileLayout.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag("userProfile") == null) {
                UserProfile userProfile = new UserProfile();
                Bundle args = new Bundle();
                args.putString(ConstantUserId.USER_ID_KEY, this.postInfo.getUserId());
                userProfile.setArguments(args);

                userProfile.show(fragmentManager, "userProfile");
            }
        });
    }

    private void setupEditableFieldsPostInfo(@NonNull View view, @NonNull PostInfo postInfo) {
        // pokazuje informacje o grze
        if (this.postInfo != null) {
            editableFieldsPostInfo = new EditableField[]{
                    // pola związane z aktywnością
                    new EditableField(getString(R.string.provideSports), postInfo.getSportType(), false),
                    new EditableField(getString(R.string.provideCity), postInfo.getCityName(), false),
                    new EditableField(getString(R.string.provideDate), postInfo.getDateTime(), false),
                    new EditableField(getString(R.string.provideHour), postInfo.getHourTime(), false),
                    new EditableField(getString(R.string.provideInfo), postInfo.getAdditionalInfo(), false),
                    new EditableField(getString(R.string.providePostID), postInfo.getPostId(), true)
            };
        }
        createRecyclerViewForPostsInfo(view);
    }

    private void createRecyclerViewForPostsInfo(View view) {
        RecyclerView recyclerViewPostInfo = view.findViewById(R.id.recycler_view_post_info);
        recyclerViewPostInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterPostExtendedInfoFields adapterPost = new AdapterPostExtendedInfoFields(editableFieldsPostInfo, getContext());
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

    private void signedUpUsersList() {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.getPost(postInfo.getPostId(), new PostCompletionListenerList() {
            @Override
            public void onSuccess(List<String> userIdsSignedUp) {
                fetchUserInformation(userIdsSignedUp);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firebase Firestore error", "Signed up user list posts " + e.getMessage());
            }
        });
    }

    private void fetchUserInformation(@NonNull List<String> userIdsSingedUp) {
        List<UserModel> signedUpUsers = new ArrayList<>();
        RecyclerView recyclerViewSignedUsers = requireView().findViewById(R.id.recycler_view_signed_users);
        AdapterSignedUpUsers adapterSignedUpUsers = new AdapterSignedUpUsers(signedUpUsers, getContext(), postInfo, requireActivity().getSupportFragmentManager());
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

    // Obsługa przycisków do zapisania się do aktywności lub napisania do organizatora
    // Jeżeli zalogowany użytkownik jest właścicielem posta, to ukrywam te przyciski
    private void handleButtons(@NonNull View view) {
        if (this.postInfo != null) {
            SavePostHandler savePostHandler = new SavePostHandler(view, postInfo.getPostId());
            if (firebaseHelper.getCurrentUser() != null) {
                String currentUserId = firebaseHelper.getCurrentUser().getUid();
                if (postInfo.getUserId().equals(currentUserId)) {
                    savePostButton.setVisibility(View.GONE);
                    chatButton.setVisibility(View.GONE);
                } else {
                    savePostButton.setVisibility(View.VISIBLE);
                    chatButton.setVisibility(View.VISIBLE);
                    savePostButton.setOnClickListener(v -> ButtonsForChatAndSignIn.checkNicknameAndPerformAction(currentUserId, savePostHandler::savePostInDBLogic, getChildFragmentManager()));
                    chatButton.setOnClickListener(v -> ButtonsForChatAndSignIn.handleChatButtonClick(view, postInfo.getUserId(), getChildFragmentManager()));
                }
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
