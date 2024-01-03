package com.example.zagrajmy.PostsManagement;

import java.util.Random;

public class UniqueIdGenerator {
    private static final int MAX = 99999;
    private static final int MIN = 1;
    private final Random random = new Random();

    public UniqueIdGenerator() {
    }

    public int generateUniqueId() {
        return random.nextInt((MAX - MIN) + 1) + MIN;
    }
}
