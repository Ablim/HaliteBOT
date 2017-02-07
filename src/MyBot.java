import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyBot {
    
	public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;
        
        Networking.sendInit("AblimBOT");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location me = gameMap.getLocation(x, y);
            	
	            	if (me.getSite().owner == myID) {
	            		Location neighbor = getWeakestNeighbor(gameMap, me);
	            		
	            		if (neighbor != null) {
	            			me.target = neighbor;
	            			
	            			if (neighbor.getSite().strength < me.getSite().strength) {
	            				//KILL!
	            				moves.add(new Move(me, me.getDirectionTo(neighbor)));
	            			}
	            			else {
	            				//Wait
	            				moves.add(new Move(me, Direction.STILL));
	            			}
	            		}
	            		else {
	            			//Move among friends
	            			me.target = getTargetFromNeighbors(gameMap, me);
	            			
	            			if (me.getSite().strength == 0) {
	            				moves.add(new Move(me, Direction.STILL));
	            			}
	            			else {
	            				if (me.target != null) {
	            					moves.add(new Move(me, me.getDirectionTo(me.target))); 
	            				}
	            				else {
	            					moves.add(new Move(me, Direction.STILL));
	            				}
	            			}
	            		}
	            	}
            	}
            }
            
            Networking.sendFrame(moves);
        }
    }
	
	private static Location getTargetFromNeighbors(GameMap gameMap, Location myLocation) {
		Location target = null;
		LinkedList<Location> neighbors = new LinkedList<Location>();
		neighbors.add(gameMap.getLocation(myLocation, Direction.NORTH));
		neighbors.add(gameMap.getLocation(myLocation, Direction.EAST));
		neighbors.add(gameMap.getLocation(myLocation, Direction.SOUTH));
		neighbors.add(gameMap.getLocation(myLocation, Direction.WEST));
		
		for (Location l : neighbors) {
			if (l.target != null && l.target.getSite().owner != myLocation.getSite().owner) {
				if (target == null) {
					target = l.target;
				}
				else {
					if (gameMap.getDistance(myLocation, l.target) < gameMap.getDistance(myLocation, target)) {
						target = l.target;
					}
				}
			}
		}
		
		return target;
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
				else if (l.getSite().owner != 0) {
					target = l;
					break;
				}
				else if (l.getSite().strength < target.getSite().strength) {
					target = l;
				}
			}
		}
		
		return target;
	}
}
