package me.blubdalegend.piggyback.compatibility;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardHook
{
	private StateFlag PIGGYBACK_FLAG;
	
	public WorldGuardHook(){
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			StateFlag flag = new StateFlag("allow-piggyback", true);
			registry.register(flag);
			PIGGYBACK_FLAG = flag;
		} catch (FlagConflictException e) {
			Flag<?> existing = registry.get("allow-piggyback");
			if (existing instanceof StateFlag) {
				PIGGYBACK_FLAG = (StateFlag) existing;
			}
		}
		catch (java.lang.IllegalStateException ex) {
			Flag<?> existing = registry.get("allow-piggyback");
			if (existing instanceof StateFlag) {
				PIGGYBACK_FLAG = (StateFlag) existing;
			}
		}
		
	}

	public boolean canPickup(Player player, Location location) {
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
		if (hasBypass(player, location)) {
			return true;
		}else  {
			return query.testState(loc, getLocalPlayer(player), PIGGYBACK_FLAG);
		}
	}

	// technically the bypass check inst needed but if it doesnt function properly it can be removed with no issues
	public boolean hasBypass(Player player, Location location) {
		World world = location.getWorld();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(getLocalPlayer(player), BukkitAdapter.adapt(world));
	}

	private LocalPlayer getLocalPlayer(Player player) {
		return player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
	}
}