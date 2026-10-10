
package io.github.ashar;

import java.util.Map;

public final class JsonSerializer {

    private JsonSerializer() {
    }

    public static String stringify(JsonValue value) {
        StringBuilder output = new StringBuilder();
        writeValue(value, output);
        return output.toString();
    }

    public static String prettyPrint(JsonValue value) {
        StringBuilder output = new StringBuilder();
        writePrettyValue(value, output, 0);
        return output.toString();
    }

    private static void writeValue(JsonValue value, StringBuilder output) {
        if (value == null) {
            throw new IllegalArgumentException("JSON value cannot be null");
        }

        if (value instanceof JsonString string) {
            writeString(string.value(), output);
        } else if (value instanceof JsonNumber number) {
            output.append(number.value().toString());
        } else if (value instanceof JsonBoolean bool) {
            output.append(bool.value());
        } else if (value instanceof JsonNull) {
            output.append("null");
        } else if (value instanceof JsonArray array) {
            output.append('[');
            for (int i = 0; i < array.size(); i++) {
                if (i > 0) output.append(',');
                writeValue(array.get(i), output);
            }
            output.append(']');
        } else if (value instanceof JsonObject object) {
            output.append('{');
            boolean first = true;

            for (Map.Entry<String, JsonValue> entry
                    : object.values().entrySet()) {
                if (!first) output.append(',');
                first = false;

                writeString(entry.getKey(), output);
                output.append(':');
                writeValue(entry.getValue(), output);
            }

            output.append('}');
        } else {
            throw new IllegalArgumentException(
                    "Unsupported JSON value type: " + value.getClass());
        }
    }

    private static void writePrettyValue(
            JsonValue value, StringBuilder output, int depth) {

        if (!(value instanceof JsonObject)
                && !(value instanceof JsonArray)) {
            writeValue(value, output);
            return;
        }

        if (value instanceof JsonArray array) {
            if (array.size() == 0) {
                output.append("[]");
                return;
            }

            output.append("[\n");
            for (int i = 0; i < array.size(); i++) {
                indent(output, depth + 1);
                writePrettyValue(array.get(i), output, depth + 1);

                if (i < array.size() - 1) output.append(',');
                output.append('\n');
            }
            indent(output, depth);
            output.append(']');
            return;
        }

        JsonObject object = (JsonObject) value;

        if (object.size() == 0) {
            output.append("{}");
            return;
        }

        output.append("{\n");
        int index = 0;

        for (Map.Entry<String, JsonValue> entry
                : object.values().entrySet()) {
            indent(output, depth + 1);
            writeString(entry.getKey(), output);
            output.append(": ");
            writePrettyValue(entry.getValue(), output, depth + 1);

            if (++index < object.size()) output.append(',');
            output.append('\n');
        }

        indent(output, depth);
        output.append('}');
    }

    private static void writeString(String value, StringBuilder output) {
        output.append('"');

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
                case '"' -> output.append("\\\"");
                case '\\' -> output.append("\\\\");
                case '\b' -> output.append("\\b");
                case '\f' -> output.append("\\f");
                case '\n' -> output.append("\\n");
                case '\r' -> output.append("\\r");
                case '\t' -> output.append("\\t");
                default -> {
                    if (c < 0x20) {
                        output.append("\\u");
                        String hex = Integer.toHexString(c);
                        output.append("0".repeat(4 - hex.length()));
                        output.append(hex);
                    } else {
                        output.append(c);
                    }
                }
            }
        }

        output.append('"');
    }

    private static void indent(StringBuilder output, int depth) {
        output.append("    ".repeat(depth));
    }
}
