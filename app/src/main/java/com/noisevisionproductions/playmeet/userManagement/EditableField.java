package com.noisevisionproductions.playmeet.userManagement;

public class EditableField {
    public final String label;
    public final String value;
    public final boolean hasIcon;

    public EditableField(String label, String value, boolean hasIcon) {
        this.label = label;
        this.value = value;
        this.hasIcon = hasIcon;
    }
}
