package com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.EditableField;

import java.util.List;

public class EditableUserFieldsAdapter extends RecyclerView.Adapter<EditableUserFieldsAdapter.ViewHolder> {
    private final List<EditableField> editableFields;
    private final Context context;

    public EditableUserFieldsAdapter(Context context, List<EditableField> editableFields) {
        this.context = context;
        this.editableFields = editableFields;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_editable_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditableUserFieldsAdapter.ViewHolder holder, int position) {
        EditableField field = editableFields.get(position);
        holder.labelTextView.setText(field.label);
        holder.editText.setText(field.value);

        // zmiana widoku jaki rodzaj wprowadzania, zaleznie od kontekstu
        if (field.fieldType == EditableField.FieldType.FIELD_TYPE_CITY_SPINNER) {
            holder.editText.setVisibility(View.GONE);
            holder.citySpinner.setVisibility(View.VISIBLE);
            holder.ageSpinner.setVisibility(View.GONE);

            holder.editButton.setText(field.isEditMode ? context.getString(R.string.Save) : context.getString(R.string.Edit));

            SpinnerUpdater.updateSpinnerData(holder.citySpinner, field.value, context);
        } else if (field.fieldType == EditableField.FieldType.FIELD_TYPE_AGE_SPINNER) {
            holder.editText.setVisibility(View.GONE);
            holder.citySpinner.setVisibility(View.GONE);
            holder.ageSpinner.setVisibility(View.VISIBLE);

            holder.editButton.setText(field.isEditMode ? context.getString(R.string.Save) : context.getString(R.string.Edit));

            SpinnerUpdater.updateSpinnerData(holder.ageSpinner, field.value, context);
        } else if (field.fieldType == EditableField.FieldType.FIELD_TYPE_EDITTEXT) {
            holder.citySpinner.setVisibility(View.GONE);
            holder.editText.setVisibility(View.VISIBLE);
            holder.ageSpinner.setVisibility(View.GONE);

            holder.editText.setText(field.value);

            holder.editButton.setText(field.isEditMode ? context.getString(R.string.Save) : context.getString(R.string.Edit));
        }
        holder.editButton.setOnClickListener(v -> handleEditButtonClick(field, holder.editText, holder.citySpinner, holder.ageSpinner, holder.editButton, holder.itemView));
    }

    @Override
    public int getItemCount() {
        return editableFields.size();
    }

    private void handleEditButtonClick(EditableField field, EditText editText, AppCompatSpinner citySpinner, AppCompatSpinner ageSpinner, AppCompatButton editButton, View view) {
        // przekazuje wszystkie pola do klasy TextUpdater, w celu weryfikacji co jest czym i zależnie od rodzaju pola, dodać odpowiednie wymogi oraz logikę
        TextUpdater.updateTextValue(field, editText, citySpinner, ageSpinner, editButton, view, context);
        // zmieniam napisz na przycisku zależnie od jego stanu
        editButton.setText(field.isEditMode ? context.getString(R.string.Save) : context.getString(R.string.Edit));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelTextView;
        private final AppCompatSpinner citySpinner, ageSpinner;
        private final EditText editText;
        private final AppCompatButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ageSpinner = itemView.findViewById(R.id.ageSpinner);

            labelTextView = itemView.findViewById(R.id.labelTextView);
            citySpinner = itemView.findViewById(R.id.citySpinner);
            editButton = itemView.findViewById(R.id.editButton);
            editText = itemView.findViewById(R.id.editText);
        }
    }
}
