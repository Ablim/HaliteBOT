import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyBot {
    
	public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;
        int stepCounter = 1;
        int nextStepCount = 1;
        Direction currentDirection = Direction.NORTH;
        
        LinkedList<Direction> directionLoop = new LinkedList<Direction>();
        directionLoop.add(Direction.EAST);
        directionLoop.add(Direction.SOUTH);
        directionLoop.add(Direction.WEST);
        directionLoop.add(Direction.NORTH);

        Networking.sendInit("AblimBOT");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location l = gameMap.getLocation(x, y);
            	
	            	if (l.getSite().owner == myID) {
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
	            				moves.add(new Move(l, currentDirection)); 
	            			}
	            		}
	            	}
            	}
            }

            if (stepCounter > 0) {
            	stepCounter--;
            }
            else {
            	nextStepCount++;
            	stepCounter = nextStepCount;
            	currentDirection = directionLoop.getFirst();
            	directionLoop.removeFirst();
            	directionLoop.addLast(currentDirection);
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
