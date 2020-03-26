package com.example.iotparkingsystem.Fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.iotparkingsystem.Objects.DateCheck;
import com.example.iotparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FreeSpotFragment extends DialogFragment {

    private String spotId;
    private View view;
    private EditText date, startTime, endTime;
    private Button reserveButton;
    private DateCheck dateCheck;
    private DatabaseReference mDatabaseReference;

    public FreeSpotFragment(String spotId) {
        this.spotId = spotId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_free_spot,container,false);
        inicialize();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateOnClick();
            }
        });
        
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeOnClick();
            }
        });
        
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeOnClick();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveButtonOnClick();
            }
        });
        return this.view;
    }


    public void inicialize() {
        date = view.findViewById(R.id.dateEditText);
        startTime = view.findViewById(R.id.startTimeEditText);
        endTime = view.findViewById(R.id.endTimeEditText);
        reserveButton = view.findViewById(R.id.reserveButton);
        dateCheck = new DateCheck("","","");
    }

    private void dateOnClick() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ++month;
                date.setText(year+"/"+month+"/"+dayOfMonth);
                dateCheck.setcDate(year+"-"+month+"-"+dayOfMonth);
            }
        },year,month,day);
        datePickerDialog.show();

    }

    private void startTimeOnClick() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.setText(String.format("%02d:%02d",hourOfDay,minute));
                dateCheck.setcStartDate(String.format("%02d-%02d",hourOfDay,minute));
            }
        },hour,minute,true);
        timePickerDialog.show();
    }

    private void endTimeOnClick() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.setText(String.format("%02d:%02d",hourOfDay,minute));
                dateCheck.setcEndDate(String.format("%02d-%02d",hourOfDay,minute));
            }
        },hour,minute,true);
        timePickerDialog.show();
    }



    private void reserveButtonOnClick() {
        //TODO -> check15Minutes
        if (dateCheck.checkIsEmpty()){
            Log.i("IoT","FreeSpotFragment: Date field is empty!");
            Toast.makeText(view.getContext(),"Date field is empty!",Toast.LENGTH_SHORT).show();
        }else if (dateCheck.checkOutDatedDate()){
            Log.i("IoT","FreeSpotFragment: Date is out of date!");
            Toast.makeText(view.getContext(),"Date is out-of-date!",Toast.LENGTH_SHORT).show();
        }else{
            Log.i("IoT","FreeSpotFragment: Date field is ok!");
            Log.i("IoT","FreeSpotFragment: Date is up to date!");
            checkDateIsFree();
        }

    }

    private void checkDateIsFree(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");
        Query query = mDatabaseReference.child("Reservations").orderByChild("spotId").equalTo(spotId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFree = true;
                for (DataSnapshot idChild:dataSnapshot.getChildren()){
                    String startDate=null,endDate=null;
                    for (DataSnapshot child:idChild.getChildren()){
                        if (child.getKey().equals("startTime")){
                            startDate = String.valueOf(child.getValue());
                            Log.i("IoT","FreeSpotFragment: startTime "+startDate);
                        }
                        if (child.getKey().equals("endTime")){
                            endDate = String.valueOf(child.getValue());
                            Log.i("IoT","FreeSpotFragment: endTime "+endDate);
                        }
                        if (startDate!=null && endDate!=null){
                            if (!dateCheck.checkOneDateIsFree(startDate,endDate)){
                                isFree = false;
                                break;
                            }
                        }
                    }
                    if (!isFree){
                        break;
                    }
                }
                if (isFree){
                    addNewReservation();
                    Toast.makeText(view.getContext(),"Reserved succesfully!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewReservation(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");
        mDatabaseReference = mDatabaseReference.child("Reservations").push();
        mDatabaseReference.child("spotId").setValue(spotId);
        mDatabaseReference.child("endTime").setValue(dateCheck.getcDate()+" "+dateCheck.getcEndDate());
        mDatabaseReference.child("startTime").setValue(dateCheck.getcDate()+" "+dateCheck.getcStartDate());
        mDatabaseReference.child("userId").setValue(firebaseUser.getUid());
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
