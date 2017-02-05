import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyBot {
    
	public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;
        Location origin = null;

        Networking.sendInit("AblimBOT");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location l = gameMap.getLocation(x, y);
            	
	            	if (l.getSite().owner == myID) {
	            		if (origin == null) {
	            			origin = l;
	            		}
	            		
	            		Location neighbor = getWeakestNeighbor(gameMap, l);
	            		
	            		if (neighbor != null) {
	            			if (neighbor.getSite().strength < l.getSite().strength) {
	            				//KILL!
	            				moves.add(new Move(l, l.getDirectionTo(neighbor)));
	            			}
	            			else {
	            				//Wait
	            				moves.add(new Move(l, Direction.STILL));
	            			}
	            		}
	            		else {
	            			//Move among friends
	            			if (l.getSite().strength == 0) {
	            				moves.add(new Move(l, Direction.STILL));
	            			}
	            			else {
	            				moves.add(new Move(l, Direction.randomDirection())); 
	            			}
	            		}
	            	}
            	}
            }

            Networking.sendFrame(moves);
        }
    }
	
	private static Location getWeakestNeighbor(GameMap map, Location myLocation) {
		Location target = null;
		LinkedList<Location> possibleTargets = new LinkedList<Location>();
		possibleTargets.add(map.getLocation(myLocation, Direction.NORTH));
		possibleTargets.add(map.getLocation(myLocation, Direction.EAST));
		possibleTargets.add(map.getLocation(myLocation, Direction.SOUTH));
		possibleTargets.add(map.getLocation(myLocation, Direction.WEST));
		
		for (Location l : possibleTargets) {
			if (l.getSite().owner != myLocation.getSite().owner) {
				if (target == null) {
					target = l;
				}
				else if (l.getSite().strength < target.getSite().strength) {
					target = l;
				}
			}
		}
		
		return target;
	}
}
