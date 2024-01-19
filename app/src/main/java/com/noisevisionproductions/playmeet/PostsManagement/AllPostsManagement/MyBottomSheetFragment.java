package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.EditableField;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import javax.annotation.Nullable;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private AppCompatButton savePostButton, chatButton;
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

    private void setupView(View view) {
        savePostButton = view.findViewById(R.id.savePostButton);
        chatButton = view.findViewById(R.id.chatButton);

        // Ustawienie maksymalnej wysokości dialogu do połowy ekranu
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            dialog.getBehavior().setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);
        }

        getUserDataFromFirebase();
        setupEditableFieldsPostInfo();

        RecyclerView recyclerViewPost = view.findViewById(R.id.recycler_view_post_info);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));
        PostExtendedInfoFieldsAdapter adapterPost = new PostExtendedInfoFieldsAdapter(editableFieldsPostInfo);
        recyclerViewPost.setAdapter(adapterPost);
        handleButtons(view);
    }

    private void setupEditableFieldsPostInfo() {
        if (postCreating != null) {
            editableFieldsPostInfo = new EditableField[]{
                    new EditableField("Data:", postCreating.getDateTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Godzina:", postCreating.getHourTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                    new EditableField("Post ID:", postCreating.getPostId(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW)
            };
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
                            RecyclerView recyclerViewUser = requireView().findViewById(R.id.recycler_view_user_info);
                            recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));
                            PostExtendedInfoFieldsAdapter adapterUser = new PostExtendedInfoFieldsAdapter(editableFieldsUserInfo);
                            recyclerViewUser.setAdapter(adapterUser);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void handleButtons(View view) {
        savePostButton.setOnClickListener(v -> ButtonHelperAllPosts.handleSavePostButton(view, postCreating.getPostId()));
        chatButton.setOnClickListener(v -> ButtonHelperAllPosts.handleChatButtonClick(view, postCreating.getUserId()));
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
