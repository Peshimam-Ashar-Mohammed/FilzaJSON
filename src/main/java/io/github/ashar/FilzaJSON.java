
package io.github.ashar;

public final class FilzaJSON {

    private FilzaJSON() {
    }

    /**
     * Parses JSON text into a JSON value.
     */
    public static JsonValue parse(String json) {
        return JsonParser.parse(json);
    }

    /**
     * Converts a JSON value into compact JSON text.
     */
    public static String stringify(JsonValue value) {
        return JsonSerializer.stringify(value);
    }

    /**
     * Converts a JSON value into indented JSON text.
     */
    public static String prettyPrint(JsonValue value) {
        return JsonSerializer.prettyPrint(value);
    }
}
