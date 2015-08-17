package com.runescape.gameserver.world.entity.mob.npc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.event.impl.DeathEvent;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.entity.Damage.Hit;
import com.runescape.gameserver.world.entity.Damage.HitType;
import com.runescape.gameserver.world.entity.action.impl.AttackAction;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.npc.drops.DropTable;
import com.runescape.gameserver.world.entity.mob.npc.impl.*;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.region.Region;

public class NPC extends Mob {

	private CacheNPCDefinition definition = null;
	private int health;
	private int transformInto;
	private String forcedChat = "";
	private NPCSpawn npcSpawn;
	private boolean hasAggro = false;
	private Random random = new Random();
	private DropTable dropTable = new DropTable();
	private boolean dynamic = false;
	private String description = "n/a";
	private String[] actions = null;
	private boolean clickable = true;
	private int id = -1;
	private boolean updateAppearance = true;
	private Location defaultWalkTo = null;
	
	public NPC() {
		super();
		setAppearance();
		setEquipment();
		setSkills();
		setDropTable();
	}
	
	public NPC(CacheNPCDefinition definition) {
		super();
		this.definition = definition;
		this.health = getMaxHealth();
		setAppearance();
		setEquipment();
		setSkills();
		setDropTable();
	}
	
	public Location getDefaultWalkTo() {
		return defaultWalkTo;
	}
	public void setDefaultWalkTo(Location defaultWalkTo) {
		this.defaultWalkTo = defaultWalkTo;
	}
	
