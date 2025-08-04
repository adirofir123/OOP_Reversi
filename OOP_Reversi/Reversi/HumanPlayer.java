public class HumanPlayer extends Player {

    protected Player player;

    @Override
    public boolean isPlayerOne() {
        Player player = new Player(isPlayerOne) {
            @Override
            boolean isHuman() {
                return true;
            }
        };
        return  player.isPlayerOne;
    }


    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }


    @Override
    public int getWins() {
        return super.getWins();
    }

    @Override
    public void addWin() {
        super.addWin();
    }

    @Override
    public int getNumber_of_bombs() {
        return super.getNumber_of_bombs();
    }

    @Override
    public int getNumber_of_unflippedable() {
        return super.getNumber_of_unflippedable();
    }

    @Override
    public void reduce_bomb() {
        super.reduce_bomb();
    }

    @Override
    public void reset_bombs_and_unflippedable() {
        super.reset_bombs_and_unflippedable();
    }

    @Override
    public void reduce_unflippedable() {
        super.reduce_unflippedable();
    }


    @Override
    boolean isHuman() {
        return true;
    }


}

