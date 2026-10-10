
package io.github.ashar;

import java.math.BigDecimal;
import java.util.Objects;

public record JsonNumber(BigDecimal value) implements JsonValue {

    public JsonNumber {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public JsonNumber(String value) {
        this(new BigDecimal(value));
    }
}
