package be.pxl.services.domain;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    GOEDGEKEURD("Goedgekeurd"),
    WACHTEND("Wachtend"),
    NIET_GOEDGEKEURD("Niet-Goedgekeurd");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }
}
