package com.noisevisionproductions.playmeet.userManagement;

public class EditableField {

    public enum FieldType {
        FIELD_TYPE_EDITTEXT, FIELD_TYPE_CITY_SPINNER, FIELD_TYPE_AGE_SPINNER, FIELD_TYPE_TEXT_VIEW
    }

    public final String label;
    public String value;
    public final boolean isEditable;
    public boolean isEditMode;
    public final boolean isSpinner;
    public final FieldType fieldType;

    public EditableField(String label, String value, boolean isSpinner, boolean isEditable, boolean isEditMode, FieldType fieldType) {
        this.label = label;
        this.value = value;
        this.isSpinner = isSpinner;
        this.isEditable = isEditable;
        this.isEditMode = isEditMode;
        this.fieldType = fieldType;
    }
}
