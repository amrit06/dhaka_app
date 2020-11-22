package com.example.dhaka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhaka.Object.User;
import com.example.dhaka.global.HomeLayout;
import com.example.dhaka.widgets.LoadingAlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText email_edit;
    private EditText password_edit;
    private Button login_button;
    private CheckBox remember_me_check;
    private TextView sign_up;
    private TextView forgot_password;
    private LoadingAlertDialog loadingPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_edit = findViewById(R.id.edittext_email);
        password_edit = findViewById(R.id.edittext_password);
        login_button = findViewById(R.id.button_login);
        remember_me_check = findViewById(R.id.checkbox_rememberme);
        sign_up = findViewById(R.id.textview_signup);
        forgot_password = findViewById(R.id.textview_forgot_password);
        loadingPage = new LoadingAlertDialog(MainActivity.this);

        remember_me_check.setChecked(false);
        login_button.setOnClickListener(this);
        sign_up.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_login:
                logUsers();
                break;
            case R.id.textview_signup:
                Intent SinUpActivityintent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(SinUpActivityintent);
                break;
            case R.id.textview_forgot_password:
                Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void logUsers(){
        String userEmail = email_edit.getText().toString();
        String userPassword = password_edit.getText().toString();
        Boolean error = false;


        if ( TextUtils.isEmpty(userEmail) ){
            email_edit.setText("");
            email_edit.setBackgroundColor(getColor(R.color.error));
            error = true;
        }

        if ( TextUtils.isEmpty(userPassword) ){
            password_edit.setText("");
            password_edit.setBackgroundColor(getColor(R.color.error));
            error = true;
        }

        if (!error){
            loadingPage.startLoadingDialog();
            loadingPage.setCancelable(false);
            login(userEmail, userPassword);
        }
    }


    public void login(final String email, final String password){
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        Query query = dbref.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.exists() ){
                    for (DataSnapshot user:dataSnapshot.getChildren()){ // loop through all email found
                        User currentUser = user.getValue(User.class);
                        if (currentUser.email.equals(email) && currentUser.password.equals(password) ){
                            loadingPage.dismissDialog();
                            // remeber me
                            Intent intent = new Intent(MainActivity.this, HomeLayout.class);
                            startActivity(intent);
                        }
                    }
                }else {
                    loadingPage.dismissDialog();
                    email_edit.setText("");
                    password_edit.setText("");
                    Toast.makeText(MainActivity.this, "2 Email Password Didn't match with our system", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}