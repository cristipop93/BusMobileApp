package com.bussscheduleoptimizer;

import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText idFrom, idTo, vehicleType, month, day, hour, minute, holiday, vacation, temperature, pType;
    TextView secondsDelay;
    Button button;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idFrom = findViewById(R.id.editTextFrom);
        idTo = findViewById(R.id.editTextTo);
        vehicleType = findViewById(R.id.editTextVehicleType);
        month = findViewById(R.id.editTextMonth);
        day = findViewById(R.id.editTextDay);
        hour = findViewById(R.id.editTextHour);
        minute = findViewById(R.id.editTextMinute);
        holiday = findViewById(R.id.editTextHoliday);
        vacation = findViewById(R.id.editTextVacation);
        temperature = findViewById(R.id.editTextTemperature);
        pType = findViewById(R.id.editTextPType);
        secondsDelay = findViewById(R.id.textViewSecondsDelay);
        button = findViewById(R.id.button);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float prediction = doInference(Float.valueOf(idFrom.getText().toString()),
                        Float.valueOf(idTo.getText().toString()),
                        Float.valueOf(vehicleType.getText().toString()),
                        Float.valueOf(month.getText().toString()),
                        Float.valueOf(day.getText().toString()),
                        Float.valueOf(hour.getText().toString()),
                        Float.valueOf(minute.getText().toString()),
                        Float.valueOf(holiday.getText().toString()),
                        Float.valueOf(vacation.getText().toString()),
                        Float.valueOf(temperature.getText().toString()),
                        Float.valueOf(pType.getText().toString())
                );
                secondsDelay.setText(Float.toString(prediction));

//                sendToPythonServer();
            }
        });
    }

    private void sendToPythonServer() {
        new SendMessage().execute("aa bb cc");
        Toast.makeText(this, "The message has been sent", Toast.LENGTH_SHORT).show();
    }

    public float doInference(float idFrom, float idTo, float vehicleType, float month, float day, float hour, float minute, float holiday, float vacation, float temperature, float pType) {
        Object[] inputVals = new Object[11];
        inputVals[0] = new float[]{day};
        inputVals[1] = new float[]{holiday};
        inputVals[2] = new float[]{hour};
        inputVals[3] = new float[]{idFrom};
        inputVals[4] = new float[]{idTo};
        inputVals[5] = new float[]{minute};
        inputVals[6] = new float[]{month};
        inputVals[7] = new float[]{pType};
        inputVals[8] = new float[]{temperature};
        inputVals[9] = new float[]{vacation};
        inputVals[10] = new float[]{vehicleType};

        float[][] outputVal = new float[1][1];
        Map<Integer, Object> outputs = new HashMap();
        outputs.put(0, outputVal);

//        Map<Integer, Object> outputVal = new HashMap();
//        outputVal.put(0, new Object());
        tflite.runForMultipleInputsOutputs(inputVals, outputs);

//        return (Float) outputVal.get(0);
        return outputVal[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("converted_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
