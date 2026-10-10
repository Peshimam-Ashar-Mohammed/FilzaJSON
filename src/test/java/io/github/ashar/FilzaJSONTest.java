
package io.github.ashar;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class FilzaJSONTest {

    @Test
    void parsesString() {
        assertEquals(
                new JsonString("hello"),
                FilzaJSON.parse("\"hello\"")
        );
    }

    @Test
    void parsesNumberWithoutLosingDecimalPrecision() {
        JsonNumber number = assertInstanceOf(
                JsonNumber.class,
                FilzaJSON.parse("123.4500")
        );

        assertEquals(
                0,
                new BigDecimal("123.4500").compareTo(number.value())
        );
    }

    @Test
    void parsesBooleansAndNull() {
        assertEquals(new JsonBoolean(true), FilzaJSON.parse("true"));
        assertEquals(new JsonBoolean(false), FilzaJSON.parse("false"));
        assertEquals(JsonNull.INSTANCE, FilzaJSON.parse("null"));
    }

    @Test
    void parsesNestedObjectAndArray() {
        JsonObject object = assertInstanceOf(
                JsonObject.class,
                FilzaJSON.parse(
                        "{\"name\":\"Ashar\",\"scores\":[10,20],\"active\":true}"
                )
        );

        assertEquals(new JsonString("Ashar"), object.get("name"));

        JsonArray scores = assertInstanceOf(
                JsonArray.class,
                object.get("scores")
        );

        assertEquals(2, scores.size());
        assertEquals(new JsonNumber("10"), scores.get(0));
        assertEquals(new JsonNumber("20"), scores.get(1));
    }

    @Test
    void parsesEscapedCharacters() {
        assertEquals(
                new JsonString("line1\nline2\t\"quoted\""),
                FilzaJSON.parse("\"line1\\nline2\\t\\\"quoted\\\"\"")
        );
    }

    @Test
    void preservesObjectInsertionOrder() {
        JsonObject object = assertInstanceOf(
                JsonObject.class,
                FilzaJSON.parse("{\"b\":1,\"a\":2}")
        );

        assertEquals(
                java.util.List.of("b", "a"),
                java.util.List.copyOf(object.values().keySet())
        );
    }

    @Test
    void rejectsInvalidJson() {
        assertThrows(JsonException.class, () -> FilzaJSON.parse("{"));
        assertThrows(JsonException.class, () -> FilzaJSON.parse("[1,]"));
        assertThrows(JsonException.class, () -> FilzaJSON.parse("01"));
        assertThrows(JsonException.class, () -> FilzaJSON.parse("\"bad\\x\""));
        assertThrows(JsonException.class, () -> FilzaJSON.parse("true false"));
    }

    @Test
    void rejectsDuplicateObjectKeys() {
        assertThrows(
                JsonException.class,
                () -> FilzaJSON.parse("{\"x\":1,\"x\":2}")
        );
    }

    @Test
    void serializesCompactJson() {
        JsonValue value = FilzaJSON.parse("{\"name\":\"Ashar\",\"age\":20}");

        assertEquals(
                "{\"name\":\"Ashar\",\"age\":20}",
                FilzaJSON.stringify(value)
        );
    }

    @Test
    void prettyPrintsJson() {
        JsonValue value = FilzaJSON.parse("{\"a\":[1,2]}");

        assertEquals(
                "{\n    \"a\": [\n        1,\n        2\n    ]\n}",
                FilzaJSON.prettyPrint(value)
        );
    }

    @Test
    void rejectsTrailingCharacters() {
        assertThrows(JsonException.class, () -> FilzaJSON.parse("null xyz"));
    }

    @Test
    void rejectsNullInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FilzaJSON.parse(null)
        );
    }
}
