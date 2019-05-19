package com.bussscheduleoptimizer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bussscheduleoptimizer.DirectionsCalculator;
import com.bussscheduleoptimizer.R;
import com.bussscheduleoptimizer.model.Result;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Result> mResults = new ArrayList<>();
    private Context mContext;
    private DirectionsCalculator directionsCalculator;

    public RecyclerViewAdapter(Context mContext, ArrayList<Result> mResults, DirectionsCalculator directionsCalculator) {
        this.mResults = mResults;
        this.mContext = mContext;
        this.directionsCalculator = directionsCalculator;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");

        final Result result = mResults.get(i);
        viewHolder.image.setImageResource(result.getVehicleType().getIconId());
        viewHolder.buss.setText(result.getBusId());
        viewHolder.delay.setText(result.getDelay());
        viewHolder.arriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: arrived");
                Toast.makeText(mContext, "arrived", Toast.LENGTH_SHORT).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // 24
                DocumentReference ref = db.collection("route").document("24");
                addSchedule1(3, ref);
                DocumentReference ref2 = db.collection("route").document("24");
                addSchedule2(3, ref2);
                //48
                DocumentReference ref3 = db.collection("route").document("48");
                addSchedule1(6, ref3);
                DocumentReference ref4 = db.collection("route").document("48");
                addSchedule2(6, ref4);
                //4
                DocumentReference ref5 = db.collection("route").document("4");
                addSchedule1(10, ref5);
                DocumentReference ref6 = db.collection("route").document("4");
                addSchedule2(10, ref6);
            }
        });
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: parentLayout: " + result.getBusId());
                directionsCalculator.calculateDirections(result.getCompleteRoute());
            }
        });

    }

    private void addSchedule1(int time, DocumentReference ref) {
        for (int hour = 6; hour < 23; hour++) {
            for (int minute = 0; minute < 60; minute +=time) {
                int result = hour * 100 + minute;
                ref.update("schedule1", FieldValue.arrayUnion(result));
            }
        }
    }
    private void addSchedule2(int time, DocumentReference ref) {
        for (int hour = 6; hour < 23; hour++) {
            for (int minute = 0; minute < 60; minute +=time) {
                int result = hour * 100 + minute;
                ref.update("schedule2", FieldValue.arrayUnion(result));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView buss;
        TextView delay;
        ImageButton arriveButton;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bus_icn);
            buss = itemView.findViewById(R.id.bus);
            delay = itemView.findViewById(R.id.delay);
            arriveButton = itemView.findViewById(R.id.btn_arrived);
            parentLayout = itemView.findViewById(R.id.item_parent);
        }
    }
}
