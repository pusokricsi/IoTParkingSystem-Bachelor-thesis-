package com.example.iotparkingsystem.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotparkingsystem.Fragments.MainFragment;
import com.example.iotparkingsystem.Fragments.ProfileFragment;
import com.example.iotparkingsystem.Fragments.ReservationListFragment;
import com.example.iotparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MainFragment mainFragment;
    private FirebaseAuth firebaseAuth;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeActivity();
    }

    public void initializeActivity() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mainFragment = new MainFragment();
        startFragment(mainFragment);
    }

    public void startFragment(Fragment fragment){
        fragmentTransaction.add(R.id.fragmentContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getClickedButton(View v){
        mainFragment.getClickedButton(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        firebaseAuth = FirebaseAuth.getInstance();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menuOnClickEvent(menu);
        return true;
    }

    public void menuOnClickEvent(Menu menu){
        menu.findItem(R.id.logoutItem).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("IoT","Logout click! User "+firebaseAuth.getCurrentUser().getEmail()+" has logged out!");
                firebaseAuth.signOut();
                MainActivity.super.finish();
                return false;
            }
        });
        menu.findItem(R.id.profileItem).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mainFragment.onDestroy();
                Log.i("IoT","Profile click! User "+firebaseAuth.getCurrentUser().getEmail()+" clicked!");
                profileFragment = new ProfileFragment(firebaseAuth);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer,profileFragment).commit();
                return false;
            }
        });
        menu.findItem(R.id.reservationItem).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mainFragment.onDestroy();
                Log.i("IoT","Reservation List click! User "+firebaseAuth.getCurrentUser().getEmail()+" clicked!");
                ReservationListFragment reservationListFragment = new ReservationListFragment();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer,reservationListFragment).commit();
                return false;
            }
        });
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }
}
