package com.runescape.gameserver.world.content.combat.magic.spells;

import com.runescape.gameserver.world.entity.mob.player.Player.SpellBook;

/**
 * @author Harlan
 *
 */
public enum Spells {
	/**
	int id, int req, int anim, int startGfx, int projId, int endGfx,
	int maxHit, double xpGained, RuneRequirement r, SpellBook book,
	int autoCastId, SpellUseEvent effect, int itemRequirement
	*/
	AIR_STRIKE(1152, 1, 711, 90, 91, 
			92, 2, 5.5, new RuneRequirement(556,1, 558, 1),
			SpellBook.MODERN, 4128, -1),//done
	WATER_STRIKE(1154, 5, 711, 93, 94,
			95, 4, 7.5 ,new RuneRequirement(555, 1, 556, 1, 558, 1),
			SpellBook.MODERN, 4130, -1),//done
	
//	TELE_OTHER_FALADOR(1001, 82, 1978, 366, -1, 367, 0, )
	
	EARTH_STRIKE(1156, 9, 711, 96, 97, 98, 6, 9, new RuneRequirement(557, 2, 556, 1, 558, 1), SpellBook.MODERN, 4132, -1),//done
    FIRE_STRIKE(1158,13, 711, 99, 100, 101, 8, 11, new RuneRequirement(554, 3, 556,2, 558, 1), SpellBook.MODERN, 4134, -1),//done
    
	WIND_BOLT(10, 17, 711, 117, 118, 119, 9, 13, new RuneRequirement(556, 2, 562, 1), SpellBook.MODERN, 4136, -1),
	WATER_BOLT(14, 23, 711, 120, 121,
			122, 10, 16, new RuneRequirement(556, 2, 555, 2, 562, 1),
			SpellBook.MODERN, 4139, -1),
    EARTH_BOLT(17, 29, 711, 123, 124,
			125, 11, 20, new RuneRequirement(556, 2, 557, 3, 562, 1),
			SpellBook.MODERN, 4142, -1),
    FIRE_BOLT(20, 35, 711, 126, 127,
			128, 12, 22, new RuneRequirement(556, 3, 554, 4, 562, 1),
			SpellBook.MODERN, 4145, -1),
			
    WIND_BLAST(24, 41, 711, 132, 133,
    		134, 13, 25, new RuneRequirement(556, 3, 560, 1),
			SpellBook.MODERN, 4148, -1),
	WATER_BLAST(27, 47, 711, 135, 136,
			137, 14, 28, new RuneRequirement(556, 3, 555, 3, 560, 1),
			SpellBook.MODERN, 4151, -1),
	EARTH_BLAST(33, 53, 711, 138, 139,
			140, 15, 31, new RuneRequirement(556, 3, 557, 4, 560, 1),
			SpellBook.MODERN, 4153, -1),
	FIRE_BLAST(38, 59, 711, 129, 130,
			131, 16, 35, new RuneRequirement(556, 4, 554, 5, 560, 1),
			SpellBook.MODERN, 4157, -1),
			
	WIND_WAVE(45, 62, 711, 158, 159, 160,
			17, 36, new RuneRequirement(556, 5, 565, 1), SpellBook.MODERN, 4159, -1),
	WATER_WAVE(
			48, 65, 711, 161, 162, 163, 18, 37, new RuneRequirement(556,
					5, 555, 7, 565, 1), SpellBook.MODERN, 4161, -1),
    EARTH_WAVE(
			52, 70, 711, 164, 165, 166, 19, 40, new RuneRequirement(556,
					5, 557, 7, 565, 1), SpellBook.MODERN, 4164, -1),
	FIRE_WAVE(
			1189, 75, 711, 155, 156, 157, 20, 42, new RuneRequirement(556,
					5, 554, 7, 565, 1), SpellBook.MODERN, 4165, -1),

