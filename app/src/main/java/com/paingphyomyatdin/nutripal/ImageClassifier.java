package com.paingphyomyatdin.nutripal;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class ImageClassifier {

    private Interpreter interpreter;

    private final int INPUT_SIZE = 300;

    // Constructor
    public ImageClassifier(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] shape = interpreter.getInputTensor(0).shape();
        android.util.Log.d("MODEL", "Input shape: " + java.util.Arrays.toString(shape));
    }

    // Method to load model file
    private ByteBuffer loadModelFile(Context context) throws Exception {
        FileInputStream inputStream =
                new FileInputStream(context.getAssets()
                        .openFd("efficientnet_b3.tflite").getFileDescriptor());

        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = context.getAssets()
                .openFd("efficientnet_b3.tflite").getStartOffset();
        long declaredLength = context.getAssets()
                .openFd("efficientnet_b3.tflite").getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Method to classify image
    public ClassificationResult classifyImage(Bitmap bitmap) {

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);

        ByteBuffer input = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
        input.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

        for (int pixel : pixels) {
            float r = (pixel >> 16) & 0xFF;
            float g = (pixel >> 8) & 0xFF;
            float b = pixel & 0xFF;

            input.putFloat(r);
            input.putFloat(g);
            input.putFloat(b);
        }

        input.rewind();

        float[][] output = new float[1][36];
        interpreter.run(input, output);

        int top1Index = 0;
        float top1 = -Float.MAX_VALUE;
        float top2 = -Float.MAX_VALUE;

        for (int i = 0; i < output[0].length; i++) {
            float score = output[0][i];

            if (score > top1) {
                top2 = top1;
                top1 = score;
                top1Index = i;
            } else if (score > top2) {
                top2 = score;
            }
        }

        boolean isValid = top1 >= 0.55f && (top1 - top2) >= 0.15f;

        return new ClassificationResult(top1Index, top1, top2, isValid);
    }
}