package model.entity;

/**
 * Enum representing the four cardinal directions with their respective coordinate changes.
 */
public enum Direction {
    UP(0, -1, 0), RIGHT(1, 0, 1), DOWN(0, 1, 2), LEFT(-1, 0, 3), ;

    public final int dx;
    public final int dy;
    public final int directionIdx;

    Direction(int dx, int dy, int directionIdx) {
        this.dx = dx;
        this.dy = dy;
        this.directionIdx = directionIdx;
    }

    /**
     * Returns the direction directly opposite to this one.
     *
     * @return the opposite direction
     */
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
