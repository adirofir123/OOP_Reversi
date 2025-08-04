import java.util.*;

public class GameLogic implements PlayableLogic {

    SimpleDisc disc44;
    SimpleDisc disc33;
    SimpleDisc disc34;
    SimpleDisc disc43;
    private Player CurrentPlayer;
    private Player FirstPlayer;
    private Player SecondPlayer;
    private final Disc[][] GameBoard = new Disc[8][8];
    private List<Position> ValidMoves;
    private final Stack<Move> moveStack = new Stack<>();

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        // Check if the position is a valid move for the current player
        if (!ValidMoves.contains(a)) {
            return false;  // Return false if the position is not valid
        }

        // Check if the disc is of type "⭕" (unflippable)
        if (disc.getType().equals("⭕")) {
            // Ensure the current player has unflippedable discs available
            if (CurrentPlayer.getNumber_of_unflippedable() == 0) {
                System.out.println("Players are limited to 2 unflippable discs");
                return false;  // Return false if no unflippable discs are left
            }
            // Reduce the number of unflippedable discs for the current player
            CurrentPlayer.reduce_unflippedable();
        }

        // Check if the disc is of type "💣" (bomb)
        if (disc.getType().equals("💣")) {
            // Ensure the current player has bomb discs available
            if (CurrentPlayer.getNumber_of_bombs() == 0) {
                System.out.println("Player are limited to 3 bomb discs");
                return false;  // Return false if no bomb discs are left
            }
            // Reduce the number of bomb discs for the current player
            CurrentPlayer.reduce_bomb();
        }

        // Place the disc on the game board at the specified position
        GameBoard[a.row()][a.col()] = disc;

        // Determine which player made the move (Player 1 or Player 2)
        int playerNum = CurrentPlayer == FirstPlayer ? 1 : 2;

        // Print a message indicating the player and the move made
        System.out.println("Player " + playerNum + " placed a " + disc.getType() + " in (" + a.row() + ", " + a.col() + ")");

        // Call the flipDiscs method to flip the appropriate discs
        List<Move.FlippedDisc> flippedDiscs = flipDiscs(a);

        // Store the move (position, disc type, and flipped discs) in the move stack for undo functionality
        moveStack.push(new Move(a, disc, flippedDiscs));

        // Switch the current player for the next turn
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;

        // Print an empty line for readability
        System.out.println();

        // Update the list of valid moves after the disc has been placed
        ValidMoves();

