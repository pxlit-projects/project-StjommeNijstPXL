package be.pxl.services.domain;

public enum Status {
    GOEDGEKEURD("Goedgekeurd"),
    WACHTEND("Wachtend"),
    NIET_GOEDGEKEURD("Niet-Goedgekeurd"),
    CONCEPT("Concept");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
