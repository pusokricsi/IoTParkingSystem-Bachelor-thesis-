package com.example.iotparkingadmin.Activity.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.iotparkingadmin.Activity.Fragments.MainFragment;
import com.example.iotparkingadmin.R;

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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getClickedButton(View v){
        mainFragment.getClickedButton(v);
    }

}
