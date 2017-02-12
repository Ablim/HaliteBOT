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
	            	
	            	LinkedList<Location> neighbors = getNeighbors(me, gameMap);
	            	
	            	Location target = null;
//	            	Location enemy = null;
//	            	Location friendsEnemy = null;
//	            	Location terrain = null;
//	            	Location friendsTerrainTarget = null;
	            	
	            	for (Location l : neighbors) {
	            		if (l.getSite().owner == myID) {
//	            			friendsEnemy = getWeakestEnemyFromFriend(friendsEnemy, l);
//	            			friendsTerrainTarget = getWeakestTerrainTargetFromFriend(friendsTerrainTarget, l);
	            			me.toEnemy = l.toEnemy != Direction.STILL ? l.toEnemy : me.toEnemy;
	            			me.toTerrain = l.toTerrain != Direction.STILL ? l.toTerrain : me.toTerrain;
	            		}
	            		else {
//	            			enemy = getWeakestEnemy(enemy, l);
//	            			terrain = getWeakestTerrain(terrain, l);
	            			target = getMostImportantTarget(target, l);
	            		}
	            	}
	            	
	            	Direction step = Direction.STILL; 
	            	
	            	if (target != null) {
	            		Direction dirToTarget = gameMap.getDirectionFromAToB(me, target);
	            		
	            		if (target.getSite().owner == 0) {
	            			me.toTerrain = dirToTarget;
	            		}
	            		else {
	            			me.toEnemy = dirToTarget;
	            		}
	            		
	            		step = me.getSite().strength > target.getSite().strength ? dirToTarget : Direction.STILL;
	            	}
	            	else if (me.getSite().strength >= strengthLimit) {
	            		if (me.toEnemy != Direction.STILL) {
	            			step = me.toEnemy;
	            		}
	            		else {
	            			step = me.toTerrain;
	            		}
	            	}
	            	
	            	
	            	
	            	
//	            	if (enemy != null) {
//	            		step = me.getSite().strength > enemy.getSite().strength ? gameMap.getDirectionFromAToB(me, enemy) : Direction.STILL;
//	            		me.target = enemy;
//	            	}
//	            	else if (friendsEnemy != null) {
//	            		step = me.getSite().strength > strengthLimit ? gameMap.getDirectionFromAToB(me, friendsEnemy) : Direction.STILL;
//	            		me.target = friendsEnemy;
//	            	}
//	            	else if (terrain != null) {
//	            		step = me.getSite().strength > terrain.getSite().strength ? gameMap.getDirectionFromAToB(me, terrain) : Direction.STILL;
//	            		me.target = terrain;
//	            	}
//	            	else if (friendsTerrainTarget != null) {
//	            		step = me.getSite().strength > strengthLimit ? gameMap.getDirectionFromAToB(me, friendsTerrainTarget) : Direction.STILL;
//	            		me.target = friendsTerrainTarget;
//	            	}
	            	
	            	moves.add(new Move(me, step));
            	}
            }
            
            Networking.sendFrame(moves);
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

	private static Location getWeakestTerrainTargetFromFriend(Location currentTerrainTarget, Location friend) {
		if (friend == null || friend.getSite().owner != myID || friend.target == null || friend.target.getSite().owner != 0) {
			return currentTerrainTarget;
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
			return currentTerrain;
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
			return currentEnemy;
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
			return currentEnemy;
		}
		
		if (currentEnemy == null) {
			return enemy;
		}
		else {
			return currentEnemy.getSite().strength < enemy.getSite().strength ? currentEnemy : enemy;
		}
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
