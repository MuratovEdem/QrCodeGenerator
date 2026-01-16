package controlm.qrcodegenerator.enums;

public enum RoleEnum {

    ADMIN("ADMIN");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}