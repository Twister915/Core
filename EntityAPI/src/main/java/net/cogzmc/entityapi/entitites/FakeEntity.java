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
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/05/2014
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

	public abstract void showTo(Player player);
}
