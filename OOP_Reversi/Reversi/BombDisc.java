public class BombDisc implements Disc {

    private Player owner;
    private boolean hasExploded;

    public BombDisc(Player owner) {
        this.hasExploded = false;
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public String getType() {
        return "💣";
    }

    @Override
    public void SetHasExploded(boolean exploded) {
        this.hasExploded = exploded;
    }

    @Override
    public boolean GetHasExploded() {
        return hasExploded;
    }
}
