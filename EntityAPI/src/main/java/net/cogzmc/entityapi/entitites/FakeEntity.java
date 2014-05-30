/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.entityapi.entitites;

import com.adamki11s.pathing.AStar;
import com.adamki11s.pathing.AStar.InvalidPathException;
import com.adamki11s.pathing.PathingResult;
import com.adamki11s.pathing.Tile;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * <p>
 * <h2>Observers:</h2>
 * There are 3 types of observers:
 * <table border="1" cellpadding="4">
 *     <tbody>
 *         <tr>
 *             <th>Observer Type</th>
 *             <th>What Do They Do?</th>
 *         </tr>
 *         <tr>
 *             <td>Observer</td>
 *             <td>An observer is someone who can see the entity. Someone who is able to see the entity and can currently see the entity</td>
 *         </tr>
 *         <tr>
 *             <td>Possible Observer</td>
 *             <td>An observer who could see the entity if he was let, though has not been let yet. He is in the render distance of the entity.</td>
 *         </tr>
 *         <tr>
 *             <td>Near Possible Observer</td>
 *             <td>An observer that is near the render distance to becoming a possible observer. This only exists as a buffer area to make code more efficient. Please refer to the image below.</td>
 *         </tr>
 *     </tbody>
 * </table>
 * <img src="http://i.imgur.com/gDdyhkq.png" width="350" height="250"/>
 * <p>
 * Latest Change:
 * <p>
 * @author George
 * @since 26/05/2014
 *
 */
@Log
@Data
@RequiredArgsConstructor
public abstract class FakeEntity {

	// TODO
	private static final Boolean debug = false;

	private Location location;
	private Vector velocity;

	private static EntityType entityType;

	@Getter
	private Boolean onGround;

	public static void spawn() {
		
	}


	public final FakeEntity getHandle() {
		return this;
	}

	public void runPathing(final Location start, final Location end, final int range){
		try {
			//create our pathfinder
			AStar path = new AStar(start, end, range);
			//get the list of nodes to walk to as a Tile object
			ArrayList<Tile> route = path.iterate();
			//get the result of the path trace
			PathingResult result = path.getPathingResult();

			switch(result){
				case SUCCESS :
					//Path was successfull. Do something here.
					changePathBlocksToDiamond(start, route);
					break;
				case NO_PATH :
					//No path found, throw error.
					System.out.println("No path found!");
					break;
			}
		} catch (InvalidPathException e) {
			//InvalidPathException will be thrown if start or end block is air
			if(e.isStartNotSolid()){
				System.out.println("End block is not walkable");
			}
			if(e.isStartNotSolid()){
				System.out.println("Start block is not walkable");
			}
		}
	}

	private void changePathBlocksToDiamond(Location start, ArrayList<Tile> tiles){
		for(Tile t : tiles){
			t.getLocation(start).getBlock().setType(Material.DIAMOND_BLOCK);
		}
	}

	public final void addObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			addObserver(observer);
		}
	}

	public final void addPossibleObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			addPossibleObserver(observer);
		}
	}

	public final void addNearPossibleObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			addNearPossibleObserver(observer);
		}
	}

	public abstract void addObserver(@NonNull CPlayer observer);

	public abstract List<CPlayer> getObservers();

	public abstract void addPossibleObserver(@NonNull CPlayer observer);

	public abstract List<CPlayer> getPossibleObservers();

	public abstract void addNearPossibleObserver(@NonNull CPlayer observer);

	public abstract List<CPlayer> getNearPossibleObservers();
}
