package me.blubdalegend.piggyback.compatibility;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DenyPiggybackFlag extends BooleanFlag<DenyPiggybackFlag> {

    public static final DenyPiggybackFlag DENY_PIGGYBACK_TRUE = new DenyPiggybackFlag(true);
    public static final DenyPiggybackFlag DENY_PIGGYBACK_FALSE = new DenyPiggybackFlag(false);

    public DenyPiggybackFlag(final boolean value) {
        super(value, TranslatableCaption.of("flags.flag_description_deny_piggyback"));
    }

    @Override
    protected DenyPiggybackFlag flagOf(final @NonNull Boolean value) {
        return value ? DENY_PIGGYBACK_TRUE : DENY_PIGGYBACK_FALSE;
    }
    
    private PlotArea getPlot(Location location) {
    	PlotAPI api = new PlotAPI();
    	com.plotsquared.core.location.Location loc = com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), 
    			location.getBlockY(), location.getBlockZ());
		return api.getPlotSquared().getPlotAreaManager().getPlotArea(loc);
	}
    
    public boolean canPiggyback(Location loc) {
    	return !getPlot(loc).getFlag(DenyPiggybackFlag.class);
    }
    

}

