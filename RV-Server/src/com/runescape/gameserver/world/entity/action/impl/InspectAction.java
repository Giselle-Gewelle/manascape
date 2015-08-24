package com.runescape.gameserver.world.entity.action.impl;

import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public abstract class InspectAction extends Action {
    
    /**
     * The location.
     */
    private Location location;

    /**
     * Constructor.
     * @param player
     * @param location
     */
    public InspectAction(Player player, Location location) {
        super(player, 0);
        this.location = location;
    }

    @Override
    public StackPolicy getStackPolicy() {
        return StackPolicy.NEVER;
    }

    @Override
    public WalkablePolicy getWalkablePolicy() {
        return WalkablePolicy.NON_WALKABLE;
    }
    
    /**
     * Initialization method.
     */
    public abstract void init();
    
    /**
     * Inspection time consumption.
     * @return
     */
    public abstract long getInspectDelay();
    
    
    /**
     * Rewards to give the player.
     * @param player
     * @param node
     */
    public abstract void giveRewards(Player player);

    @Override
    public void execute() {        
        final Mob player = getMob();
        boolean isPlayer = false;
        if(player instanceof Player) {
        	isPlayer = true;
        }
        if(this.getDelay() == 0) {
            this.setDelay(getInspectDelay());
            init();
            if(this.isRunning()) {
                player.face(location);
            }
        } else {
        	if(isPlayer) {
        		giveRewards((Player) player);
        	}
            stop();
        }
    }

}