package com.example.tollpayment.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.tollpayment.R;
import com.google.android.material.textfield.TextInputEditText;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import es.dmoral.toasty.Toasty;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Context context = this;
    TextInputEditText cardNumber, expiryDate, cvv;
    Button getStartedButton;
    String[] seperated;
    int expiryMonth, expiryYear;
    Card card;
    Spinner cardSpinner;

    String[] cardType = { "Debit", "Visa", "Mastercard", "Verve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cardNumber = findViewById(R.id.card_number_edit_text);
        expiryDate = findViewById(R.id.expiry_date_edit_text);
        cvv = findViewById(R.id.cvv_edit_text);
        cardSpinner = findViewById(R.id.card_type_spinner);
        getStartedButton = findViewById(R.id.payment_button);



        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cardType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        cardSpinner.setAdapter(aa);
        cardSpinner.setOnItemSelectedListener(this);

        //initialize the paystack sdk
        PaystackSdk.initialize(context);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                seperated = expiryDate.getText().toString().split("/");
                expiryMonth = Integer.parseInt(seperated[0]);
                expiryYear = Integer.parseInt(seperated[1]);


                card = new Card(cardNumber.getText().toString().trim(),expiryMonth, expiryYear, cvv.getText().toString().trim());
                if (card.isValid()) {
                    // charge card
                    performCharge();
                } else {
                    Toasty.error(context, "Card not valid", Toasty.LENGTH_LONG).show();
                }

            }
        });



    }

    public void performCharge(){
        //create a Charge object
        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge

        PaystackSdk.chargeCard(PaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                Toasty.success(context, "Payment Successful", Toasty.LENGTH_LONG).show();
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                Toasty.error(context, "Transaction failed", Toasty.LENGTH_LONG).show();
            }

        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

class Bootstrap {
    public static void setPaystackKey(String publicKey) {
        PaystackSdk.setPublicKey(publicKey);
    }
}
