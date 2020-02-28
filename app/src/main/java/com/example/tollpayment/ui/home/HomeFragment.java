package com.example.tollpayment.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tollpayment.R;
import com.example.tollpayment.activities.PaymentActivity;

import java.util.Objects;

public class HomeFragment extends Fragment {

    Button fundwallet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fundwallet = root.findViewById(R.id.payButton);
        fundwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PaymentActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        return root;
    }
}