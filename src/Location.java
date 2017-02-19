public class Location {

    // Public for backward compability
    public final int x, y;
    public final Site site;
    public Location target = null;
    public Direction direction = Direction.STILL;
    public int stepsToTarget = Integer.MAX_VALUE;
    
    public Location(int x, int y, Site site) {
        this.x = x;
        this.y = y;
        this.site = site;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Site getSite() {
        return site;
    }
}
