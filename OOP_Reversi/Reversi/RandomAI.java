import java.util.List;
import java.util.Random;

/**
 * RandomAI class represents an AI player that makes random moves from the available valid moves.
 * It extends the AIPlayer class and overrides the makeMove() method to return a random valid move.
 */
public class RandomAI extends AIPlayer {

    // Constructor to initialize the RandomAI player with player type (Player 1 or Player 2)
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * This method generates a random valid move for the AI to play.
     *
     * @param gameStatus The current game status, providing valid moves and game information.
     * @return A Move object representing a randomly chosen valid move.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }

        // Randomly select a valid move from the list of valid moves
        Random random = new Random();
        Position randomMove = validMoves.get(random.nextInt(validMoves.size()));  // Get a random index

        // 15% chance to select a special disc (BombDisc or UnflippableDisc)
        boolean placeSpecialDisc = random.nextInt(100) < 15;
        if (placeSpecialDisc) {
            // Decide which special disc to place if allowed by the game state
            boolean canPlaceBomb = this.getNumber_of_bombs() > -1;
            this.reduce_bomb();
            boolean canPlaceUnflippable = this.getNumber_of_unflippedable() > -1;
            this.reduce_unflippedable();

            // Randomly decide between BombDisc and UnflippableDisc if both are allowed
            if (canPlaceBomb && canPlaceUnflippable) {
                return new Move(randomMove, random.nextBoolean() ? new BombDisc(this) : new UnflippableDisc(this));
            } else if (canPlaceBomb) {
                return new Move(randomMove, new BombDisc(this));
            } else if (canPlaceUnflippable) {
                return new Move(randomMove, new UnflippableDisc(this));
            }
        }

        // Default case: Place a regular SimpleDisc
        return new Move(randomMove, new SimpleDisc(this));
    }
}