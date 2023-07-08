package com.cinerikuy.presenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.utilty.Constans;
import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*Fragmento por defecto*/
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        //Obtenemos referencia del encabezado
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.username);
        Intent intent = getIntent();
        String user = intent.getStringExtra("username");
        userName.setText(user);
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                break;
            case R.id.movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Movie()).commit();
                break;
            case R.id.store:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Product()).commit();
                break;
            case R.id.vote:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Vote()).commit();
                break;
            case R.id.stores:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Shopping()).commit();
                break;
            case R.id.sesion:
                Toast.makeText(this, "Cerrando sesion", Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                Toast.makeText(this, "Como usar CineRikuy", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constans.URL_MANUAL_USER));
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}