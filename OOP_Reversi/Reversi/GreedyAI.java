import java.util.Comparator;
import java.util.List;


public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Get the list of valid moves available for this AI player
        List<Position> validMoves = gameStatus.ValidMoves();

        // If no valid moves are available, return null
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }

        Comparator<Position> positionComparator = Comparator.comparingInt(gameStatus::countFlips)
                .thenComparingInt(Position::col).thenComparingInt(Position::row);

        Position positionToReturn = validMoves.stream().max(positionComparator).orElse(null);

        // Return the best move found, represented by a new SimpleDisc for the current player
        return new Move(positionToReturn, new SimpleDisc(this));
    }
}