package com.example.dhaka.global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.dhaka.R;
import com.google.android.material.navigation.NavigationView;

public class HomeLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;
    private DrawerLayout drawer;
    private LinearLayout layout;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_view = findViewById(R.id.navigationview);
        layout = findViewById(R.id.linear_layout);
        frameLayout = findViewById(R.id.frame_layout);
        drawer = findViewById(R.id.drawer);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeLayout.this, drawer, R.string.navigation_open_drawer, R.string.navigation_close_drawer){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slidevalue = drawerView.getWidth() * slideOffset;
                layout.setTranslationX(slidevalue);
            }
        };
        toggle.getDrawerArrowDrawable().setBarThickness(4);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colour_black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(HomeLayout.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}