package com.noisevisionproductions.playmeet.UserManagement;

public class EditableField {

    public enum FieldType {
        FIELD_TYPE_EDITTEXT, FIELD_TYPE_CITY_SPINNER, FIELD_TYPE_AGE_SPINNER, FIELD_TYPE_TEXT_VIEW
    }

    public String label;
    public String value;
    public boolean isEditable;
    public boolean isEditMode;
    public boolean isSpinner;
    public FieldType fieldType;

    public EditableField(String label, String value, boolean isSpinner, boolean isEditable, boolean isEditMode, FieldType fieldType) {
        this.label = label;
        this.value = value;
        this.isSpinner = isSpinner;
        this.isEditable = isEditable;
        this.isEditMode = isEditMode;
        this.fieldType = fieldType;
    }
}
