package com.example.dhaka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Pattern;


import com.example.dhaka.Object.User;
import com.example.dhaka.widgets.LoadingAlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText first_name;
    private EditText last_name;
    private EditText email;
    private EditText password;
    private EditText retry_password;
    private EditText phone;
    private ImageView calender;
    private TextView dob;
    private RadioGroup radiogroup;
    private Button submit;
    private User user;
    private TextView form_errormsg;
    private TextView gender_errormsg;
    private LoadingAlertDialog loadingPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        user = new User();
        first_name = findViewById(R.id.edittext_first_name);
        last_name = findViewById(R.id.edittext_last_name);
        email = findViewById(R.id.edittext_email);
        password = findViewById(R.id.edittext_password);
        retry_password = findViewById(R.id.edittext_retry_password);
        phone = findViewById(R.id.edittext_phone);
        calender = findViewById(R.id.imageview_calender);
        dob = findViewById(R.id.textview_dob);
        radiogroup = findViewById(R.id.radiogroup_gender);
        submit = findViewById(R.id.button_submit);
        form_errormsg = findViewById(R.id.textview_form_errormsg);
        gender_errormsg = findViewById(R.id.textview_gender);
        loadingPage = new LoadingAlertDialog(SignUpActivity.this);

        // listener
        calender.setOnClickListener(this);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_male:
                        user.gender = "Male";
                        break;
                    case R.id.radio_female:
                        user.gender = "Female";
                        break;
                    case R.id.radio_others:
                        user.gender = "Others";
                        break;
                    default:
                        user.gender = "unspecified";
                        break;
                }
            }
        });

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_calender:
               retrieveDob();
                break;
            case R.id.button_submit:
                register();
                break;
            default:
                break;
        }
    }

    public void retrieveDob(){
        int year = 0, month = 0, day = 0;
        DatePickerDialog.OnDateSetListener datePickerListener;
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                user.Dob = day+"/"+(month+1)+"/"+year;
                dob.setText(user.Dob);
            }
        };

        DatePickerDialog datePicker = new DatePickerDialog(SignUpActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                datePickerListener,
                day, month, year);
        datePicker.show();

    }

    public void register(){
        Toast.makeText(SignUpActivity.this, "Submit", Toast.LENGTH_SHORT).show();
        boolean error = false;
        //retrieve
        user.firstname = first_name.getText().toString();
        user.lastname = last_name.getText().toString();
        user.email = email.getText().toString();
        user.password = password.getText().toString();
        user.phone = phone.getText().toString();
        //dob and gender retrieved above
        Pattern nameRegex = Pattern.compile("[0-9$&+,:;= \\\\?@#|/'<>.^*()%!-]");

        // error checking
        if (checkError(first_name, user.firstname, nameRegex)){ error = true; }
        if (checkError(last_name, user.lastname, nameRegex)){ error = true; }

        if (TextUtils.isEmpty(user.email) || !Patterns.EMAIL_ADDRESS.matcher(user.email).matches() )
        {
            email.setText("");
            displayError(email);
            error = true;
        }else {
            neutralState(email);
        }

        if (TextUtils.isEmpty(user.password) )
        {
            displayError(password);
            displayError(retry_password);
            error = true;
        }else {
            if ( !user.password.equals(retry_password.getText().toString()) ){
                displayError(retry_password);
                error = true;
            }else {
                neutralState(password);
                neutralState(retry_password);
            }
        }

        if ( TextUtils.isEmpty(user.phone) || user.phone.length() < 10 ) {
            phone.setText("");
            displayError(phone);
            error = true;
        }else {
            neutralState(phone);
        }

        if (TextUtils.isEmpty(user.Dob) ){
            dob.setTextColor(getColor(R.color.error));
        }else {
            dob.setTextColor(getColor(R.color.colour_black));
        }

        if (TextUtils.isEmpty(user.gender)){
            gender_errormsg.setTextColor(getColor(R.color.error));
        }else{
            gender_errormsg.setTextColor(getColor(R.color.colour_black));
        }

        // submit
        if (error){
            form_errormsg.setVisibility(View.VISIBLE);
        }else {
            form_errormsg.setVisibility(View.INVISIBLE);
            loadingPage.startLoadingDialog();
            loadingPage.setCancelable(false);
            validateAndSave();
        }

    }

    public void validateAndSave(){
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(user.phone).exists()){

                    dbRef.child(user.phone).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            loadingPage.dismissDialog();
                            Intent mainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                        }
                    });

                    loadingPage.dismissDialog();
                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }else {
                    Toast.makeText(SignUpActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    loadingPage.dismissDialog();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean checkError(EditText editText, String value, Pattern regex){
        if (TextUtils.isEmpty(value) || regex.matcher(value).find() ){
            displayError(editText);
            return true;
        }else {
            neutralState(editText);
            return false;
        }
    }

    public void displayError(EditText editText){
        editText.setText("");
        editText.setBackgroundColor(getColor(R.color.error));
    }

    public void neutralState(EditText editText){
        editText.setBackgroundColor(getColor(R.color.colour_white));
    }

}