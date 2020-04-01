package com.wisekrakr.david.lonesome.app.activities.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.wisekrakr.david.lonesome.MainActivity;
import com.wisekrakr.david.lonesome.R;
import com.wisekrakr.david.lonesome.app.fragments.HomeFragment;
import com.wisekrakr.david.lonesome.app.fragments.profile.ProfileFragment;
import com.wisekrakr.david.lonesome.app.fragments.UsersFragment;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseContext;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;
import com.wisekrakr.david.lonesome.utils.CheckUserStatus;

public class DashboardActivity extends AppCompatActivity {

    //FirebaseRunner has all Firebase handlers
    private FirebaseRunner firebaseRunner;

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //actionbar and title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Create a Profile");

        //init firebase authentication
        firebaseRunner = new FirebaseRunner();
        firebaseRunner.initFirebaseAssistant();

        //init views

        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener());

        //home fragment transaction as default
        actionBar.setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragTransaction1 = getSupportFragmentManager().beginTransaction();
        fragTransaction1.replace(R.id.container, homeFragment, "");
        fragTransaction1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //handle item clicks
                switch (item.getItemId()){
                    case R.id.nav_home:
                        //home fragment transaction
                        actionBar.setTitle("Home");
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentTransaction fragTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragTransaction1.replace(R.id.container, homeFragment, "");
                        fragTransaction1.commit();
                        return true;
                    case R.id.nav_profile:
                        //profile fragment transaction
                        actionBar.setTitle("Profile");
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction fragTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragTransaction2.replace(R.id.container, profileFragment, "");
                        fragTransaction2.commit();
                        return true;
                    case R.id.nav_users:
                        //users fragment transaction
                        actionBar.setTitle("Users");
                        UsersFragment usersFragment = new UsersFragment();
                        FragmentTransaction fragTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragTransaction3.replace(R.id.container, usersFragment, "");
                        fragTransaction3.commit();
                        return true;
                }

                return false;
            }
        };
    }

    private void checkUserStatus(){
        // check for user
        if(CheckUserStatus.userStatus(firebaseRunner.getFirebaseAuth().getCurrentUser())){
            //user is signed in, stay here
            //set email of logged user
            FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

        }else{
            //user is not signed in, go back to main
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStart() {
        // Check if user is signed in (non-null)
        checkUserStatus();
        super.onStart();
    }

    /**
     * Inflate option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle menu item clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseRunner.getFirebaseAuth().signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }
}
