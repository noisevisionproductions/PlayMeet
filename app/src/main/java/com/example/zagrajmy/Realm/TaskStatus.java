package com.example.zagrajmy.Realm;

public enum TaskStatus {
    Open("Open"),
    InProgress("In Progress"),
    Complete("Complete");
    final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
}