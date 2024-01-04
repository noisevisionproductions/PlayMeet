package com.example.zagrajmy.UserManagement;

public class EditableField {
    public String label;
    public String value;
    public boolean isEditable;
    public boolean isEditMode;

    public EditableField(String label, String value, boolean isEditable, boolean isEditMode) {
        this.label = label;
        this.value = value;
        this.isEditable = isEditable;
        this.isEditMode = isEditMode;
    }
}
