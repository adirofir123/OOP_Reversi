import java.util.List;

public class Move {

    private Position position; // The position where the new disc was placed
    private Disc disc;         // The disc that was placed
    private List<FlippedDisc> flippedDiscs; // The discs that were flipped during the move

    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
    }

    public Move(Position position, Disc disc, List<FlippedDisc> flippedDiscs) {
        this.position = position;
        this.disc = disc;
        this.flippedDiscs = flippedDiscs;
    }


    public Position position() {
        return position;
    }


    public Disc disc() {
        return disc;
    }


    public List<FlippedDisc> flippedDiscs() {
        return flippedDiscs;
    }


    public static class FlippedDisc {
        private Position position; // The position of the flipped disc
        private Player originalOwner; // The original owner of the flipped disc

        public FlippedDisc(Position position, Player originalOwner) {
            this.position = position;
            this.originalOwner = originalOwner;
        }

        public Position getPosition() {
            return position;
        }

        public Player getOriginalOwner() {
            return originalOwner;
        }
    }
}