package controlm.qrcodegenerator.dto.response;

public record OcrJobEvent(
        Long jobId,
        String status,
        String message,
        Integer progress
) { }
