package com.bussscheduleoptimizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bussscheduleoptimizer.model.Day;
import com.bussscheduleoptimizer.model.Month;
import com.bussscheduleoptimizer.model.PrecipitationType;
import com.bussscheduleoptimizer.model.VehicleType;
import com.bussscheduleoptimizer.utils.TFLiteUtils;

public class ManualTestFragment extends Fragment {
    public static final String TAG = "ManualTestFragment";

    View myView;
    EditText hour, minute, temperature;
    Spinner vehicleType, month, day, pType;
    Switch holiday, vacation;
    Button button, reset;

    public ManualTestFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.manual_test_page, container, false);
        vehicleType = myView.findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<VehicleType>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, VehicleType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleType.setAdapter(adapter);

        month = myView.findViewById(R.id.monthSpinner);
        ArrayAdapter<Month> monthAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Month.values());
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(monthAdapter);

        day = myView.findViewById(R.id.daySpinner);
        ArrayAdapter<Day> dayAdadpter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Day.values());
        dayAdadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dayAdadpter);

        hour = myView.findViewById(R.id.editTextHour);
        minute = myView.findViewById(R.id.editTextMinute);
        holiday = myView.findViewById(R.id.holidaySwitch);
        vacation = myView.findViewById(R.id.vacationSwitch);
        temperature = myView.findViewById(R.id.editTextTemperature);
        pType = myView.findViewById(R.id.precipitationSpinner);
        ArrayAdapter<PrecipitationType> pTypeAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, PrecipitationType.values());
        pTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pType.setAdapter(pTypeAdapter);
        button = myView.findViewById(R.id.button);
        reset = myView.findViewById(R.id.reset);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidInput()) {
                    return;
                }
                TFLiteUtils.useTestData = true;
                TFLiteUtils.s_vehicleType = ((VehicleType) vehicleType.getSelectedItem()).getId();
                TFLiteUtils.s_month = ((Month) month.getSelectedItem()).getId();
                TFLiteUtils.s_day = ((Day) day.getSelectedItem()).getId();
                TFLiteUtils.s_hour = Float.valueOf(hour.getText().toString());
                TFLiteUtils.s_minute = Float.valueOf(minute.getText().toString());
                TFLiteUtils.s_holiday = holiday.isChecked() ? 1 : 0;
                TFLiteUtils.s_vacation = vacation.isChecked() ? 1 : 0;
                TFLiteUtils.s_temperature = Float.valueOf(temperature.getText().toString());
                TFLiteUtils.s_pType = ((PrecipitationType) pType.getSelectedItem()).getId();
                Log.i(TAG,
                        "Vehicle: " + TFLiteUtils.s_vehicleType
                        + " Month: " + TFLiteUtils.s_month
                        + " Day: " + TFLiteUtils.s_day
                        + " Hour: " + TFLiteUtils.s_hour
                        + " Minute: " + TFLiteUtils.s_minute
                        + " Holiday: " + TFLiteUtils.s_holiday
                        + " Vacation: " + TFLiteUtils.s_vacation
                        + " Temperature: " + TFLiteUtils.s_temperature
                        + " Precipitation: " + TFLiteUtils.s_pType);
                Toast.makeText(getActivity().getApplicationContext(), "Using test data", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).navigateToFragment(R.id.map_page);
                hideSoftKeyboard(getActivity());
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TFLiteUtils.useTestData = false;
                Toast.makeText(getActivity().getApplicationContext(), "Using real data", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).navigateToFragment(R.id.map_page);
                hideSoftKeyboard(getActivity());
            }
        });
        return myView;
    }

    private boolean isValidInput() {
        if (hour.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Hour is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (minute.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Minute is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (temperature.getText().toString().isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Temperature is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
