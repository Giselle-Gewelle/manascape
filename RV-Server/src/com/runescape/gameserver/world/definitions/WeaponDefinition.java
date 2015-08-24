package com.runescape.gameserver.world.definitions;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.Combat.CombatStyle;

/**
 * The weapon definition manager.
 * @author Aizen Sousuke
 */
public class WeaponDefinition {
	
	/**
	 * Weapon animation indexes.
	 */
	public static final int 
		STAND = 0, 
		WALK = 1, 
		RUN = 2, 
		DEFEND = 3,
		ATTACK_1 = 0,
		ATTACK_2 = 1;
	
	public static WeaponDefinition UNARMED = new WeaponDefinition(
		0, null, 5855, 4, -1, false, new int[] {-1, -1, -1, 422, 422, 424}, new int[] { 422, 422, 424 }, new ItemBonuses(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), 4, new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
	);

	private static final Logger logger = Logger.getLogger(ItemDefinition.class.getName());
	
	private static WeaponDefinition[] definitions;
	
	public static WeaponDefinition forId(int id) {
		if(id >= definitions.length) {
			return null;
		}
		return definitions[id];
	}

	public static enum Predefined {
		
		DAGGER(
			2276, 
			4, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451, 412 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.STAB, AttackType.STAB, AttackType.SLASH, AttackType.STAB}
		),
		SWORD(
			2276, 
			4, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451, 412 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.STAB, AttackType.STAB, AttackType.SLASH, AttackType.STAB}
		),
		LONGSWORD(
			2423, 
			5, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451, 412 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.CONTROLLED, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.SLASH, AttackType.SLASH, AttackType.STAB, AttackType.SLASH}
		),
		SCIMITAR(
			2423, 
			4, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424},
			new int[] { 451, 412 }, 
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.CONTROLLED, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.SLASH, AttackType.SLASH, AttackType.STAB, AttackType.SLASH}
		),
		BATTLEAXE(
			1698, 
			6, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.SLASH, AttackType.SLASH, AttackType.CRUSH, AttackType.SLASH}
		),
		AXE(
			1698, 
			5, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.SLASH, AttackType.SLASH, AttackType.CRUSH, AttackType.SLASH}
		),
		MACE(
			-1, 
			5, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
		),
		WARHAMMER(
			425, 
			6, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
		),
		TWOHANDEDSWORD(
			4705, 
			7, 
			-1, 
			false, 
			new int[] { 2561, 2562, 2563, 410 },
			new int[] { 407, 406, 407, 407 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
		),
		CLAWS(
			-1, 
			-1, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 451 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
		),
		SPEAR(
			-1, 
			-1, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 422 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH}
		),
		HALBERD(
			8460, 
			7, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 440 },
			3, 
			new CombatStyle[] {CombatStyle.CONTROLLED, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.STAB, AttackType.SLASH, AttackType.STAB}
		),
		PICKAXE(
			5570, 
			-1, 
			-1, 
			false, 
			new int[] {-1, -1, -1, 424}, 
			new int[] { 422 },
			4, 
			new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE}, 
			new AttackType[] {AttackType.STAB, AttackType.STAB, AttackType.CRUSH, AttackType.STAB}
		);
		
		private int interfaceId;
		
		private int speed;
		
		private int sound;
		
		private boolean hasSpecial;
		
		private int[] anims;
		
		private int[] attackAnims;
		
		private int options;
		
		private CombatStyle[] combatStyles;
		
		private AttackType[] attackTypes;
		
		/*private Predefined() {
			this(5855, 2400, -1, new int[] {-1, -1, -1, 422, 422, 424});
		}
		
		private Predefined(int interfaceId) {
			this(interfaceId, 3, -1, false, defaultAnims, 4, defaultCombatStyles, defaultsAttackTypes);
		}
		
		private Predefined(int interfaceId, int speed) {
			this(interfaceId, speed, -1, false, new int[] {-1, -1, -1, 422, 422, 424}, 4);
		}
		
		private Predefined(int interfaceId, int speed, int[] anims) {
			this(interfaceId, speed, -1, false, anims, 4);
		}
		
		private Predefined(int interfaceId, int speed, int sound, int[] anims) {
			this(interfaceId, speed, sound, false, anims, 4);
		}*/
		
		private Predefined(int interfaceId, int speed, int sound, boolean hasSpecial, int[] anims, int[] attackAnims, int options, CombatStyle[] combatStyles, AttackType[] attackTypes) {
			this.interfaceId = interfaceId;
			this.speed = speed;
			this.sound = sound;
			this.hasSpecial = hasSpecial;
			this.anims = anims;
			this.attackAnims = attackAnims;
			this.options = options;
			this.combatStyles = combatStyles;
			this.attackTypes = attackTypes;
		}
		
		public int getInterfaceId() {
			return this.interfaceId;
		}
		
		public int getSpeed() {
			return this.speed;
		}
		
		public int getSound() {
			return this.sound;
		}
		
		public boolean hasSpecial() {
			return this.hasSpecial;
		}
		
		public int[] getAnims() {
			return this.anims;
		}
		
		public int[] getAttackAnims() {
			return attackAnims;
		}
		
		public int getOptionCount() {
			return this.options;
		}
		
		public CombatStyle[] getCombatStyles() {
			return this.combatStyles;
		}
		
		public AttackType[] getAttackTypes() {
			return this.attackTypes;
		}
	}
	
	public static void init() throws IOException, ParseException {
		if(definitions != null) {
			throw new IllegalStateException("Weapon definitions already loaded.");
		}
		
		logger.info("Loading weapon definitions...");
		
		logger.info("Loading weapon bonuses...");
		if (ItemBonuses.loadItemBonuses())
			logger.info("Weapon bonuses successfully loaded.");
		
		final JSONParser parser = new JSONParser();
		final JSONArray json = (JSONArray) parser.parse(new FileReader("data/weapons.json"));
		
		definitions = new WeaponDefinition[ItemDefinition.definitionCount()];
		definitions[0] = UNARMED;
		
		int count = 0;
		for(Object o : json) {
			
			JSONObject definition = (JSONObject) o;
			
			int id = ((Long) definition.get("id")).intValue();
			int interfaceId = 5855;
			int speed = 3;
			int sound = -1;
			boolean hasSpecial = false;
			int[] anims = {
				-1, -1, -1, 422, 422, 424
			};
			int[] attackAnims = {
				422, 422, 424
			};
			ItemBonuses bonuses = ItemBonuses.getBonusesForId(id);
			int options = 4;
			CombatStyle[] combatStyles = new CombatStyle[] {CombatStyle.ACCURATE, CombatStyle.AGGRESSIVE, CombatStyle.DEFENSIVE, CombatStyle.DEFENSIVE};
			AttackType[] attackTypes = new AttackType[] {AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH, AttackType.CRUSH};
			Predefined type = null;
			if(definition.containsKey("predefined")) {
				String predefined = (String) definition.get("predefined");
				type = Predefined.valueOf(predefined);
				interfaceId = type.getInterfaceId();
				speed = type.getSpeed();
				sound = type.getSound();
				hasSpecial = type.hasSpecial();
				anims = type.getAnims();
				attackAnims = type.getAttackAnims();
				options = type.getOptionCount();
				combatStyles = type.getCombatStyles();
				attackTypes = type.getAttackTypes();
			} else {
				if(definition.containsKey("interface")) {
					interfaceId = ((Long) definition.get("interface")).intValue();
				}
				if(definition.containsKey("speed")) {
					speed = ((Long) definition.get("speed")).intValue();
				}
				if(definition.containsKey("sound")) {
					sound = ((Long) definition.get("sound")).intValue();
				}
				if(definition.containsKey("special")) {
					hasSpecial = (boolean) definition.get("special");
				}
				if(definition.containsKey("anims")) {
					JSONObject animObj = (JSONObject) definition.get("anims");
					if(animObj.containsKey("stand")) {
						anims[STAND] = ((Long) animObj.get("stand")).intValue();
					}
					if(animObj.containsKey("walk")) {
						anims[WALK] = ((Long) animObj.get("walk")).intValue();
					}
					if(animObj.containsKey("run")) {
						anims[RUN] = ((Long) animObj.get("run")).intValue();
					}
					if(animObj.containsKey("attack1")) {
						anims[ATTACK_1] = ((Long) animObj.get("attack1")).intValue();
						if(animObj.containsKey("attack2")) {
							anims[ATTACK_2] = ((Long) animObj.get("attack2")).intValue();
						} else {
							anims[ATTACK_2] = anims[ATTACK_1];
						}
					}
					if(animObj.containsKey("block")) {
						anims[DEFEND] = ((Long) animObj.get("block")).intValue();
					}
				}
				if(definition.containsKey("attackAnims")) {
					JSONObject attAnimObj = (JSONObject) definition.get("attackAnims");
					attackAnims = new int[attAnimObj.size()];
					if(attAnimObj.containsKey("attack1")) {
						attackAnims[ATTACK_1] = ((Long) attAnimObj.get("attack1")).intValue();
					}
					if(attAnimObj.containsKey("attack2")) {
						attackAnims[ATTACK_2] = ((Long) attAnimObj.get("attack2")).intValue();
					}
				}
				if(definition.containsKey("options")) {
					options = ((Long) definition.get("options")).intValue();
				}
				if(definition.containsKey("combatStyles")) {
					combatStyles = new CombatStyle[options];
					JSONArray combatStyleArray = (JSONArray) definition.get("combatStyles");
					int size = combatStyleArray.size();
					for(int i = 0; i < size; i++) {
						combatStyles[i] = CombatStyle.valueOf((String) combatStyleArray.get(i));
					}
				}
				if(definition.containsKey("attackTypes")) {
					attackTypes = new AttackType[options];
					JSONArray attackTypeArray = (JSONArray) definition.get("attackTypes");
					int size = attackTypeArray.size();
					for(int i = 0; i < size; i++) {
						attackTypes[i] = AttackType.valueOf((String) attackTypeArray.get(i));
					}
				}
			}
			
			definitions[id] = new WeaponDefinition(id, type, interfaceId, speed, sound, hasSpecial, anims, attackAnims, bonuses, options, combatStyles, attackTypes);
			
			count++;
			
		}
		logger.info("Loaded " + count + " weapon definitions.");
	}
	
	private final int id;
	private Predefined type;
	private int interfaceId;
	private int speed;
	private int sound;
	private boolean hasSpecial;
	private int[] anims;
	private int[] attackAnims;
	private ItemBonuses bonuses;
	private int options;
	private CombatStyle[] combatStyles;
	private AttackType[] attackTypes;
	
	private WeaponDefinition(int id, Predefined type, int interfaceId, int speed, int sound, boolean hasSpecial, int[] anims, int[] attackAnims, ItemBonuses bonuses, int options, CombatStyle[] combatStyles, AttackType[] attackTypes) {
		this.id = id;
		this.type = type;
		this.interfaceId = interfaceId;
		this.speed = speed;
		this.sound = sound;
		this.hasSpecial = hasSpecial;
		this.anims = anims;
		this.attackAnims = attackAnims;
		this.bonuses = bonuses;
		this.options = options;
		this.combatStyles = combatStyles;
		this.attackTypes = attackTypes;
	}
	
	public int getId() {
		return id;
	}
	
	public Predefined getType() {
		return type;
	}
	
	public int getInterfaceId() {
		return interfaceId;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getSound() {
		return sound;
	}
	
	public boolean hasSpecial() {
		return hasSpecial;
	}
	
	public int[] getAnims() {
		return anims;
	}
	
	public int[] getAttackAnims() {
		return attackAnims;
	}
	
	public ItemBonuses getBonuses() {
		return bonuses;
	}
	
	public int getOptionCount() {
		return options;
	}
	
	public CombatStyle[] getCombatStyles() {
		return combatStyles;
	}
	
	public AttackType[] getAttackTypes() {
		return attackTypes;
	}

}