	public boolean shouldUpdateAppearance() {
		return updateAppearance;
	}
	public void setUpdateAppearance(boolean updateAppearance) {
		this.updateAppearance = updateAppearance;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isClickable() {
		return clickable;
	}
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	public String[] getActions() {
		return actions;
	}
	public void setAction(int index, String action) {
		if(actions == null) {
			actions = new String[5];
		}
		actions[index] = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public DropTable getDropTable() {
		return dropTable;
	}
	
	public void aggroTo(Class<?> npcType) {
		if(this.isDead()) {
			return;
		}
		if(this.isInCombat()) {
			return;
		}
		if(this.hasAggro()) {
			return;
		}
		if(this.getAggressorState()) {
			return;
		}
		
		Region region = World.getInstance().getRegionManager().getRegionByLocation(getLocation());
		Collection<Player> players = region.getPlayers();
		if(players.size() < 1) {
			return;
		}
		
		int index = 0;
		HashMap<Integer, NPC> aggros = new HashMap<Integer, NPC>();
		
		Region[] regions = region.getSurroundingRegions();
		//Collection<NPC> npcs = region.getNpcs();
		for(Region regionCheck : regions) {
			for(NPC npc : regionCheck.getNpcs()) {
				if(npcType.isInstance(npc)) {
					aggros.put(index++, npc);
				}
			}
		}
		/*for(NPC npc : npcs) {
			if(npcType.isInstance(npc)) {
				aggros.put(index++, npc);
			}
		}*/
		if(index < 1) {
			return;
		}
		
		int randId = random.nextInt(index + 1);
		aggroAction = new AttackAction(this, aggros.get(randId));
		getActionQueue().addAction(aggroAction);
	}
	
	public void resetHealth() {
		getSkills().setLevel(Skill.HITPOINTS, getSkills().getRealLevel(Skill.HITPOINTS));
	}
	
	public void kill(Mob killer) {
		removeFromRegion(getRegion());
		getDropTable().dropRandomDrops((killer instanceof Player ? (Player) killer : null), getLocation());
		resetFace();
		reset();
		resetHealth();
		setAppearance();
		setEquipment();
		setSkills();
		setDropTable();
	}
	
	public void validateLife() {
		if(getSkills().getLevel(Skill.HITPOINTS) <= 0) {
			if(!this.isDead()) {
				World.getInstance().submit(new DeathEvent(this, null));
			}
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		hasAggro = false;
	}
	
	public boolean hasAggro() {
		return hasAggro;
	}
	
	public void setHasAggro(boolean hasAggro) {
		this.hasAggro = hasAggro;
	}

	public void setNPCSpawn(NPCSpawn npcSpawn) {
		this.npcSpawn = npcSpawn;
	}

	public NPCSpawn getNPCSpawn() {
		return npcSpawn;
	}

	public CacheNPCDefinition getDefinition() {
		return definition;
	}

	@Override
	public void addToRegion(Region region) {
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		region.addNpc(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return 10;
	}

	public int getTransformInto() {
		return transformInto;
	}

	public void transformNpc(int npcid) {
		this.transformInto = npcid;
		this.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}

	public String getForcedChat() {
		return forcedChat;
	}

	public void requestForcedChat(String text) {
		this.forcedChat = text;
		this.getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
	}

	@Override
	public void inflictDamage(int damage, HitType type) {
		// TODO Auto-generated method stub

	}

	public boolean handleFirstClick(Player player) {
		return false;
	}

	public boolean handleSecondClick(Player player) {
		return false;
	}

	public boolean handleThirdClick(Player player) {
		return false;
	}

	public void inflictDamage(Hit damage, Mob aggressor) {
		if(!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(damage);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if(!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(damage);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		getSkills().detractLevel(Skill.HITPOINTS, damage.getDamage());
		/*health -= damage.getDamage();
		if(health <= 0) {
			if(!this.isDead()) {
				World.getWorld().submit(new DeathEvent(this));
			}
			this.setDead(true);
			return;
		}*/
		if(getSkills().getLevel(Skill.HITPOINTS) <= 0) {
			if(!this.isDead()) {
				World.getInstance().submit(new DeathEvent(this, aggressor));
			}
			return;
		}
		if(!this.isInCombat()) {
			this.setInCombat(true);
			this.setAggressorState(false);
			this.getActionQueue().addAction(new AttackAction(this, aggressor, 1));
		}
	}
	
	protected AttackAction aggroAction = null;
	
	public AttackAction getAggroAction() {
		return aggroAction;
	}
	
	public void setAggroAction(AttackAction aggroAction) {
		this.aggroAction = aggroAction;
	}
	
	public void setAppearance() {
		
	}
	
	public void setDropTable() {
		
	}
	
	public void setSkills() {
		
	}
	
	public void setEquipment() {
		
	}

	public int getAttackAnim() {
		return 422;
	}
	
	public int getDefendAnim() {
		return 424;
	}
	
	public void tick() {
		
	}
	
	public static NPC getNpcInstance(int id) {
		if(id > Constants.MAX_NPCS)
			return null;

		NPC npc = null;
		switch(id) {
			case 24:
				npc = new CommanderAdriea();
				npc.setId(id);
				return npc;
			case 19:
				npc = new TempleKnight();
				npc.setId(id);
				return npc;
			case 178:
				npc = new BlackKnight();
				npc.setId(178);
				return npc;
			case 2244:
				return new LumbridgeGuide(CacheNPCDefinition.forId(id));
			default:
				break;
		}
		
		CacheNPCDefinition def = CacheNPCDefinition.forId(id);
		if(def == null) {
			return new NPC(def);
		}
		if(def.getId() == -1) {
			System.out.println("null npc: " + def.getId());
		}

		if(def.getName().equalsIgnoreCase("man")) {
			return new Man(def);
		}

		if(def.getName().contains("Fishing spot")) {
			return new FishingSpot(def);
		}
		
		return new NPC(def);
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public int getLockIndex() {
		return getIndex() + 1;
	}

	@Override
	public int getSize() {
		return 1;
	}

	public int defence = 0, attack = 0;

	@Override
	public int meleeDef() {
		return defence;
	}

	@Override
	public int meleeAtk() {
		return attack;
	}

	@Override
	public int mageDef() {
		return defence;
	}

	@Override
	public int mageAtk() {
		return attack;
	}

	@Override
	public int rangeAtk() {
		return attack;
	}

	@Override
	public int rangeDef() {
		return defence;
	}

}
