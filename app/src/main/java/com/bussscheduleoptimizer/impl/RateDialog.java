package com.bussscheduleoptimizer.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bussscheduleoptimizer.R;
import com.bussscheduleoptimizer.model.Arrival;
import com.google.firebase.firestore.FirebaseFirestore;

public class RateDialog extends Dialog implements View.OnClickListener {

    private final Arrival arrival;

    public RateDialog(Context context, Arrival arrival) {
        super(context);
        this.arrival = arrival;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_dialog);
        Button submit = (Button) findViewById(R.id.rateButton);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int crowdedLevel = 2;
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        int rate = (int) ratingBar.getRating();
        arrival.setCrowdedLevel(rate);
        storeArrival(arrival);
        hide();
        Toast.makeText(getContext(), R.string.rateAdded, Toast.LENGTH_SHORT).show();
    }

    private void storeArrival(Arrival arrival) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("arrival").add(arrival);
    }
}
