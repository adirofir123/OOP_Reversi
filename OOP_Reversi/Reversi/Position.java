import java.util.Objects;

public class Position {
private int x,y;
private String isFlipped;


    public Position(int row, int col) {
        this.x = row;
        this.y = col;
    }

    public int row() {
        return x;
    }

    public int col() {
        return y;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;

    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
