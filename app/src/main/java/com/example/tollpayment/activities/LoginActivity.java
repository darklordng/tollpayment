package com.example.tollpayment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tollpayment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    Context context = this;

    EditText tollId, password;
    Button getInButton;
    TextView signUp;
    ACProgressFlower acProgressFlower;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, usersRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tollId = findViewById(R.id.toll_id_edit_text);
        password = findViewById(R.id.password_edit_text);
        getInButton = findViewById(R.id.get_in_button);
        signUp = findViewById(R.id.sign_up_text_view);

        acProgressFlower = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY)
                .text("Loading")
                .build();

        //firebase initialization
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        usersRef = databaseReference.child("users");
        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegActivity.class));
                finish();
            }
        });

        getInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIn();
            }
        });
    }

    private void getIn() {

        Query userQuery =
                usersRef.orderByChild("email").equalTo(tollId.getText().toString().trim());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String user = String.valueOf(dataSnapshot.child("name").getValue());
                    startActivity(new Intent(context, MainActivity.class));
                    Toasty.success(context, "Welcome back "+user, Toasty.LENGTH_LONG).show();
                    finish();
                }else {
                    Toasty.error(context, "Invalid Login Credentials", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });
    }
}
