package models;

public enum RadioType {
    PORTABLE("Portable Radio"),
    BASE("Base Radio");

    private final String displayName;

    RadioType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}