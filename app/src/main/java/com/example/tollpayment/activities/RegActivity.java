package com.example.tollpayment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tollpayment.R;
import com.example.tollpayment.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import es.dmoral.toasty.Toasty;

public class RegActivity extends AppCompatActivity {
    Context context = this;

    EditText name, email, phone, password, confirmPassword;
    Button createAccount;
    TextView signIn;
    ACProgressFlower acProgressFlower;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, usersRef, sqRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        signIn = findViewById(R.id.sign_in_text_view);
        name = findViewById(R.id.name_edit_text);
        email = findViewById(R.id.email_edit_text);
        phone = findViewById(R.id.phone_edit_text);
        password = findViewById(R.id.password_edit_text_reg);
        confirmPassword = findViewById(R.id.cpass_edit_text);
        createAccount = findViewById(R.id.create_account_button);

        acProgressFlower = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY)
                .text("Loading")
                .build();

        //firebase initialization
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        usersRef = databaseReference.child("users");
        auth = FirebaseAuth.getInstance();
        userId = databaseReference.push().getKey();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(context)) {

                    if (validateInput()) {
                        if((isEmailValid(email.getText().toString().trim()))
                                && (isValidPassword(password.getText().toString().trim()))
                        ) {
                            registerUser();
                        }else {
                            Toasty.error(context, "Email not valid", Toasty.LENGTH_LONG).show();
                        }
                    }else {
                        Toasty.error(context, "Inputs not valid", Toasty.LENGTH_LONG).show();
                    }
                }else {
                    Toasty.error(context, "Network not available", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerUser() {
        acProgressFlower.show();
        final HashMap userMap = new HashMap();
        userMap.put("name", name.getText().toString().trim());
        userMap.put("email", email.getText().toString().trim());
        userMap.put("phone", phone.getText().toString().trim());
        userMap.put("password", password.getText().toString().trim());
        userMap.put("confirmpassword", confirmPassword.getText().toString().trim());

        Query userQuery =
                usersRef.orderByChild("email").equalTo(email.getText().toString().trim());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    acProgressFlower.dismiss();
                    createUser(email.getText().toString().trim(), password.getText().toString().trim());
                    usersRef.child(userId).setValue(userMap);
                }else {
                    Toasty.error(context, "User already registered", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });


    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (NetworkUtils.isNetworkAvailable(context)) {
                            if (task.isSuccessful()) {
                                //acProgressFlower.dismiss();
                                RegActivity.this.startActivity(new Intent(context, DashboardActivity.class));
                                RegActivity.this.finish();
                            }
                            else
                            {

                            }
                        } else {
                            Toasty.error(context,"Network not available",Toasty.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateInput() {

        if(TextUtils.isEmpty(name.getText())) {
            name.setError("Cannot be empty");
        }
        if (phone.getText().toString().isEmpty()){
            phone.setError("Input a valid Number");
            phone.requestFocus();
        }else {
            int maxLength = 11;
            phone.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(maxLength)
            });
        }
        return true;

    }

    private boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    private boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