	CRUMBLE_UNDEAD(22, 39, 724, 145, 146, 147, 15, 25, new RuneRequirement(
			556, 2, 557, 2, 562, 1), SpellBook.MODERN, 4147, -1),
	IBAN_BLAST(29,
			50, 708, 87, 88, 89, 25, 42, new RuneRequirement(554, 5, 560, 1),
			SpellBook.MODERN, 6003, -1),//done
     MAGIC_DART(31, 50, 1978, 327, 328, 329,
			19, 30, new RuneRequirement(560, 1, 558, 4), SpellBook.MODERN,
			47005, -1),
	SARA_STRIKE(41, 60, 811, 0, 0, 76, 20, 60,
			new RuneRequirement(554, 2, 565, 2, 556, 4), SpellBook.MODERN, 4166, -1),
	GUTHIX_STRIKE(
			42, 60, 811, 0, 0, 77, 20, 60, new RuneRequirement(554, 1, 565,
					2, 556, 4), SpellBook.MODERN, 4167, -1),
	FLAMES_OF_ZAMAROK(
			43, 60, 811, 0, 0, 78, 20, 60, new RuneRequirement(554, 4, 565,
					2, 556, 1), SpellBook.MODERN, 4168, -1),
					
	CONFUSE(2, 3, 716,
			102, 103, 104, 0, 13.0, new RuneRequirement(555, 3, 557, 2, 559, 1),//done
			
			// int id, int req, int anim, int startGfx, int projId, int endGfx, int
			// maxHit, int xpGained, RuneRequirement r, SpellBook book, int autoCastId
			SpellBook.MODERN, -1, -1),
    WEAKEN(7, 11, 716, 105, 106, 107, 0, 20,
			new RuneRequirement(555, 3, 557, 2, 559, 1), SpellBook.MODERN, -1, -1),
			
	CURSE(11, 19, 716, 108, 109, 110, 0, 29, new RuneRequirement(555, 2, 557, 3, 559, 1), SpellBook.MODERN, -1, -1),
	
	VULNERABILITY(50,
			66, 729, 167, 168, 169, 0, 76, new RuneRequirement(557, 5, 555, 5,
					566, 1), SpellBook.MODERN, -1, -1),
	ENFEEBLE(53, 73, 729,
			170, 171, 172, 0, 83, new RuneRequirement(557, 8, 555, 8, 566, 1),
			SpellBook.MODERN, -1, -1),
	STUN(57, 80, 729, 173, 174, 107, 0, 90,
			new RuneRequirement(557, 12, 555, 12, 556, 1), SpellBook.MODERN, -1, -1),
	BIND(
			12, 20, 711, 177, 178, 181, 0, 30, new RuneRequirement(557, 3,
					555, 3, 561, 2), SpellBook.MODERN, -1, -1),
					
	SNARE(30, 50,710, 177, 178, 180, 2, 60.5, new RuneRequirement(557, 4, 555, 4, 561, 3), SpellBook.MODERN, -1, -1),//done
					
					ENTANGLE(56, 79, 710,
			177, 178, 179, 4, 90, new RuneRequirement(557, 5, 555, 5, 561, 4),
			SpellBook.MODERN, -1, -1),
	TELEBLOCK(12445, 85, 1819, 0, 344,
			345, 2, 65, new RuneRequirement(563, 1, 562, 1, 560, 1),
			SpellBook.MODERN, -1, -1),
	CHARGE(-1, 80, 811, 301, 0, 0, 0, 0,
			new RuneRequirement(554, 3, 565, 3, 556, 3), SpellBook.MODERN, -1, -1),
	LOW_ALCH(
			13, 21, 712, 112, 31,
			new RuneRequirement(554, 3, 561, 1), SpellBook.MODERN),//done
	HIGH_ALCH(
			34, 55, 713, 113, 65,
			new RuneRequirement(554, 5, 561, 1), SpellBook.MODERN),//done
	TELEGRAB(
			20, 33, 728, 142, 143, 144, 0, 35, new RuneRequirement(556, 1, 563,
					1), SpellBook.MODERN, -1, -1),
					//anim 722 for superheat, splash gfx 85, gfx  148
	SUPERHEATITEM(25, 43, 722, 148, 66, new RuneRequirement(554, 4, 561, 1), SpellBook.MODERN),
	SAPH_ENCHANT(5, 7, 719, 114, 17.5, new RuneRequirement(555, 1, 564, 1), SpellBook.MODERN),
	EMERALD_ENCHANT(16, 27, 719, 114, 37, new RuneRequirement(556, 3, 564, 1), SpellBook.MODERN),
	RUBY_ENCHANT(28, 49, 720, 115, 59, new RuneRequirement(554, 5, 564, 1), SpellBook.MODERN),
	DIAMOND_ENCHANT(36, 57, 720, 115, 67, new RuneRequirement(557, 10, 564, 1), SpellBook.MODERN),
	DRAGONSTONE_ENCHANT(51, 68, 721, 116, 78, new RuneRequirement(557, 15, 555, 15, 564, 1), SpellBook.MODERN),
	ONYX_ENCHANT(61, 87, 721, 116, 97, new RuneRequirement(557, 20, 556, 20, 564, 1), SpellBook.MODERN),
					
