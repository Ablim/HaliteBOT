import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyBot {
    
	private static int myID = 0;
	
	public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;
        final int strengthLimit = 50;
        
        Networking.sendInit("AblimBOT 1.1");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location me = gameMap.getLocation(x, y);
            	
	            	if (me.getSite().owner != myID) {
	            		continue;
	            	}
	            	
	            	LinkedList<Location> neighbors = getNeighbors(me, gameMap);
	            	Location target = null;
	            	Location closestTarget = null;
	            	
	            	for (Location l : neighbors) {
	            		if (l.getSite().owner == myID) {
	            			closestTarget = getClosestTarget(me, gameMap, closestTarget, l.target);
	            		}
	            		else {
	            			target = getMostImportantTarget(target, l);
	            		}
	            	}
	            	
	            	if (target != null) {
	            		me.target = target;
	            		Direction dirToTarget = gameMap.getDirectionFromAToB(me, target);
            			Direction step = me.getSite().strength > target.getSite().strength ? dirToTarget : Direction.STILL;
            			moves.add(new Move(me, step));
	            	}
	            	else if (closestTarget != null) {
	            		me.target = closestTarget;
	            		Direction dirToTarget = gameMap.getDirectionFromAToB(me, closestTarget);
	            		Direction step = me.getSite().strength >= strengthLimit ? dirToTarget : Direction.STILL;
	            		moves.add(new Move(me, step));
	            	}
	            	else {
	            		moves.add(new Move(me, Direction.STILL));
	            	}
            	}
            }
            
            Networking.sendFrame(moves);
        }
    }
	
	private static Location getClosestTarget(Location me, GameMap gameMap, Location currentTarget, Location newTarget) {
		if (me == null || newTarget == null || newTarget.getSite().owner == myID) {
			return currentTarget;
		}
		else if (currentTarget == null) {
			return newTarget;
		}
		else if (gameMap.getDistance(me, currentTarget) < gameMap.getDistance(me, newTarget)) {
			return currentTarget;
		}
		else {
			return newTarget;
		}
	}

	private static LinkedList<Location> getNeighbors(Location me, GameMap gameMap) {
		LinkedList<Location> neighbors = new LinkedList<Location>();
		neighbors.add(gameMap.getLocationWraparound(me.x - 1, me.y - 1));
		neighbors.add(gameMap.getLocationWraparound(me.x,     me.y - 1));
		neighbors.add(gameMap.getLocationWraparound(me.x + 1, me.y - 1));
		neighbors.add(gameMap.getLocationWraparound(me.x - 1, me.y));
		neighbors.add(gameMap.getLocationWraparound(me.x + 1, me.y));
		neighbors.add(gameMap.getLocationWraparound(me.x - 1, me.y + 1));
		neighbors.add(gameMap.getLocationWraparound(me.x,     me.y + 1));
		neighbors.add(gameMap.getLocationWraparound(me.x + 1, me.y + 1));
		return neighbors;
	}

	private static Location getMostImportantTarget(Location currentTarget, Location newTarget) {
		if (newTarget == null || newTarget.getSite().owner == myID) {
			return currentTarget;
		}
		else if (currentTarget == null) {
			return newTarget;
		}
		else if ((currentTarget.getSite().owner == 0 && newTarget.getSite().owner == 0) || (currentTarget.getSite().owner != 0 && newTarget.getSite().owner != 0)) {
			return currentTarget.getSite().strength < newTarget.getSite().strength ? currentTarget : newTarget;
		}
		else if (currentTarget.getSite().owner != 0) {
			return currentTarget;
		}
		else {
			return newTarget;
		}
	}
}
