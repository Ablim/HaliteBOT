public class Location {

    // Public for backward compability
    public final int x, y;
    private final Site site;

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
    
    public Direction getDirectionTo(Location l) {
    	if (l.x < x) {
    		return Direction.WEST;
    	}
    	else if (l.x > x) {
    		return Direction.EAST;
    	}
    	else if (l.y < y) {
    		return Direction.NORTH;
    	}
    	else if (l.y > y) {
    		return Direction.SOUTH;
    	}
    	else {
    		return Direction.STILL;
    	}
    }
    
    public Direction getDirectionFrom(Location l) {
    	int dx = Math.abs(x - l.x);
    	int dy = Math.abs(y - l.y);
    	
    	if (dx > dy) {
    		//East or West
    		if (x > l.x) {
    			return Direction.EAST;
    		}
    		else {
    			return Direction.WEST;
    		}
    	}
    	else {
    		//North or South
    		if (y < l.y) {
    			return Direction.NORTH;
    		}
    		else {
    			return Direction.SOUTH;
    		}
    	}
    }
}
