package com.runescape.gameserver.world.content.skills.impl.cooking;

import java.util.ArrayList;
import java.util.List;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class PieMaking extends Action {

	public PieMaking(Mob entity, Pie p) {
		super(entity, 0);
		this.pie = p;
		this.p = (Player)entity;
	}
	
	Pie pie;
	
	Player p;

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.WALKABLE;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public void execute() {
		if (p.getInventory().contains(pie.getIngredOne()) && p.getInventory().contains(pie.getIngredTwo())) {
			p.getInventory().remove(new Item(pie.getIngredOne()));
			p.getInventory().remove(new Item(pie.getIngredTwo()));
			p.getInventory().add(new Item(pie.getItemCreated()));
			this.stop();
		}
		
	}
	
	public enum Pies {
		REDBERRY_PIE(new Pie(1951, 2321)),
		SUMMER_PIE(new Pie(5504, 7212), new Pie(5982, 7212, 7214), new Pie(1955, 7214, 7216)),
		MEAT_PIE(new Pie(2142, 2319)),
		MEAT_PIE_CH(new Pie(2140, 2319)),
		APPLE_PIE(new Pie(1955, 2317)),
		ADMRIAL_PIE(new Pie(329, 7192), new Pie(361, 7192, 7194), new Pie(1942, 7194, 7196)),
		GARDEN_PIE(new Pie(1982, 7172), new Pie(1957, 7172, 7174), new Pie( 1965, 7174, 7176)),
		FISH_PIE(new Pie(333, 7182), new Pie( 339, 7182, 7184), new Pie(1942, 7184, 7186)),
		WILD_PIE(new Pie(2136, 7202), new Pie(2876, 7202, 7204), new Pie(3226, 7204, 7206)),
		MUD_PIE(new Pie(6032, 7164), new Pie(2953, 7164, 7166), new Pie(434, 7166, 7168))
		;
		private List<Pie> pieList = new ArrayList<>();
		Pies(Pie... pies) {
			for (Pie p : pies)
				pieList.add(p);
		}
		public List<Pie> getPieList() {
			return pieList;
		}
		public static Pie forId(int ingrediantOne, int ingrediantTwo) {
			for (Pies p : values()) {
				for (Pie pie : p.getPieList()) {
					if ((pie.getIngredOne() == ingrediantOne && pie.getIngredTwo() == ingrediantTwo)
							|| (pie.getIngredOne() == ingrediantTwo && pie.getIngredTwo() == ingrediantOne)) {
						return pie;
					}
				}
			}
			return null;
		}
	}
}