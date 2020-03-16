package com.example.iotparkingsystem.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotparkingsystem.Activitys.MainActivity;
import com.example.iotparkingsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private View view;
    private TextView registerTextView;
    private EditText emailEditText,passwordEditText;
    private Button loginButton;
    public FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_login,container,false);
        initialize();

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("IoT","LoginFragment/ loginButtonClick");
                checkUserData();
            }
        });
        registerTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startNewFragment();
                return false;
            }
        });
        return view;
    }

    public void initialize(){
        registerTextView = view.findViewById(R.id.text2);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
    }

    public void startNewFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        fragmentTransaction.replace(R.id.fragmentContainer,registerFragment);
        fragmentTransaction.addToBackStack("1");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton){
            Log.i("IoT","LoginFragment/ loginButtonClick");
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            loginUser(email,password);
        }
    }

    private void checkUserData(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (!email.equals("") && !password.equals("")){
            Log.i("IoT","LoginFragment/ user data is ok");
            loginUser(email,password);
        }else{
            Toast.makeText(view.getContext(),"Email or password is emplty",Toast.LENGTH_SHORT).show();
        }

    }

    private void loginUser (String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(view.getContext(), "Autentification succesfull", Toast.LENGTH_SHORT).show();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("firebaseUser", firebaseUser);
                    startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "Autentification failed ", Toast.LENGTH_SHORT).show();
                    Log.i("IoT","ERROR: " + task.getException().toString());
                }
            }
        });
    }
}
