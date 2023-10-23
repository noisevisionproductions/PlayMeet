package com.example.zagrajmy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SidePanelMenu extends AppCompatActivity {

    NavigationView navigationView;
/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_main);
       // navigationView = findViewById(R.id.navigationViewSidePanel);


    }
*/

    public void logout(NavigationView navigationView){
        Log.d("Wyloguj", "Przycisk 'Wyloguj' został kliknięty"); // Dodaj ten log

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.d("Wyloguj", "Przycisk 'Wyloguj' został kliknięty"); // Dodaj ten log

                int itemId = menuItem.getItemId();
                if (itemId == R.id.wylogujKontoPrzycisk) {
                    Log.d("Wyloguj", "Przycisk 'Wyloguj' został kliknięty"); // Dodaj ten log

                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(String.valueOf(Login.class));
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

    }

}
