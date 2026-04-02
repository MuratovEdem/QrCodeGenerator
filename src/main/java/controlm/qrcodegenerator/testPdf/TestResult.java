package controlm.qrcodegenerator.testPdf;

import lombok.Data;

public class TestResult {
    String fileName;
    boolean success;
    int protocolCount;
    long durationMs;
    String error;

    public TestResult(String fileName, boolean success, int protocolCount, long durationMs, String error) {
        this.fileName = fileName;
        this.success = success;
        this.protocolCount = protocolCount;
        this.durationMs = durationMs;
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }
}
