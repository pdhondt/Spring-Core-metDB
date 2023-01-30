package be.vdab.geld.exceptions;

import java.io.Serial;

public class MensNietGevondenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long id;
    public MensNietGevondenException(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
}
