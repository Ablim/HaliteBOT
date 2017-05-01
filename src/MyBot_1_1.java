import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyBot_1_1 {
    
	private static int myID = 0;
	private static int strengthLimit = 20;
	private static InitPackage iPackage = null;
	private static GameMap gameMap = null;
	
	public static void main(String[] args) throws java.io.IOException {

        iPackage = Networking.getInit();
        myID = iPackage.myID;
        gameMap = iPackage.map;
        
        Networking.sendInit("AblimBot 1.1");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location me = gameMap.getLocation(x, y);
            	
	            	if (me.site.owner != myID) {
	            		continue;
	            	}
	            	
	            	LinkedList<Location> neighbors = gameMap.getNeighbors4(me);
	            	Location weakestTarget = null;
	            	Location friendWithClosestTarget = null;
	            	
	            	for (Location l : neighbors) {
	            		if (l.site.owner == myID) {
	            			friendWithClosestTarget = getFriendWithClosestTarget(friendWithClosestTarget, l);
	            		}
	            		else {
	            			weakestTarget = getWeakestTarget(weakestTarget, l);
	            		}
	            	}
	            	
	            	if (weakestTarget != null) {
	            		me.stepsToTarget = 1;
	            		Direction step = me.site.strength > weakestTarget.site.strength ? weakestTarget.direction : Direction.STILL;
	            		moves.add(new Move(me, step));
	            	}
	            	else if (friendWithClosestTarget != null) {
	            		me.stepsToTarget = friendWithClosestTarget.stepsToTarget + 1;
	            		Direction step = me.site.strength >= strengthLimit ? friendWithClosestTarget.direction : Direction.STILL;
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
	
	private static Location getFriendWithClosestTarget(Location currentFriend, Location newFriend) {
		if (newFriend == null || newFriend.site.owner != myID) {
			return currentFriend;
		}
		else if (currentFriend == null || currentFriend.site.owner != myID) {
			return newFriend;
		}
		else {
			return currentFriend.stepsToTarget < newFriend.stepsToTarget ? currentFriend : newFriend;
		}
	}

	private static Location getWeakestTarget(Location currentTarget, Location newTarget) {
		if (newTarget == null || newTarget.site.owner == myID) {
			return currentTarget;
		}
		else if (currentTarget == null || currentTarget.site.owner == myID) {
			return newTarget;
		}
		else {
			return currentTarget.site.strength < newTarget.site.strength ? currentTarget : newTarget;
		}
	}
}
