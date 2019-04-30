package com.bussscheduleoptimizer.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import com.bussscheduleoptimizer.R;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class TFLiteUtils {

    public static MappedByteBuffer loadModelFile(Context applicationContext, AssetManager assets) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(applicationContext.getResources().getString(R.string.tflite_file));
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float doInference(Interpreter tflite, float idFrom, float idTo, float vehicleType, float month, float day, float hour, float minute, float holiday, float vacation, float temperature, float pType) {
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

        tflite.runForMultipleInputsOutputs(inputVals, outputs);

        return outputVal[0][0];
    }
}
