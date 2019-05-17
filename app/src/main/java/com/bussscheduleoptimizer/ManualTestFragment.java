package com.bussscheduleoptimizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bussscheduleoptimizer.utils.TFLiteUtils;

public class ManualTestFragment extends Fragment {
    View myView;
    EditText vehicleType, month, day, hour, minute, holiday, vacation, temperature, pType;
    Button button, reset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.manual_test_page, container, false);
        vehicleType = myView.findViewById(R.id.editTextVehicleType);
        month = myView.findViewById(R.id.editTextMonth);
        day = myView.findViewById(R.id.editTextDay);
        hour = myView.findViewById(R.id.editTextHour);
        minute = myView.findViewById(R.id.editTextMinute);
        holiday = myView.findViewById(R.id.editTextHoliday);
        vacation = myView.findViewById(R.id.editTextVacation);
        temperature = myView.findViewById(R.id.editTextTemperature);
        pType = myView.findViewById(R.id.editTextPType);
        button = myView.findViewById(R.id.button);
        reset = myView.findViewById(R.id.reset);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TFLiteUtils.useTestData = true;
                TFLiteUtils.s_vehicleType = Float.valueOf(vehicleType.getText().toString());
                TFLiteUtils.s_month = Float.valueOf(month.getText().toString());
                TFLiteUtils.s_day = Float.valueOf(day.getText().toString());
                TFLiteUtils.s_hour = Float.valueOf(hour.getText().toString());
                TFLiteUtils.s_minute = Float.valueOf(minute.getText().toString());
                TFLiteUtils.s_holiday = Float.valueOf(holiday.getText().toString());
                TFLiteUtils.s_vacation = Float.valueOf(vacation.getText().toString());
                TFLiteUtils.s_temperature = Float.valueOf(temperature.getText().toString());
                TFLiteUtils.s_pType = Float.valueOf(pType.getText().toString());
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TFLiteUtils.useTestData = false;
            }
        });
        return myView;
    }
}
