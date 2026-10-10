
package io.github.ashar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonParser {

    private final String input;
    private int position;

    private JsonParser(String input) {
        if (input == null) {
            throw new IllegalArgumentException("JSON input cannot be null");
        }
        this.input = input;
    }

    public static JsonValue parse(String input) {
        JsonParser parser = new JsonParser(input);
        parser.skipWhitespace();

        JsonValue result = parser.parseValue();
        parser.skipWhitespace();

        if (!parser.isAtEnd()) {
            parser.fail("Unexpected characters after JSON value");
        }

        return result;
    }

    private JsonValue parseValue() {
        if (isAtEnd()) {
            return fail("Expected a JSON value");
        }

        return switch (current()) {
            case '"' -> new JsonString(parseString());
            case '{' -> parseObject();
            case '[' -> parseArray();
            case 't' -> {
                consumeLiteral("true");
                yield new JsonBoolean(true);
            }
            case 'f' -> {
                consumeLiteral("false");
                yield new JsonBoolean(false);
            }
            case 'n' -> {
                consumeLiteral("null");
                yield JsonNull.INSTANCE;
            }
            default -> {
                if (current() == '-' || isDigit(current())) {
                    yield parseNumber();
                }
                yield fail("Unexpected character '" + current() + "'");
            }
        };
    }

    private JsonObject parseObject() {
        expect('{');
        skipWhitespace();

        Map<String, JsonValue> values = new LinkedHashMap<>();

        if (consumeIf('}')) {
            return new JsonObject(values);
        }

        while (true) {
            skipWhitespace();

            if (isAtEnd() || current() != '"') {
                return fail("Expected a quoted object key");
            }

            String key = parseString();
            skipWhitespace();
            expect(':');
            skipWhitespace();

            JsonValue value = parseValue();

            if (values.containsKey(key)) {
                fail("Duplicate object key: " + key);
            }

            values.put(key, value);
            skipWhitespace();

            if (consumeIf('}')) {
                return new JsonObject(values);
            }

            expect(',');
            skipWhitespace();
        }
    }

    private JsonArray parseArray() {
        expect('[');
        skipWhitespace();

        ArrayList<JsonValue> values = new ArrayList<>();

        if (consumeIf(']')) {
            return new JsonArray(values);
        }

        while (true) {
            skipWhitespace();
            values.add(parseValue());
            skipWhitespace();

            if (consumeIf(']')) {
                return new JsonArray(values);
            }

            expect(',');
            skipWhitespace();
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder result = new StringBuilder();

        while (!isAtEnd()) {
            char c = advance();

            if (c == '"') {
                return result.toString();
            }

            if (c < 0x20) {
                fail("Unescaped control character in string");
            }

            if (c != '\\') {
                result.append(c);
                continue;
            }

            if (isAtEnd()) {
                return fail("Unfinished escape sequence");
            }

            char escaped = advance();

            switch (escaped) {
                case '"' -> result.append('"');
                case '\\' -> result.append('\\');
                case '/' -> result.append('/');
                case 'b' -> result.append('\b');
                case 'f' -> result.append('\f');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 't' -> result.append('\t');
                case 'u' -> result.append(parseUnicodeEscape());
                default -> fail("Invalid escape sequence: \\" + escaped);
            }
        }

        return fail("Unterminated JSON string");
    }

    private char parseUnicodeEscape() {
        if (position + 4 > input.length()) {
            return fail("Incomplete Unicode escape");
        }

        int value = 0;

        for (int i = 0; i < 4; i++) {
            char c = advance();
            int digit = Character.digit(c, 16);

            if (digit == -1) {
                return fail("Invalid hexadecimal digit in Unicode escape");
            }

            value = value * 16 + digit;
        }

        return (char) value;
    }

    private JsonNumber parseNumber() {
        int start = position;

        consumeIf('-');

        if (isAtEnd()) {
            return fail("Incomplete number");
        }

        if (consumeIf('0')) {
            if (!isAtEnd() && isDigit(current())) {
                return fail("Leading zeros are not allowed");
            }
        } else {
            if (!isDigitOneToNine(current())) {
                return fail("Expected a digit in number");
            }

            while (!isAtEnd() && isDigit(current())) {
                advance();
            }
        }

        if (consumeIf('.')) {
            if (isAtEnd() || !isDigit(current())) {
                return fail("Expected digits after decimal point");
            }

            while (!isAtEnd() && isDigit(current())) {
                advance();
            }
        }

        if (!isAtEnd() && (current() == 'e' || current() == 'E')) {
            advance();

            if (!isAtEnd() && (current() == '+' || current() == '-')) {
                advance();
            }

            if (isAtEnd() || !isDigit(current())) {
                return fail("Expected exponent digits");
            }

            while (!isAtEnd() && isDigit(current())) {
                advance();
            }
        }

        try {
            return new JsonNumber(new BigDecimal(input.substring(start, position)));
        } catch (NumberFormatException ex) {
            return fail("Invalid JSON number");
        }
    }

    private void consumeLiteral(String literal) {
        for (int i = 0; i < literal.length(); i++) {
            if (isAtEnd() || current() != literal.charAt(i)) {
                fail("Expected '" + literal + "'");
            }
            advance();
        }
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = current();

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                advance();
            } else {
                break;
            }
        }
    }

    private void expect(char expected) {
        if (isAtEnd() || current() != expected) {
            fail("Expected '" + expected + "'");
        }
        advance();
    }

    private boolean consumeIf(char expected) {
        if (!isAtEnd() && current() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private char current() {
        return input.charAt(position);
    }

    private char advance() {
        return input.charAt(position++);
    }

    private boolean isAtEnd() {
        return position >= input.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isDigitOneToNine(char c) {
        return c >= '1' && c <= '9';
    }

    private <T> T fail(String message) {
        int line = 1;
        int column = 1;

        for (int i = 0; i < position; i++) {
            if (input.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }

        throw new JsonException(message, position, line, column);
    }
}
