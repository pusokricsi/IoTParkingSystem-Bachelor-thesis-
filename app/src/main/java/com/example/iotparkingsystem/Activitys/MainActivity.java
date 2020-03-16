package com.example.iotparkingsystem.Activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotparkingsystem.Fragments.MainFragment;
import com.example.iotparkingsystem.R;



public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MainFragment mainFragment;

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
        fragmentTransaction.commit();
    }

    public void getClickedButton(View v){
        mainFragment.getClickedButton(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

}
