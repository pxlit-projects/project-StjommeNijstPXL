package be.pxl.services.domain;

public enum Status {
    GOEDGEKEURD("Goedgekeurd"),
    WACHTEND("Wachtend"),
    NIET_GOEDGEKEURD("Niet-Goedgekeurd"),
    CONCEPT("Concept");

    Status(String displayName) {
    }
}
