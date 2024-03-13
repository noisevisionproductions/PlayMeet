package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.BottomSheetFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.EditableField;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

public class AdapterPostExtendedInfoFields extends RecyclerView.Adapter<AdapterPostExtendedInfoFields.ViewHolder> {
    private final EditableField[] editableFields;
    private final Context context;

    public AdapterPostExtendedInfoFields(EditableField[] editableFields, Context context) {
        this.editableFields = editableFields;
        this.context = context;
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
        holder.labelInfoType.setText(editableField.label);
        holder.labelInfoProvided.setText(editableField.value);
        setCopyIcon(editableField, holder);
    }

    @Override
    public int getItemCount() {
        return editableFields.length;
    }

    private void setCopyIcon(EditableField editableField, ViewHolder holder) {
        if (editableField.hasIcon) {
            // ustawianie efektu wizualnego wskazującego na możliwość wyboru elementu dla ikonki do kopiowania
            holder.labelInfoProvided.setBackgroundResource(R.drawable.ripple_effect);
            holder.labelInfoProvided.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_copy, 0);
            holder.labelInfoProvided.setOnClickListener(v -> ProjectUtils.copyTextOnClick(context, "postId", holder.labelInfoProvided.getText().toString()));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView labelInfoType, labelInfoProvided;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            labelInfoType = itemView.findViewById(R.id.labelInfoType);
            labelInfoProvided = itemView.findViewById(R.id.labelInfoProvided);
        }
    }
}