					/**
					 * resume from here!!!!
					 */

	SMOKE_RUSH(1008, 50, 1978, 0, 384, 385, 13, 30, new RuneRequirement(560,
			2, 562, 2, 554, 1, 556, 1), SpellBook.ANCIENT, 50139, -1),
	SHADOW_RUSH(
			1012, 52, 1978, 0, 378, 379, 14, 31, new RuneRequirement(560, 2,
					562, 2, 566, 1, 556, 1), SpellBook.ANCIENT, 50187, -1),
    BLOOD_RUSH(
			1004, 56, 1978, 0, 0, 373, 15, 33, new RuneRequirement(560, 2,
					562, 2, 565, 1), SpellBook.ANCIENT, 50101, -1),
	ICE_RUSH(1000,
			58, 1978, 0, 360, 361, 16, 34, new RuneRequirement(560, 2, 562, 2,
					555, 2), SpellBook.ANCIENT, 50061, -1),
	SMOKE_BURST(1010, 62,
			1979, 0, 0, 389, 19, 36, new RuneRequirement(560, 2, 562, 4, 556,
					2, 554, 2), SpellBook.ANCIENT, 50163, -1),
	SHADOW_BURST(1014,
			64, 1979, 0, 0, 382, 20, 37, new RuneRequirement(560, 2, 562, 4,
					556, 2, 566, 2), SpellBook.ANCIENT, 50211, -1),
	BLOOD_BURST(
			1006, 68, 1979, 0, 0, 376, 21, 39, new RuneRequirement(560, 2,
					562, 4, 565, 2), SpellBook.ANCIENT, 50119, -1),
	ICE_BURST(
			1002, 70, 1979, 0, 0, 363, 22, 40, new RuneRequirement(560, 2,
					562, 4, 555, 4), SpellBook.ANCIENT, 50081, -1),
	SMOKE_BLITZ(
			1009, 74, 1978, 0, 386, 387, 23, 42, new RuneRequirement(560, 2,
					554, 2, 565, 2, 556, 2), SpellBook.ANCIENT, 50151, -1),
	SHADOW_BLITZ(
			1013, 76, 1978, 0, 380, 381, 24, 43, new RuneRequirement(560, 2,
					565, 2, 556, 2, 566, 2), SpellBook.ANCIENT, 50199, -1),
	BLOOD_BLITZ(
			1005, 80, 1978, 0, 374, 375, 25, 45, new RuneRequirement(560, 2,
					565, 4), SpellBook.ANCIENT, 50111, -1),
	ICE_BLITZ(1001, 82,
			1978, 366, 0, 367, 26, 46, new RuneRequirement(560, 2, 565, 2, 555,
					3), SpellBook.ANCIENT, 50071, -1),
	SMOKE_BARRAGE(1011, 86,
			1979, 0, 0, 391, 27, 48, new RuneRequirement(560, 4, 565, 2, 556,
					4, 554, 4), SpellBook.ANCIENT, 50175, -1),
    SHADOW_BARRAGE(
			1015, 88, 1979, 0, 0, 383, 28, 49, new RuneRequirement(560, 4,
					565, 2, 556, 4, 566, 3), SpellBook.ANCIENT, 50223, -1),
	BLOOD_BARRAGE(
			1007, 92, 1979, 0, 0, 377, 29, 51, new RuneRequirement(560, 4,
					565, 4, 566, 1), SpellBook.ANCIENT, 50129, -1),
    ICE_BARRAGE(
			1003, 94, 1979, 0, 0, 369, 30, 52, new RuneRequirement(560, 4,
					565, 2, 555, 6), SpellBook.ANCIENT, 50091, -1)