        // Return true indicating that the disc has been successfully placed
        return true;
    }


    public List<Move.FlippedDisc> flipDiscs(Position a) {
        List<Move.FlippedDisc> flippedDiscs = new ArrayList<>();
        Set<Position> flippedPositions = new HashSet<>();  // Track flipped positions to avoid duplicates

        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1},   // Right
                {-1, -1}, // Top-left diagonal
                {-1, 1},  // Top-right diagonal
                {1, -1},  // Bottom-left diagonal
                {1, 1}    // Bottom-right diagonal
        };

        for (int[] direction : directions) {
            int r = a.row();
            int c = a.col();
            List<Position> discsToFlip = new ArrayList<>();

            while (true) {
                r += direction[0];
                c += direction[1];

                if (r < 0 || r >= 8 || c < 0 || c >= 8) {
                    break;
                }

                if (GameBoard[r][c] == null) {
                    break;
                }

                if (GameBoard[r][c].getType().equals("💣") && GameBoard[r][c].getOwner() != CurrentPlayer) {
                    flipBomb(new Position(r, c), discsToFlip);
                }

                if (GameBoard[r][c].getOwner() != CurrentPlayer) {
                    discsToFlip.add(new Position(r, c));
                } else if (GameBoard[r][c].getOwner() == CurrentPlayer && !discsToFlip.isEmpty()) {
                    for (Position flipPos : discsToFlip) {
                        if (!flippedPositions.contains(flipPos)) {  // Check if the disc was already flipped
                            flippedDiscs.add(new Move.FlippedDisc(flipPos, GameBoard[flipPos.row()][flipPos.col()].getOwner()));
                            GameBoard[flipPos.row()][flipPos.col()].setOwner(CurrentPlayer);
                            flippedPositions.add(flipPos);  // Mark as flipped
                            System.out.println((CurrentPlayer == FirstPlayer ? "Player 1 " : "Player 2 ") + "flipped the "
                                    + GameBoard[flipPos.row()][flipPos.col()].getType()
                                    + " in (" + (flipPos.row()) + ", " + (flipPos.col()) + ")");
                        }
                    }
                    break;
                }
            }
        }
        return flippedDiscs;
    }


    private void flipBomb(Position bombPos, List<Position> discsToFlip) {
        // Get the bomb at the current position from the game board
        Disc bombDisc = GameBoard[bombPos.row()][bombPos.col()];

        // If this bomb has already exploded, do not trigger it again
        if (bombDisc.GetHasExploded()) {
            return; // Exit the method if the bomb has already exploded
        }

        // Mark this bomb as exploded to prevent re-triggering it in the future
        bombDisc.SetHasExploded(true);

        // Directions array to check the surrounding positions (row and column directions -1, 0, 1)
        int[] dirs = {-1, 0, 1};  // The bomb affects its adjacent cells, including diagonals
        for (int dr : dirs) {  // Loop through row directions (-1, 0, 1)
            for (int dc : dirs) {  // Loop through column directions (-1, 0, 1)
                if (dr == 0 && dc == 0) continue;  // Skip the center position (the bomb itself)

                // Calculate the new row and column by adding the direction to the bomb's position
                int nr = bombPos.row() + dr;
                int nc = bombPos.col() + dc;

                // Check if the new position is within the bounds of the board
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8) {
                    // If there's a disc at this position (not null), and it's not an empty disc (⭕), add it to the flip list
                    if (GameBoard[nr][nc] != null && !Objects.equals(GameBoard[nr][nc].getType(), "⭕")) {
                        discsToFlip.add(new Position(nr, nc));  // Add the position to the flip list

                        // If this disc is a bomb, recursively trigger its explosion as well
                        if (GameBoard[nr][nc].getType().equals("💣")) {
                            flipBomb(new Position(nr, nc), discsToFlip);  // Recursively flip the neighboring bomb
                        }
                    }
                }
            }
        }

        // After processing the bomb, reset its exploded status to allow it to be triggered again if needed
        bombDisc.SetHasExploded(false);
    }


    @Override
    public Disc getDiscAtPosition(Position position) {
        // Get the disc located at the given position on the game board
        Disc disc = GameBoard[position.row()][position.col()];

        // If the position is empty (i.e., no disc), return null
        if (GameBoard[position.row()][position.col()] == null) {
            return null;
        }

        // Return the disc found at the given position
        return disc;
    }

    @Override
    public int getBoardSize() {
        // Return the size of the game board (assuming a square board)
        return GameBoard.length;
    }

    @Override
    public List<Position> ValidMoves() {
        // Create a list to store valid moves
        List<Position> validMoves = new ArrayList<>();

        // Iterate through every position on the game board (8x8 grid)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col); // Create a position object for each cell

                // Check if the current position is empty (no disc placed there)
                if (GameBoard[row][col] == null) {
                    // If placing a disc here would flip any other discs, it's a valid move
                    if (countFlips(pos) > 0) {
                        validMoves.add(pos);  // Add this position to the list of valid moves
                    }
                }
            }
        }

        // Store the list of valid moves in the instance variable (if needed later)
        this.ValidMoves = validMoves;

        // Return the list of valid moves
        return validMoves;
    }


    @Override
    public int countFlips(Position a) {
        // Array of directions to check for potential disc flips around the given position
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1},  // Right
                {-1, -1}, // Top-left diagonal
                {-1, 1},  // Top-right diagonal
                {1, -1},  // Bottom-left diagonal
                {1, 1}    // Bottom-right diagonal
        };

        // Set to keep track of all unique discs that need to be flipped
        Set<Disc> totalFlips = new HashSet<>();

        // Loop through each direction and gather all discs to flip
        for (int[] direction : directions) {
            // Add the discs flipped in the current direction to the totalFlips set
            totalFlips.addAll(getFlipsEachDir(a, direction[0], direction[1]));
        }

        // Return the total number of unique discs that would be flipped
        return totalFlips.size();
    }

    public Set<Disc> CheckBombFlips(Position a) {
        // Set to store the discs that are affected by the bomb's explosion
        Set<Disc> BombFlip = new HashSet<>();
        int[] dirs = {-1, 0, 1};  // Directions for row and column (Up, Down, Left, Right, Diagonals)

        // Iterate over all surrounding directions to check for discs to flip around the bomb
        for (int dr : dirs) {
            for (int dc : dirs) {
                // Calculate the position of the surrounding disc
                int nr = a.row() + dr;
                int nc = a.col() + dc;

                // Check if the position is within bounds and is not empty
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && GameBoard[nr][nc] != null && !Objects.equals(GameBoard[nr][nc].getType(), "⭕")) {
                    // If it's an opponent's disc, check if it's a bomb or a regular disc to flip
                    if (GameBoard[nr][nc].getOwner() != CurrentPlayer) {
                        // If the disc is a bomb and hasn't exploded, recursively check for further bomb explosions
                        if (Objects.equals(GameBoard[nr][nc].getType(), "💣")
                                && GameBoard[nr][nc].getOwner() != CurrentPlayer
                                && !GameBoard[nr][nc].GetHasExploded()) {
                            // Add the bomb disc itself to the list
                            BombFlip.add(GameBoard[nr][nc]);

                            // Mark this bomb as exploded to prevent re-triggering
                            GameBoard[nr][nc].SetHasExploded(true);

                            // Recursively check for further bombs that could be triggered
                            BombFlip.addAll(CheckBombFlips(new Position(nr, nc)));

                            // Reset the bomb's exploded state after recursion is complete
                            GameBoard[nr][nc].SetHasExploded(false);
                        }
                        // If it's not a bomb, simply add the opponent's disc to the flip list
                        else {
                            BombFlip.add(GameBoard[nr][nc]);
                        }
                    }
                }
            }
        }
        // Return the set of discs that would be flipped by the bomb's explosion
        return BombFlip;
    }


    public Set<Disc> getFlipsEachDir(Position a, int rowDirection, int colDirection) {
        // Set to store discs that will be flipped in the given direction
        Set<Disc> flipped = new HashSet<>();

        // Starting row and column from the given position 'a'
        int r = a.row();
        int c = a.col();

        // Boolean flag to check if we've encountered opponent's discs that can be flipped
        boolean validDirection = false;

        // Loop to check in the specified direction (rowDirection, colDirection)
        while (true) {
            // Move to the next position in the given direction
            r += rowDirection;
            c += colDirection;

            // If the position is out of bounds, stop checking this direction
            if (r < 0 || r >= 8 || c < 0 || c >= 8) break;

            // If the cell is empty, stop checking as no discs can be flipped in this direction
            if (GameBoard[r][c] == null) break;

            // If the disc at the current position is a bomb, check for its explosion effect
            if (GameBoard[r][c].getType().equals("💣") && GameBoard[r][c].getOwner() != CurrentPlayer) {
                // Mark the bomb as exploded temporarily
                GameBoard[r][c].SetHasExploded(true);
                // Add any discs flipped due to this bomb's explosion
                flipped.addAll(CheckBombFlips(new Position(r, c)));
                // Reset the bomb's exploded status after checking
                GameBoard[r][c].SetHasExploded(false);
            }

            // If the current position has an opponent's disc, add it to the flipped set
            if (GameBoard[r][c].getOwner() != CurrentPlayer) {
                flipped.add(GameBoard[r][c]);
                validDirection = true;  // Valid direction found since there are opponent's discs
            }
            // If the current position has the current player's disc, and we have flipped opponent's discs in between
            else if (GameBoard[r][c].getOwner() == CurrentPlayer && validDirection) {
                // Return the set of opponent discs that will be flipped in this direction
                return flipped;
            }
            // If it's the current player's disc but there are no opponent's discs between, stop the search
            else {
                break;
            }
        }

        // Return an empty set if no valid flips were found
        return new HashSet<>();
    }


    @Override
    public Player getFirstPlayer() {
        // Returns the first player
        return FirstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        // Returns the second player
        return SecondPlayer;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        // Sets the two players and initializes the current player to the first player
        this.FirstPlayer = player1;
        this.SecondPlayer = player2;
        CurrentPlayer = player1; // Player 1 starts the game
    }

    @Override
    public boolean isFirstPlayerTurn() {
        // Returns true if it's the first player's turn, false otherwise
        return CurrentPlayer == FirstPlayer;
    }

    public List<Position> ValidMovesForPlayer(Player player) {
        // Returns a list of valid moves for the specified player
        List<Position> validMoves = new ArrayList<>();

        // Temporarily store the current player and set the specified player as the current player
        Player originalPlayer = CurrentPlayer;
        CurrentPlayer = player;

        // Iterate through the entire board and check if there are any valid moves for the player
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);

                // If the position is empty and placing a disc would flip any discs, it's a valid move
                if (GameBoard[row][col] == null && countFlips(pos) > 0) {
                    validMoves.add(pos);
                }
            }
        }

        // Restore the original player after checking the valid moves for the given player
        CurrentPlayer = originalPlayer;

        // Return the list of valid moves for the player
        return validMoves;
    }


    @Override
    public boolean isGameFinished() {
        // If there are no valid moves left, the game is finished
        if (ValidMoves().isEmpty()) {
            // If there are no valid moves, the game has ended. Award win based on the disc counts.
            awardWinBasedOnDiscs();
            return true; // Game is finished
        }

        // Count the number of discs currently placed on the board
        int placedDiscs = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameBoard[row][col] != null) {
                    placedDiscs++;  // Increment placedDiscs if the cell contains a disc
                }
            }
        }

        // If all 64 positions on the board are occupied (board is full), the game ends
        if (placedDiscs == 64) {
            // Board is full, determine the winner based on the number of discs
            awardWinBasedOnDiscs();
            return true; // Game is finished
        }

        // Check if either player has valid moves left
        List<Position> firstPlayerMoves = ValidMovesForPlayer(FirstPlayer);
        List<Position> secondPlayerMoves = ValidMovesForPlayer(SecondPlayer);

        // If neither player has valid moves left, the game ends
        if (firstPlayerMoves.isEmpty() && secondPlayerMoves.isEmpty()) {
            // Neither player can make a move, end the game and determine the winner
            awardWinBasedOnDiscs();
            return true; // Game is finished
        }

        // If none of the conditions above are met, the game is still ongoing
        return false; // Game is not finished
    }


    private void awardWinBasedOnDiscs() {
        // If either of the players is not set, the game cannot determine the winner
        if (FirstPlayer == null || SecondPlayer == null) {
            System.out.println("Cannot award win, players not set.");
            return; // Return early if players are not set
        }

        // Initialize counters for the number of discs each player has
        int firstPlayerDiscs = 0;
        int secondPlayerDiscs = 0;

        // Iterate through the game board to count the discs for both players
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameBoard[row][col] != null) {
                    // Increment the respective player's disc count
                    if (GameBoard[row][col].getOwner() == FirstPlayer) {
                        firstPlayerDiscs++;
                    } else if (GameBoard[row][col].getOwner() == SecondPlayer) {
                        secondPlayerDiscs++;
                    }
                }
            }
        }

        // Compare the number of discs for both players and declare the winner
        if (firstPlayerDiscs > secondPlayerDiscs) {
            FirstPlayer.addWin();  // Award the win to Player 1
            System.out.println("Player 1 wins with " + firstPlayerDiscs + " discs! " + "Player 2 had " + secondPlayerDiscs + " discs");
        } else if (secondPlayerDiscs > firstPlayerDiscs) {
            SecondPlayer.addWin();  // Award the win to Player 2
            System.out.println("Player 2 wins with " + secondPlayerDiscs + " discs! " + "Player 1 had " + firstPlayerDiscs + " discs");
        } else {
            System.out.println("The game is a draw!");  // If both players have the same number of discs
        }
    }



    @Override
    public void reset() {
        // Reset the game board by clearing all positions
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GameBoard[row][col] = null;
            }
        }

        // Create initial discs for both players and place them on the board
        disc44 = new SimpleDisc(FirstPlayer);
        disc33 = new SimpleDisc(FirstPlayer);
        disc34 = new SimpleDisc(SecondPlayer);
        disc43 = new SimpleDisc(SecondPlayer);

        // Reset initial board configuration
        GameBoard[4][4] = disc44;  // Player 1's disc at (4,4)
        GameBoard[3][3] = disc33;  // Player 1's disc at (3,3)
        GameBoard[3][4] = disc34;  // Player 2's disc at (3,4)
        GameBoard[4][3] = disc43;  // Player 2's disc at (4,3)

        // Reset bomb and unflippable disc counts for both players
        FirstPlayer.reset_bombs_and_unflippedable();
        SecondPlayer.reset_bombs_and_unflippedable();

        // Clear any previous moves
        moveStack.clear();

        // Set the current player back to Player 1
        CurrentPlayer = FirstPlayer;
    }


    @Override
    public void undoLastMove() {
        System.out.println("Undoing last move:");

        // Check if both players are human and the move history is not empty
        if (!FirstPlayer.isHuman() || !SecondPlayer.isHuman()) {
            System.out.println("Not allowed to undo last move if both players are not human");
            return; // Do not allow undo if one or both players are not human
        }

        if (moveStack.isEmpty()) {
            System.out.println("\t No previous move available to undo");
            return; // No move to undo
        }

        // Get the last move from the move stack
        Move lastmove = moveStack.pop();
        Position lastPlaced = lastmove.position();

        // Remove the disc placed in the last move
        System.out.println("\t Undo: removing " + GameBoard[lastPlaced.row()][lastPlaced.col()].getType() +
                " from (" + lastPlaced.row() + ", " + lastPlaced.col() + ")");
        GameBoard[lastPlaced.row()][lastPlaced.col()] = null;

        // Revert the flipped discs
        for (Move.FlippedDisc disc : lastmove.flippedDiscs()) {
            Position pos = disc.getPosition();

            // Ensure the position exists before attempting to set the owner
            if (GameBoard[pos.row()][pos.col()] != null) {
                Disc currentDisc = GameBoard[pos.row()][pos.col()];

                // If the disc is a bomb, reset its exploded state
                if (currentDisc.getType().equals("💣")) {
                    currentDisc.SetHasExploded(false);  // Reset the bomb's exploded state
                }

                // Revert the ownership of the flipped discs
                currentDisc.setOwner(disc.getOriginalOwner());
                System.out.println("\t Undo: flipping back " + currentDisc.getType() + " in (" + pos.row() + ", " + pos.col()+")");
            } else {
                System.out.println("Error: Tried to revert a flipped disc that doesn't exist");
            }
        }

        // Switch to the other player
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;

        // Recalculate valid moves after undoing the move
        ValidMoves();
    }



}
