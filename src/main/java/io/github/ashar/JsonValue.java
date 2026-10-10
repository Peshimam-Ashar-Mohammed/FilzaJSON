
package io.github.ashar;

/**
 * Represents any valid JSON value.
 */
public sealed interface JsonValue
        permits JsonString, JsonNumber, JsonBoolean, JsonNull,
                JsonObject, JsonArray {

}
