import java.util.LinkedList;
public class GameMap{

    private final Site[][] contents;
    private final Location[][] locations;
    public final int width, height;

    public GameMap(int width, int height, int[][] productions) {

        this.width = width;
        this.height = height;
        this.contents = new Site[width][height];
        this.locations = new Location[width][height];

        for (int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                final Site site = new Site(productions[x][y]);
                contents[x][y] = site;
                locations[x][y] = new Location(x, y, site);
            }
        }
    }

    public boolean inBounds(Location loc) {
        return loc.x < width && loc.x >= 0 && loc.y < height && loc.y >= 0;
    }

    public double getDistance(Location loc1, Location loc2) {
        int dx = Math.abs(loc1.x - loc2.x);
        int dy = Math.abs(loc1.y - loc2.y);

        if(dx > width / 2.0) dx = width - dx;
        if(dy > height / 2.0) dy = height - dy;

        return dx + dy;
    }

    public double getAngle(Location loc1, Location loc2) {
        int dx = loc1.x - loc2.x;

        // Flip order because 0,0 is top left
        // and want atan2 to look as it would on the unit circle
        int dy = loc2.y - loc1.y;

        if(dx > width - dx) dx -= width;
        if(-dx > width + dx) dx += width;

        if(dy > height - dy) dy -= height;
        if(-dy > height + dy) dy += height;

        return Math.atan2(dy, dx);
    }

    public Location getLocation(Location location, Direction direction) {
    	Location to = null;
    	
        switch (direction) {
            case STILL:
                return location;
            case NORTH:
            	to = locations[location.getX()][(location.getY() == 0 ? height - 1 : location.getY()) -1];
            	to.direction = Direction.NORTH;
                return to;
            case EAST:
            	to = locations[location.getX() == width - 1 ? 0 : location.getX() + 1][location.getY()];
            	to.direction = Direction.EAST;
            	return to;
            case SOUTH:
                to = locations[location.getX()][location.getY() == height - 1 ? 0 : location.getY() + 1];
                to.direction = Direction.SOUTH;
                return to;
            case WEST:
                to = locations[(location.getX() == 0 ? width - 1 : location.getX()) - 1][location.getY()];
                to.direction = Direction.WEST;
                return to;
            default:
                throw new IllegalArgumentException(String.format("Unknown direction %s encountered", direction));
        }
    }

    public Site getSite(Location loc, Direction dir) {
        return getLocation(loc, dir).getSite();
    }

    public Site getSite(Location loc) {
        return loc.getSite();
    }

    public Location getLocation(int x, int y) {
        return locations[x][y];
    }
    
    public Location getLocationWraparound(int x, int y) {
    	if (x < 0) {
    		x = width - 1;
    	}
    	else if (x >= width) {
    		x = 0;
    	}
    	
    	if (y < 0) {
    		y = height - 1;
    	}
    	else if (y >= height) {
    		y = 0;
    	}
    	
    	return locations[x][y];
    }

    public Direction getDirectionFromAToB(Location a, Location b) {
    	int dirNorth = a.y > b.y ? a.y - b.y : a.y + (height - b.y);
    	int dirSouth = a.y < b.y ? b.y - a.y : b.y + (height - a.y);
    	int dirEast = a.x < b.x ? b.x - a.x : b.x + (width - a.x);
    	int dirWest = a.x > b.x ? a.x - b.x : a.x + (width - b.x);
    	int shortest = Math.min(Math.min(dirNorth, dirSouth), Math.min(dirEast, dirWest));
    	
    	if (shortest == dirNorth) {
    		return Direction.NORTH;
    	}
    	else if (shortest == dirSouth) {
    		return Direction.SOUTH;
    	}
    	else if (shortest == dirEast) {
    		return Direction.EAST;
    	}
    	else {
    		return Direction.WEST;
    	}
    }
    
    void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Site site = contents[x][y];
                site.owner = 0;
                site.strength = 0;
            }
        }
    }
    
    public LinkedList<Location> getNeighbors8(Location me) {
		LinkedList<Location> neighbors = new LinkedList<Location>();
		neighbors.add(getLocationWraparound(me.x - 1, me.y - 1));
		neighbors.add(getLocationWraparound(me.x,     me.y - 1));
		neighbors.add(getLocationWraparound(me.x + 1, me.y - 1));
		neighbors.add(getLocationWraparound(me.x - 1, me.y));
		neighbors.add(getLocationWraparound(me.x + 1, me.y));
		neighbors.add(getLocationWraparound(me.x - 1, me.y + 1));
		neighbors.add(getLocationWraparound(me.x,     me.y + 1));
		neighbors.add(getLocationWraparound(me.x + 1, me.y + 1));
		return neighbors;
	}
    
    public LinkedList<Location> getNeighbors4(Location me) {
		LinkedList<Location> neighbors = new LinkedList<Location>();
		neighbors.add(getLocation(me, Direction.NORTH));
		neighbors.add(getLocation(me, Direction.SOUTH));
		neighbors.add(getLocation(me, Direction.EAST));
		neighbors.add(getLocation(me, Direction.WEST));
		return neighbors;
	}
}
