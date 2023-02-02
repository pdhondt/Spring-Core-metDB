package be.vdab.geld.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Schenking {
    private final long id;
    private final long vanMensId;
    private final long aanMensId;
    private final LocalDateTime wanneer;
    private final BigDecimal bedrag;

    public Schenking(long vanMensId, long aanMensId, BigDecimal bedrag) {
        if (vanMensId <= 0) {
            throw new IllegalArgumentException("Id van mens moet positief zijn");
        }
        if (aanMensId <= 0) {
            throw new IllegalArgumentException("Id aan mens moet positief zijn");
        }
        if (vanMensId == aanMensId) {
            throw new IllegalArgumentException("Je kan niet aan jezelf schenken");
        }
        if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Het bedrag moet positief zijn");
        }
        this.id = 0;
        this.vanMensId = vanMensId;
        this.aanMensId = aanMensId;
        this.wanneer = LocalDateTime.now();
        this.bedrag = bedrag;
    }

    public long getId() {
        return id;
    }

    public long getVanMensId() {
        return vanMensId;
    }

    public long getAanMensId() {
        return aanMensId;
    }

    public LocalDateTime getWanneer() {
        return wanneer;
    }

    public BigDecimal getBedrag() {
        return bedrag;
    }
}
