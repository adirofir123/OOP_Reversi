public class UnflippableDisc implements Disc{

    private Player owner;

    public UnflippableDisc(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {

    }

    @Override
    public String getType() {
        return "⭕";
    }

    @Override
    public void SetHasExploded(boolean exploded) {

    }

    @Override
    public boolean GetHasExploded() {
        return false;
    }
}
