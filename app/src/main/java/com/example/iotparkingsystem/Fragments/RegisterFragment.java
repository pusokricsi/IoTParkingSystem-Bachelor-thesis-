package com.example.iotparkingsystem.Fragments;

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

import com.example.iotparkingsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterFragment extends Fragment {

    private View view;
    private TextView loginTextView;
    private Button registerButton;
    private EditText registerEmailEditText,registerPasswordEditText1, registerPasswordEditText2;
    public FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_register,container,false);
        initialize();

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewUserData();
            }
        });

        loginTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getFragmentManager().getBackStackEntryCount() != 0){
                    getFragmentManager().popBackStack();
                }
                return false;
            }
        });
        return view;
    }

    public void initialize(){
        loginTextView = view.findViewById(R.id.text5);
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText);
        registerPasswordEditText1 = view.findViewById(R.id.registerPasswordEditText1);
        registerPasswordEditText2 = view.findViewById(R.id.registerPasswordEditText2);
        registerButton = view.findViewById(R.id.registerButton);
    }

    private void checkNewUserData(){
        String password = null;
        String email = registerEmailEditText.getText().toString();
        if (registerPasswordEditText1.getText().toString().equals(registerPasswordEditText2.getText().toString())){
            password = registerPasswordEditText1.getText().toString();
        }else{
            Toast.makeText(view.getContext(),"Password is not match",Toast.LENGTH_SHORT).show();
        }

        if (!email.equals("") && !password.equals("") && !password.equals(null)){
            Log.i("IoT","RegisterFragment/ New user create:"+email);
            createNewUser(email,password);
        }else{
            Toast.makeText(view.getContext(),"Email or password is empty",Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewUser(String email, final String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    firebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference = firebaseDatabase.getInstance().getReference("IoTSystem");
                    databaseReference.child("Users").child(firebaseUser.getUid()).child("userId").setValue(firebaseUser.getUid());
                    databaseReference.child("Users").child(firebaseUser.getUid()).child("email").setValue(firebaseUser.getEmail());
                    Log.i("IoT","RegisterFragment/ New user created:"+firebaseUser.getEmail());
                }else{
                    Log.i("IoT","RegisterFragment/ Failed to create new user");
                    Toast.makeText(view.getContext(), "Problem with register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
