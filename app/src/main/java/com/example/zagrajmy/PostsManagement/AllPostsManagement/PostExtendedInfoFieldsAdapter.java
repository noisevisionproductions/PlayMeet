package com.example.zagrajmy.PostsManagement.AllPostsManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.EditableField;

public class PostExtendedInfoFieldsAdapter extends RecyclerView.Adapter<PostExtendedInfoFieldsAdapter.ViewHolder> {
    private final EditableField[] editableFields;

    public PostExtendedInfoFieldsAdapter(EditableField[] editableFields) {
        this.editableFields = editableFields;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_show_info_from_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EditableField editableField = editableFields[position];
        holder.label.setText(editableField.label);
        holder.value.setText(editableField.value);
    }

    @Override
    public int getItemCount() {
        return editableFields.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView label, value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.labelInfoType);
            value = itemView.findViewById(R.id.labelInfoProvided);
        }
    }
}
