package controlm.qrcodegenerator.enums;

public enum OcrJobStatus {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    DONE("DONE"),
    ERROR("ERROR"),
    SAVED("SAVED");

    private final String name;

    OcrJobStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
