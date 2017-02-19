import java.util.ArrayList;
import java.util.List;

public class DirectionBot {
    
	private static int myID = 0;
	
	public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;
        
        Networking.sendInit("DirectionBot");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location me = gameMap.getLocation(x, y);
            	
	            	if (me.site.owner != myID) {
	            		continue;
	            	}
	            	
	            	if (me.site.strength > 0) {
	            		if (gameMap.getLocation(me, Direction.NORTH).site.owner != myID) {
	            			moves.add(new Move(me, Direction.NORTH));
	            		}
	            		else if (gameMap.getLocation(me, Direction.SOUTH).site.owner != myID) {
	            			moves.add(new Move(me, Direction.SOUTH));
	            		}
	            		else if (gameMap.getLocation(me, Direction.EAST).site.owner != myID) {
	            			moves.add(new Move(me, Direction.EAST));
	            		}
	            		else if (gameMap.getLocation(me, Direction.WEST).site.owner != myID) {
	            			moves.add(new Move(me, Direction.WEST));
	            		}
	            		else {
	            			moves.add(new Move(me, Direction.STILL));
	            		}
	            	}
	            	else {
            			moves.add(new Move(me, Direction.STILL));
            		}
            	}
            }
            
            Networking.sendFrame(moves);
        }
    }
}
