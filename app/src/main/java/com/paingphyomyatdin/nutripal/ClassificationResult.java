package com.paingphyomyatdin.nutripal;

public class ClassificationResult {
    public int index;
    public float confidence;
    public float secondConfidence;
    public boolean isValid;

    public ClassificationResult(int index, float confidence, float secondConfidence, boolean isValid) {
        this.index = index;
        this.confidence = confidence;
        this.secondConfidence = secondConfidence;
        this.isValid = isValid;
    }
}