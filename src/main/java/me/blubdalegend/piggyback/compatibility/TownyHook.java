package me.blubdalegend.piggyback.compatibility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;

import me.blubdalegend.piggyback.Piggyback;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TownyHook
{
	private TownyAPI api;
	private TownyUniverse universe;
	
	public TownyHook(Piggyback main) {
	    api = TownyAPI.getInstance();
	    universe = TownyUniverse.getInstance();
	}
	
	public boolean hasTown(String townName) {
		return universe.hasTown(townName);
	}
	
	public boolean hasNation(String nationName) {
		return universe.hasNation(nationName);
	}
	
	public Town getTown(String townName) {
		if(hasTown(townName)) {
			return universe.getTown(townName);
		}
		return null;
	}
	
	public Nation getNation(String nationName) {
		if(hasNation(nationName)) {
			return universe.getNation(nationName);
		}
		return null;
	}
	
	public boolean hasTownPermission(Player p) {
		return p.hasPermission("piggyback.towny.town");
	}
	
	public boolean hasNationPermission(Player p) {
		return p.hasPermission("piggyback.towny.nation");
	}
	
	public boolean isWilderness(Location loc) {
		//return TownyUniverse.isWilderness(loc.getBlock());
		return isWilderness(loc.getBlock());
	}
	
	public boolean isWilderness(Player p) {
		return isWilderness(p.getLocation().getBlock());
	}
	
	public boolean isWilderness(Block block) {
		return api.isWilderness(block);
	}
	
	public boolean isAlly(Player player, Player otherPlayer){
    	Resident resident = universe.getResident(player.getUniqueId());
    	Resident otherResident = universe.getResident(otherPlayer.getUniqueId());
		if(resident.isAlliedWith(otherResident) || resident.hasFriend(otherResident))
		    return true;
		return false;
	}
    
    public boolean isEnemy(Player player, Player otherPlayer){
    	Resident resident = universe.getResident(player.getUniqueId());
    	Resident otherResident = universe.getResident(otherPlayer.getUniqueId());
		if(resident.hasNation() && otherResident.hasNation()) {
			try {
				for(Nation n : resident.getNation().getEnemies())
				{
					if(otherResident.getNation() == n && !resident.hasFriend(otherResident))
						return true;
				}
			} catch (TownyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
    public boolean isNeutral(Player player, Player otherPlayer){
    	if(!isAlly(player, otherPlayer) && !isEnemy(player, otherPlayer))
    		return true;
    	return false;
    }
    
    public Town getTownFromLocation(Location location) {
        TownBlock townBlock = api.getTownBlock(location);

        if (townBlock != null && townBlock.hasTown()) {
            try {
                return townBlock.getTown();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
        }

        return null; // No town found at this location
    }
    
	public boolean isInHomeTown(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = universe.getResident(p.getUniqueId());
			block = getTownBlock(p.getLocation());
			if (block != null)
				if(block.hasTown()) {
					if(resident.getTown() == block.getTown()) {
						return true;
					}
				}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isInHomeNation(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = universe.getResident(p.getName());
			block = getTownBlock(p.getLocation());
			if (block != null)
				if(block.hasTown()) {
					if(block.getTown().hasNation()) {
						if(resident.getTown().getNation() == block.getTown().getNation()) {
							return true;
						}
					}				
				}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isInOtherTown(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = universe.getResident(p.getUniqueId());
			block = getTownBlock(p.getLocation());
			if (block != null)
				if(block.hasTown()) {
					if(resident.getTown() != block.getTown()) {
						return true;
					}
				}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isInOtherNation(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = universe.getResident(p.getName());
			block = getTownBlock(p.getLocation());
			if (block != null)
				if(block.hasTown()) {
					if(block.getTown().hasNation()) {
						if(resident.getTown().getNation() != block.getTown().getNation()) {
							return true;
						}
					}				
				}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isInEnemyNation(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = universe.getResident(p.getUniqueId());
			block = getTownBlock(p.getLocation());
			if (block != null)
				if(block.hasTown()) {
					if(block.getTown().hasNation() && resident.hasNation()) {
						if(block.getTown().getNation().getEnemies().contains(resident.getNation()))
							return true;
					}				
				}
		} catch (TownyException e) {
		}		
		return false;
	}
	
	public boolean isMayor(String townName, Player p) {
		Town town = getTown(townName);
		if(town!=null) {
			return isMayor(town,p);
		}
		return false;
	}
	
	public boolean isMayor(Town town, Player p) {
		if(town!=null) {
			Resident resident = universe.getResident(p.getUniqueId());
			if(town.getMayor()==resident)
				return true;
		}		
		return false;
	}
	
	public boolean isMayor(Location loc, Player p) {
		TownBlock townBlock;
		try {
			townBlock = getTownBlock(loc);
			Resident resident = universe.getResident(p.getUniqueId());
	        if (townBlock != null)
	        	if(townBlock.hasTown()) {
	        		if(townBlock.getTown().getMayor() == resident)
	                	return true;
	        	}
		} catch (NotRegisteredException e) {
		}
		return false;		
	}
	
	public boolean isMayor(Player p) {
		return isMayor(p.getLocation(), p);
	}
	
	public boolean isPlotOwner(Player p, Location loc) {
		Resident resident;
		TownBlock block;
		resident = universe.getResident(p.getName());
		block = getTownBlock(loc);
		if(block!=null && resident!=null) {
			if(block.hasTown())
					if(block.hasResident())
						if(block.getResidentOrNull()==resident)
							return true;
		}
		return false;
	}
	
	public boolean isPlotOwner(Player p) {
		return isPlotOwner(p, p.getLocation());
	}
	
	private TownBlock getTownBlock(Location loc) {
		TownBlock block = null;
		try {
			block = WorldCoord.parseWorldCoord(loc).getTownBlock();	
		}catch (TownyException e) {
		}
		return block;
	}
	
	public boolean getAllowPiggybackInTown(String town) {
		if(hasTown(town))
		    return getAllowPiggybackInObject(getTown(town));
		return false;
	}
	
	public boolean getAllowPiggybackInTown(Town town) {
		return getAllowPiggybackInObject(town);
	}
	
	public boolean getAllowPiggybackInNation(Nation nation) {
		return getAllowPiggybackInObject(nation);
	}
	
	public boolean getAllowPiggybackInNation(String nation) {
		if(hasNation(nation))
		    return getAllowPiggybackInObject(getNation(nation));
		return false;
	}
	
	public boolean getAllowPiggybackInPlot(Location loc) {
		return getAllowPiggybackInObject(getTownBlock(loc));
	}
	
	public boolean getAllowPiggybackInPlot(TownBlock plot) {
		return getAllowPiggybackInObject(plot);
	}
	
	public boolean setAllowPiggybackInTown(Town town, boolean allow) {
		return setAllowPiggybackInObject(town, allow);
	}
	
	public boolean setAllowPiggybackInTown(String town, boolean allow) {
		return setAllowPiggybackInObject(getTown(town), allow);
	}
	
	public boolean setAllowPiggybackInNation(Nation nation, boolean allow) {
		return setAllowPiggybackInObject(nation, allow);
	}
	
	public boolean setAllowPiggybackInNation(String nation, boolean allow) {
		return setAllowPiggybackInObject(getNation(nation), allow);
	}
	
	public boolean setAllowPiggybackInPlot(Location loc, boolean allow) {
		return setAllowPiggybackInObject(getTownBlock(loc), allow);
	}
	
	public boolean setAllowPiggybackInPlot(TownBlock block, boolean allow) {
		return setAllowPiggybackInObject(block, allow);
	}
	
	/*
	 * Gets whether or not Piggyback is allowed in a plot/town/nation
	*/
	private boolean getAllowPiggybackInObject(TownyObject object){
		if(object.getMetadata("allowPiggyback")!=null) {
			@Nullable CustomDataField<?> field = object.getMetadata("allowPiggyback");
			if(field instanceof BooleanDataField) {
				return ((BooleanDataField)field).getValue();
			}
		}else {
			MetaDataUtil.addNewBooleanMeta(object, "allowPiggyback", false, true);
		}
		return false;
	}
	
	/*
	 *  Sets whether or not to allow piggyback in a plot/town/nation
	 *  Returns whether the value has changed or not 
	*/
	private boolean setAllowPiggybackInObject(TownyObject object, boolean allow) {
		if(getAllowPiggybackInObject(object) == allow) {
			return false;
		}else {
			@Nullable CustomDataField<?> field = object.getMetadata("allowPiggyback");
			if(field instanceof BooleanDataField) {
				MetaDataUtil.setBoolean(object, (BooleanDataField) field, allow, true);
				return true;
			}
			return false;
		}
	}
}