	;

	Spells(int id, int req, int anim, int startGfx, int projId, int endGfx,
			int maxHit, double xpGained, RuneRequirement r, SpellBook book,
			int autoCastId, int itemRequirement) {
		this.id = id;
		this.req = req;
		this.anim = anim;
		this.sGfx = startGfx;
		this.pId = projId;
		this.eGfx = endGfx;
		this.maxHit = maxHit;
		this.xpGained = xpGained;
		this.r = r;
		this.itemRequirement = itemRequirement;
		this.book = book;
		this.autoCastId = autoCastId;
	}
	Spells(int id, int req, int anim, int startGfx, int projId, int endGfx,
			int maxHit, double xpGained, RuneRequirement r, SpellBook book,
			int autoCastId) {
		this.id = id;
		this.req = req;
		this.anim = anim;
		this.sGfx = startGfx;
		this.pId = projId;
		this.eGfx = endGfx;
		this.maxHit = maxHit;
		this.xpGained = xpGained;
		this.r = r;
		this.book = book;
		this.autoCastId = autoCastId;
	}
	Spells(int id, int req, int anim, int startGfx, double xpGained, RuneRequirement r, SpellBook book) {
		this.id = id;
		this.req = req;
		this.anim = anim;
		this.sGfx = startGfx;
		this.xpGained = xpGained;
		this.r = r;
		this.book = book;
	}
	
	public static boolean isNonCombatSpell(Spells s) {
		Spells[] nonCombatSpells = {Spells.EMERALD_ENCHANT, Spells.RUBY_ENCHANT, Spells.SAPH_ENCHANT, Spells.DIAMOND_ENCHANT,
				Spells.DRAGONSTONE_ENCHANT, Spells.ONYX_ENCHANT, Spells.HIGH_ALCH, Spells.LOW_ALCH, Spells.SUPERHEATITEM};
		for (Spells spell : nonCombatSpells) {
			if (spell == s) {
				return true;
			}
		}
		return false;
	}

	public int getFreezeTime() {
		switch (this) {
		case ICE_RUSH:
			return 5;
		case ICE_BURST:
			return 10;
		case ICE_BLITZ:
			return 15;
		case ICE_BARRAGE:
			return 20;
		case BIND:
			return 5;
		case SNARE:
			return 10;
		case ENTANGLE:
			return 15;
		default:
			return 0;
		}
	}

	public static Spells forId(int id) {
		for (final Spells s : Spells.values()) {
			if (s.id == id) {
				return s;
			}
		}
		return null;
	}

	public RuneRequirement getRuneReq() {
		return r;
	}

	public int getLevel() {
		return req;
	}

	public int getAnimation() {
		return anim;
	}

	public int getStartGfx() {
		return sGfx;
	}

	public double getXPGain() {
		return xpGained;
	}

	public int getEndGfx() {
		return eGfx;
	}

	public int getProjectile() {
		return pId;
	}

	public int getMaximumHit() {
		return maxHit;
	}

	public int getId() {
		return id;
	}

	public SpellBook getBook() {
		return book;
	}

	public int getAutoCast() {
		return autoCastId;
	}
	public int getItemRequirement() {
		return itemRequirement;
	}
	int id;
	int req;
	int anim;
	int itemRequirement;
	int sGfx;
	int pId;
	int eGfx;
	int autoCastId;
	int maxHit;
	double xpGained;
	SpellBook book;
	RuneRequirement r;

}

