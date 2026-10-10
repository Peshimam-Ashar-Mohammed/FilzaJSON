
package io.github.ashar;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record JsonObject(Map<String, JsonValue> values)
        implements JsonValue {

    public JsonObject {
        Objects.requireNonNull(values, "values cannot be null");
        values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public JsonValue get(String key) {
        return values.get(key);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public int size() {
        return values.size();
    }
}
