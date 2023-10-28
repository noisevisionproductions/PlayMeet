package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();

        super.onCreate(savedInstanceState);


    }


}