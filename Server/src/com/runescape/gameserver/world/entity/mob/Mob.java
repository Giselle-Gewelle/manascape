package com.runescape.gameserver.world.entity.mob;

import java.util.LinkedList;
import java.util.List;

import com.runescape.gameserver.util.NameUtils;
import com.runescape.gameserver.world.Graphic;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.Sprites;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.Combat.CombatStyle;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.content.faction.*;
import com.runescape.gameserver.world.content.skills.Skills;
import com.runescape.gameserver.world.content.skills.impl.Prayer;
import com.runescape.gameserver.world.definitions.WeaponDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.Damage;
import com.runescape.gameserver.world.entity.WalkingQueue;
import com.runescape.gameserver.world.entity.action.ActionQueue;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.region.Region;

public abstract class Mob {
	
	//public static final Location DEFAULT_LOCATION = Location.create(3222, 3222, 0);
	public static final Location DEFAULT_LOCATION = Location.create(3167, 3231, 0);
	
	private int index;
	
	private Location location;
	private final ActionQueue actionQueue = new ActionQueue(this);
	public WeaponDefinition equippedWeapon = WeaponDefinition.forId(0);
	private transient Damage damage = new Damage();
	private boolean isDead;
	private boolean isInCombat = false;
	private Location teleportTarget = null;
	private final UpdateFlags updateFlags = new UpdateFlags();
	private final MobCooldowns cooldowns = new MobCooldowns(this);
	private final List<Player> localPlayers = new LinkedList<Player>();
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	private boolean teleporting = false;
	private final WalkingQueue walkingQueue = new WalkingQueue(this);
	private final Sprites sprites = new Sprites();
	private Location lastKnownRegion = this.getLocation();
	private final Skills skills = new Skills(this);
	private final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);
	private boolean mapRegionChanging = false;
	private Animation currentAnimation;
	private Graphic currentGraphic;
	private Region currentRegion;
	private Mob interactingEntity;
	private Location face;
	private boolean isAggressor;
	private boolean destroyed = false;
	private boolean visible = true;
	private CombatStyle combatStyle = CombatStyle.ACCURATE;
	private AttackType attackType = AttackType.CRUSH;
	private Spells currentSpell = null;
	private String name;
	private long nameLong;
	private final Appearance appearance = new Appearance();
	private Factions factions = new Factions(this);
	
	private Prayer prayer = new Prayer(this);
	
	public Prayer getPrayer() {
		return prayer;
	}
	
	public Factions getFactions() {
		return factions;
	}
	
	public Appearance getAppearance() {
		return appearance;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		nameLong = NameUtils.nameToLong(this.name);
	}
	public long getNameAsLong() {
		return nameLong;
	}
	
	public ActionQueue getActionQueue() {
		return actionQueue;
	}
	
	public WeaponDefinition getEquippedWeapon() {
		return this.equippedWeapon;
	}
	
	public void setEquippedWeapon(WeaponDefinition equippedWeapon) {
		this.equippedWeapon = equippedWeapon;
	}
	
	public Skills getSkills() {
		return skills;
	}
	
	public Container getEquipment() {
		return equipment;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setCombatStyle(CombatStyle combatStyle) {
		this.combatStyle = combatStyle;
	}
	
	public CombatStyle getCombatStyle() {
		return combatStyle;
	}
	
	public void setAttackType(AttackType attackType) {
		this.attackType = attackType;
	}
	
	public AttackType getAttackType() {
		return attackType;
	}
	
	public Mob() {
		setLocation(DEFAULT_LOCATION);
		this.lastKnownRegion = location;
	}
	
	public void setInCombat(boolean isInCombat) {
		this.isInCombat = isInCombat;
	}

	public boolean isInCombat() {
		return isInCombat;
	}
	
	public boolean getAggressorState() {
		return isAggressor;
	}
	
	public void setAggressorState(boolean b) {
		isAggressor = b;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public boolean isDead() {
		return isDead;
	}

	public void face(Location location) {
		this.face = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	public boolean isFacing() {
		return face != null;
	}
	
	public void resetFace() {
		this.face = null;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	public Location getFaceLocation() {
		return face;
	}
	
	public boolean isInteracting() {
		return interactingEntity != null;
	}
	
	public void setInteractingEntity(Mob entity) {
		this.interactingEntity = entity;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	public void resetInteractingEntity() {
		this.interactingEntity = null;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	public Mob getInteractingEntity() {
		return interactingEntity;
	}
	
	public Region getRegion() {
		return currentRegion;
	}
	
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}
	
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
		this.isInCombat = false;
		this.isAggressor = false;
	}
	
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
	}
	
	public void playGraphics(Graphic graphic) {
		this.currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}
	
	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}
	
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}
	
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}
	
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}
	
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}
	
	public Location getTeleportTarget() {
		return teleportTarget;
	}
	
	public void setTeleportTarget(Location teleportTarget) {
		this.teleportTarget = teleportTarget;
	}
	
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}
	
	public Sprites getSprites() {
		return sprites;
	}
	
	public boolean isTeleporting() {
		return teleporting;
	}
	
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}
	
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}
	
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setLocation(Location location) {
		this.location = location;
		
		Region newRegion = World.getInstance().getRegionManager().getRegionByLocation(location);
		if(newRegion != currentRegion) {
			if(currentRegion != null) {
				removeFromRegion(currentRegion);
			}
			currentRegion = newRegion;
			addToRegion(currentRegion);
		}
	}
	
	public Location getLocation() {
		return location;
	}

	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}
	
	public MobCooldowns getMobCooldowns() {
		return cooldowns;
	}

	public Damage getDamage() {	
		return damage;
	}
	
	public Spells getCurrentSpell() {
		return currentSpell;
	}
	
	public void setCurrentSpell(Spells s) {
		this.currentSpell = s;
	}
	
	public abstract int getSize();
	
	public List<Location> getInternalTiles() {
		List<Location> tiles = new LinkedList<Location>();
		if (getSize() == 1) {
			tiles.add(getLocation());
			return tiles;
		}
		for (int x = getLocation().getX(); x < getLocation()
				.getX() + getSize(); x++) {
			tiles.add(Location.create(x, getLocation().getY(), getLocation().getHeight()));
			tiles.add(Location.create(x, getLocation().getY() + (getSize() - 1), getLocation().getHeight()));
		}
		for (int y = getLocation().getY(); y < getLocation()
				.getY() + getSize(); y++) {
			tiles.add(Location.create(getLocation().getX(), y,getLocation().getHeight()));
			tiles.add(Location.create(
					getLocation().getX() + (getSize() - 1), y, getLocation().getHeight()));
		}
		return tiles;
	}
	
	public void destroy() {
		getActionQueue().cancelQueuedActions();
		getWalkingQueue().reset();
		removeFromRegion(currentRegion);
		destroyed = true;
	}
	
	public abstract void inflictDamage(int damage, Damage.HitType type);
	
	public abstract void removeFromRegion(Region region);
	
	public abstract void addToRegion(Region region);

	public abstract int getClientIndex();
	
	public abstract boolean isPlayer();

	public abstract int getLockIndex();
	
	public abstract int meleeDef();

	public abstract int meleeAtk();

	public abstract int mageDef();

	public abstract int mageAtk();

	public abstract int rangeAtk();

	public abstract int rangeDef();

}
