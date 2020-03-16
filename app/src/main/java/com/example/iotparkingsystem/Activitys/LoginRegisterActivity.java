package com.example.iotparkingsystem.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.iotparkingsystem.Fragments.LoginFragment;
import com.example.iotparkingsystem.Fragments.RegisterFragment;
import com.example.iotparkingsystem.R;

public class LoginRegisterActivity extends AppCompatActivity {

    public FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        initializeActivity();
    }

    public void initializeActivity() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        loginFragment = new LoginFragment();
        startFragment(loginFragment);
    }

    public void startFragment(Fragment fragment){
        fragmentTransaction.add(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }


}
