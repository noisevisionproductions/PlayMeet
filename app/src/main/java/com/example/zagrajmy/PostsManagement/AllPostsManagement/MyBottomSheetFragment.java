package com.example.zagrajmy.PostsManagement.AllPostsManagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.EditableField;
import com.example.zagrajmy.UserManagement.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.annotation.Nullable;

import io.realm.Realm;

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

        setupEditableFieldsUserInfo();
        setupEditableFieldsPostInfo();

        RecyclerView recyclerViewUser = view.findViewById(R.id.recycler_view_user_info);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));
        PostExtendedInfoFieldsAdapter adapterUser = new PostExtendedInfoFieldsAdapter(editableFieldsUserInfo);
        recyclerViewUser.setAdapter(adapterUser);

        RecyclerView recyclerViewPost = view.findViewById(R.id.recycler_view_post_info);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));
        PostExtendedInfoFieldsAdapter adapterPost = new PostExtendedInfoFieldsAdapter(editableFieldsPostInfo);
        recyclerViewPost.setAdapter(adapterPost);
        handleButtons(view);
    }

    private void setupEditableFieldsUserInfo() {
        getRealmUserData();
        editableFieldsUserInfo = new EditableField[]{
                new EditableField(getString(R.string.provideName), userModel.getName(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                new EditableField(getString(R.string.provideAge), userModel.getBirthDay(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                new EditableField(getString(R.string.provideCity), userModel.getLocation(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                new EditableField(getString(R.string.provideGender), userModel.getGender(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                new EditableField(getString(R.string.provideAboutYou), userModel.getAboutMe(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
        };
    }

    private void setupEditableFieldsPostInfo() {
        editableFieldsPostInfo = new EditableField[]{
                new EditableField("Data:", postCreating.getDateTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
                new EditableField("Godzina:", postCreating.getHourTime(), false, false, false, EditableField.FieldType.FIELD_TYPE_TEXT_VIEW),
        };
    }

    private void getRealmUserData() {
        try (Realm realm = Realm.getDefaultInstance()) {
            userModel = realm.where(UserModel.class)
                    .equalTo("userId", postCreating.getUserId())
                    .findFirst();
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
