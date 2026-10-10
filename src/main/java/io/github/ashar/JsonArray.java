
package io.github.ashar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record JsonArray(List<JsonValue> values)
        implements JsonValue {

    public JsonArray {
        Objects.requireNonNull(values, "values cannot be null");
        values = Collections.unmodifiableList(new ArrayList<>(values));
    }

    public JsonValue get(int index) {
        return values.get(index);
    }

    public int size() {
        return values.size();
    }
}
