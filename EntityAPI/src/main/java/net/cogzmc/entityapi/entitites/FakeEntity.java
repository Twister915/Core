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
import lombok.NonNull;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.entityapi.EntityAPI;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 *
 *
 *
 * <p>
 * Latest Change:
 * <p>
 * @author George
 * @since 26/05/2014
 *
 */
@Data
public abstract class FakeEntity {

	// TODO
	private static final Boolean debug = false;

	private  Integer defaultMaxRangeForPathing = 60;

	private Location location;
	private UUID uuid;
	private Integer entityID;
	private Vector velocity;

	private static EntityType entityType;

	private Boolean onGround;

	private volatile LinkedHashSet<CPlayer> observers = new LinkedHashSet<>();

	private volatile LinkedHashSet<CPlayer> possibleObservers = new LinkedHashSet<>();

	private volatile LinkedHashSet<CPlayer> nearPossibleObservers = new LinkedHashSet<>();

	public FakeEntity(Location location) {
		this.location = location;
		init();
	}

	public void init() {
		
	}

	public void pathTo(final Location to, Integer maxRangeForPathing){

		Location start = getLocation();
		try {
			//create our pathfinder
			AStar path = new AStar(start, to, maxRangeForPathing);
			//get the list of nodes to walk to as a Tile object
			ArrayList<Tile> route = path.iterate();
			//get the result of the path trace
			PathingResult result = path.getPathingResult();

			switch(result){
				case SUCCESS :
					//Path was successfull. Do something here.
					runPath(start, route);
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

	private void runPath(final Location start, final ArrayList<Tile> tiles){
		new BukkitRunnable() {

			Integer i = 0;

			@Override
			public void run() {
				if(tiles.size()-1 >= i) return;
				teleportTo(tiles.get(i).getLocation(start));
				i++;
			}

		}.runTaskTimer(EntityAPI.getInstance(), 0, 1);
	}

	private void teleportTo(Location location) {
		this.setLocation(location);
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

	public final void addObserver(@NonNull CPlayer observer) {
		showTo(observer);
		observers.add(observer);
	}

	public final void addPossibleObserver(@NonNull CPlayer observer) {
		possibleObservers.add(observer);
	}

	public final void addNearPossibleObserver(@NonNull CPlayer observer) {
		nearPossibleObservers.add(observer);
	}

	public final void removeObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			removeObserver(observer);
		}
	}

	public final void removePossibleObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			removePossibleObserver(observer);
		}
	}

	public final void removeNearPossibleObservers(@NonNull List<CPlayer> observers) {
		for(CPlayer observer : observers) {
			removeNearPossibleObserver(observer);
		}
	}

	public final void removeObserver(@NonNull CPlayer observer) {
		removeFor(observer);
		observers.remove(observer);
	}

	public final void removePossibleObserver(@NonNull CPlayer observer){
		possibleObservers.remove(observer);
	}

	public final void removeNearPossibleObserver(@NonNull CPlayer observer) {
		nearPossibleObservers.remove(observer);
	}

	public final boolean isObserver(@NonNull CPlayer observer) {
		return observers.contains(observer);
	}

	public final boolean isPossibleObserver(@NonNull CPlayer observer) {
		return possibleObservers.contains(observer);
	}

	public final boolean isNearPossibleObserver(@NonNull CPlayer observer) {
		return nearPossibleObservers.contains(observer);
	}

	public final boolean isAnyObserver(CPlayer player) {
		return isObserver(player) ||
				isPossibleObserver(player) ||
				isNearPossibleObserver(player);
	}

	protected abstract void showTo(CPlayer observer);

	protected abstract void removeFor(CPlayer observer);
}
