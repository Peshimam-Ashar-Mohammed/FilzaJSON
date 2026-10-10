
package io.github.ashar;

public class JsonException extends RuntimeException {

    private final int position;
    private final int line;
    private final int column;

    public JsonException(
            String message, int position, int line, int column) {
        super(message + " (line " + line + ", column " + column + ")");
        this.position = position;
        this.line = line;
        this.column = column;
    }

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
