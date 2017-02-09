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
        
        Networking.sendInit("AblimBOT");

        while (true) {
        	Networking.updateFrame(gameMap);
            List<Move> moves = new ArrayList<Move>();
            
            for (int x = 0; x < gameMap.width; x++) {
            	for (int y = 0; y < gameMap.height; y++) {
            		Location me = gameMap.getLocation(x, y);
            	
	            	if (me.getSite().owner != myID) {
	            		continue;
	            	}
	            	
            		/*
            		 * Look at neighbors
            		 * 
            		 * if enemy {
            		 * 		my.strength > enemy.strength -> KILL
            		 * 		my.strength <= enemy.strength -> STILL
            		 * }
            		 * else if any friend.target is enemy {
            		 * 		my.strength >= 50 -> move to target
            		 * 		my.strength < 50 -> STILL
            		 * }
            		 * else if terrain {
            		 * 		my.strength > terrain.strength -> KILL
            		 * 		my.strength <= terrain.strength -> STILL
            		 * }
            		 * else {
            		 * 		my.strength >= 50 -> move to any friend.target
            		 * 		my.strength < 50 -> STILL
            		 * }
            		 */
	            	
	            	LinkedList<Location> neighbors = new LinkedList<Location>();
	            	neighbors.add(gameMap.getLocation(me, Direction.NORTH));
	            	neighbors.add(gameMap.getLocation(me, Direction.EAST));
	            	neighbors.add(gameMap.getLocation(me, Direction.SOUTH));
	            	neighbors.add(gameMap.getLocation(me, Direction.WEST));
	            	
	            	Location enemy = null;
	            	Location friendsEnemy = null;
	            	Location terrain = null;
	            	Location friendsTerrainTarget = null;
	            	
	            	for (Location l : neighbors) {
	            		enemy = getWeakestEnemy(enemy, l);
	            		friendsEnemy = getWeakestEnemyFromFriend(friendsEnemy, l);
	            		terrain = getWeakestTerrain(terrain, l);
	            		friendsTerrainTarget = getWeakestTerrainTargetFromFriend(friendsTerrainTarget, l);
	            	}
	            	
	            	Direction step = Direction.STILL; 
	            	
	            	if (enemy != null) {
	            		step = me.getSite().strength > enemy.getSite().strength ? me.getDirectionTo(enemy) : Direction.STILL;
	            		me.target = enemy;
	            	}
	            	else if (friendsEnemy != null) {
	            		step = me.getSite().strength > strengthLimit ? me.getDirectionTo(friendsEnemy) : Direction.STILL;
	            		me.target = friendsEnemy;
	            	}
	            	else if (terrain != null) {
	            		step = me.getSite().strength > terrain.getSite().strength ? me.getDirectionTo(terrain) : Direction.STILL;
	            		me.target = terrain;
	            	}
	            	else if (friendsTerrainTarget != null) {
	            		step = me.getSite().strength > strengthLimit ? me.getDirectionTo(friendsTerrainTarget) : Direction.STILL;
	            		me.target = friendsTerrainTarget;
	            	}
	            	
	            	moves.add(new Move(me, step));
            	}
            }
            
            Networking.sendFrame(moves);
        }
    }
	
	private static Location getWeakestTerrainTargetFromFriend(Location currentTerrainTarget, Location friend) {
		if (friend == null || friend.getSite().owner != myID || friend.target == null || friend.target.getSite().owner != 0) {
			return null;
		}
		
		if (currentTerrainTarget == null) {
			return friend.target;
		}
		else {
			return currentTerrainTarget.getSite().strength < friend.target.getSite().strength ? currentTerrainTarget : friend.target;
		}
	}

	private static Location getWeakestTerrain(Location currentTerrain, Location terrain) {
		if (terrain == null || terrain.getSite().owner != 0) {
			return null;
		}
		
		if (currentTerrain == null) {
			return terrain;
		}
		else {
			return currentTerrain.getSite().strength < terrain.getSite().strength ? currentTerrain : terrain;
		}
	}

	private static Location getWeakestEnemyFromFriend(Location currentEnemy, Location friend) {
		if (friend == null || friend.getSite().owner != myID || friend.target == null || friend.target.getSite().owner == 0 || friend.target.getSite().owner == myID) {
			return null;
		}
		
		if (currentEnemy == null) {
			return friend.target;
		}
		else {
			return currentEnemy.getSite().strength < friend.target.getSite().strength ? currentEnemy : friend.target;
		}
	}

	private static Location getWeakestEnemy(Location currentEnemy, Location enemy) {
		if (enemy == null || enemy.getSite().owner == 0 || enemy.getSite().owner == myID) {
			return null;
		}
		
		if (currentEnemy == null) {
			return enemy;
		}
		else {
			return currentEnemy.getSite().strength < enemy.getSite().strength ? currentEnemy : enemy;
		}
	}
}
