# FilzaJSON

### A lightweight JSON parser and serializer built from scratch in Java 21.

[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.oracle.com/java/)
[![Version](https://img.shields.io/badge/version-0.1.0-blue)](https://github.com/Peshimam-Ashar-Mohammed/FilzaJSON/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

FilzaJSON is a lightweight Java library for parsing JSON text into Java value objects and serializing those values back into JSON. It is implemented from scratch without third-party JSON parsing libraries or parser generators.

The goal is to provide a simple API, readable implementation, and an opportunity to explore how JSON parsing and serialization work internally.

## Features

- **JSON parsing** — strings, numbers, booleans, `null`, objects, and arrays.
- **Nested structures** — parse objects containing arrays and other objects.
- **String escaping** — supports common JSON escapes and `\uXXXX` Unicode escapes.
- **Number handling** — uses `BigDecimal` to represent JSON numbers without unnecessary binary floating-point precision loss.
- **Serialization** — convert JSON values into compact JSON strings.
- **Pretty printing** — generate readable, indented JSON.
- **Useful errors** — parsing exceptions include a message, position, line, and column.
- **Insertion order** — object keys retain their insertion order.
- **Duplicate-key validation** — duplicate object keys are rejected.
- **Immutable collections** — JSON object and array values expose unmodifiable collection snapshots.

## Requirements

- Java Development Kit (JDK) 21 or later
- No third-party runtime dependencies

## Download

**Latest release:** [FilzaJSON v0.1.0](https://github.com/Peshimam-Ashar-Mohammed/FilzaJSON/releases/tag/v0.1.0)

[Download the JAR](https://github.com/Peshimam-Ashar-Mohammed/FilzaJSON/releases/download/v0.1.0/filzajson-0.1.0.jar)

Download `filzajson-0.1.0.jar` from the release assets and add it to your Java project's classpath.

> FilzaJSON is distributed through GitHub Releases. It has not been published to Maven Central.

## Quick Start

### 1. Parse JSON

```java
import io.github.ashar.FilzaJSON;
import io.github.ashar.JsonObject;
import io.github.ashar.JsonString;
import io.github.ashar.JsonValue;

public class Main {
    public static void main(String[] args) {
        String json = """
                {
                    "name": "Ashar",
                    "language": "Java",
                    "active": true
                }
                """;

        JsonValue value = FilzaJSON.parse(json);
        JsonObject object = (JsonObject) value;

        String name = ((JsonString) object.get("name")).value();

        System.out.println(name); // Ashar
    }
}
```

### 2. Serialize JSON

```java
import io.github.ashar.FilzaJSON;
import io.github.ashar.JsonValue;

public class Main {
    public static void main(String[] args) {
        JsonValue value = FilzaJSON.parse(
                "{\"project\":\"FilzaJSON\",\"version\":0.1}"
        );

        System.out.println(FilzaJSON.stringify(value));
    }
}
```

Output:

```json
{"project":"FilzaJSON","version":0.1}
```

### 3. Pretty-print JSON

```java
JsonValue value = FilzaJSON.parse(
        "{\"name\":\"Ashar\",\"skills\":[\"Java\",\"JSON\"]}"
);

System.out.println(FilzaJSON.prettyPrint(value));
```

Output:

```json
{
    "name": "Ashar",
    "skills": [
        "Java",
        "JSON"
    ]
}
```

## Installation

### Option A: Regular Java project

1. Download the JAR from the [v0.1.0 release](https://github.com/Peshimam-Ashar-Mohammed/FilzaJSON/releases/tag/v0.1.0).
2. Create a `lib` directory in your project and place the JAR inside it.
3. Import the classes you need.

**Compile on Windows:**

```bat
javac -cp "lib\filzajson-0.1.0.jar" Main.java
```

**Run on Windows:**

```bat
java -cp ".;lib\filzajson-0.1.0.jar" Main
```

On Linux or macOS, use `:` instead of `;` in the classpath.

### Option B: Maven project using the local JAR

FilzaJSON is not on Maven Central. You can install the downloaded JAR into your local Maven repository.

Run:

```bat
mvn install:install-file -Dfile=filzajson-0.1.0.jar -DgroupId=io.github.ashar -DartifactId=filzajson -Dversion=0.1.0 -Dpackaging=jar
```

Then add this dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.github.ashar</groupId>
    <artifactId>filzajson</artifactId>
    <version>0.1.0</version>
</dependency>
```

The JAR must be installed locally on each development machine using this method.

## Public API

All classes are in the package `io.github.ashar`.

### `FilzaJSON`

The main entry point for library users.

| Method | Description |
|---|---|
| `parse(String json)` | Parses JSON text into a `JsonValue`. |
| `stringify(JsonValue value)` | Produces compact JSON. |
| `prettyPrint(JsonValue value)` | Produces indented JSON. |

### `JsonValue`

A sealed interface representing the supported JSON value types.

| Type | Represents |
|---|---|
| `JsonString` | JSON strings |
| `JsonNumber` | JSON numbers using `BigDecimal` |
| `JsonBoolean` | `true` or `false` |
| `JsonNull.INSTANCE` | JSON `null` |
| `JsonObject` | JSON objects |
| `JsonArray` | JSON arrays |

### `JsonObject`

Provides access to object members:

```java
JsonObject object = (JsonObject) FilzaJSON.parse(
        "{\"name\":\"Ashar\",\"age\":20}"
);

JsonValue name = object.get("name");
boolean hasAge = object.containsKey("age");
int memberCount = object.size();
```

Object members are exposed through an unmodifiable map:

```java
Map<String, JsonValue> members = object.values();
```

### `JsonArray`

Provides indexed access to array values:

```java
JsonArray array = (JsonArray) FilzaJSON.parse(
        "[\"Java\", \"Python\", \"C++\"]"
);

JsonValue first = array.get(0);
int count = array.size();
```

Array values are exposed through an unmodifiable list:

```java
List<JsonValue> values = array.values();
```

### `JsonException`

Malformed JSON causes a `JsonException`, which extends `RuntimeException`.

```java
try {
    FilzaJSON.parse("{invalid}");
} catch (JsonException exception) {
    System.out.println(exception.getMessage());
    System.out.println(exception.getLine());
    System.out.println(exception.getColumn());
    System.out.println(exception.getPosition());
}
```

Line and column values are one-based; the position is a zero-based character offset.

## Validation and Limitations

FilzaJSON is an initial release. It has tests for core parsing and serialization behavior, but it should not yet be considered a fully validated, production-ready replacement for established JSON libraries.

Current implementation notes:

- Duplicate object keys are rejected.
- Parsing errors report the location where an error is detected.
- Unicode escapes are decoded as individual UTF-16 code units; additional validation is needed for surrogate-pair correctness.
- Extremely deeply nested input may exhaust the Java call stack.
- Object and array wrappers protect their collections, but nested values are not deep-copied.
- Numeric values are represented using `BigDecimal`; serialization preserves the stored decimal representation rather than guaranteeing the original input's exact formatting.
- Pretty printing uses four spaces for indentation.

For production systems that process untrusted or large JSON inputs, consider resource limits and additional validation before deployment.

## Testing

The project uses Maven and JUnit 5.

Run the test suite:

```bash
mvn clean verify
```

The initial release was validated with:

- 12 automated unit tests.
- 9 standalone integration checks using the compiled JAR from a separate Java application.

These tests cover core parsing, nested data, serialization, pretty printing, invalid input, and JSON round-tripping. They do not establish complete conformance with every JSON specification edge case.

## Build from Source

Clone the repository:

```bash
git clone https://github.com/Peshimam-Ashar-Mohammed/FilzaJSON.git
cd FilzaJSON
```

Build and test the Maven project:

```bash
mvn clean verify
```

The JAR is generated at:

```text
target/filzajson-0.1.0.jar
```

## Project Structure

```text
filzajson/
├── src/
│   ├── main/java/io/github/ashar/
│   │   ├── FilzaJSON.java
│   │   ├── JsonParser.java
│   │   ├── JsonSerializer.java
│   │   ├── JsonValue.java
│   │   ├── JsonString.java
│   │   ├── JsonNumber.java
│   │   ├── JsonBoolean.java
│   │   ├── JsonNull.java
│   │   ├── JsonObject.java
│   │   └── JsonArray.java
│   └── test/java/io/github/ashar/
│       └── FilzaJSONTest.java
├── pom.xml
├── README.md
└── LICENSE
```

## Roadmap

Potential improvements include:

- Stronger Unicode surrogate-pair validation.
- Recursion and input-size limits.
- More specification-driven tests.
- Improved APIs for accessing typed object and array values.
- Additional error-reporting tests.
- Benchmarks against established JSON libraries.
- Further API and documentation improvements.

## Contributing

Suggestions, bug reports, and pull requests are welcome.

When reporting a bug, include the input JSON, the expected behavior, the actual behavior, and the Java version when relevant.

## License

FilzaJSON is distributed under the MIT License. See [LICENSE](LICENSE) for details.

---

**Built by [Ashar](https://github.com/Peshimam-Ashar-Mohammed)**

If you find FilzaJSON useful, consider starring the repository or contributing improvements.
