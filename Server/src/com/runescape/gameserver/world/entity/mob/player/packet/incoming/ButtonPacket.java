package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.Combat.CombatStyle;
import com.runescape.gameserver.world.content.dialogue.DialogueButton;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.content.dialogue.ItemButton;
import com.runescape.gameserver.world.content.dialogue.ItemDialogueButton;
import com.runescape.gameserver.world.content.dialogue.sequence.ItemDialogueSequence;
import com.runescape.gameserver.world.content.dialogue.sequence.OptionDialogueSequence;
import com.runescape.gameserver.world.content.miscellaneous.NumberInputListener;
import com.runescape.gameserver.world.content.quest.QuestHandler;
import com.runescape.gameserver.world.content.skills.SkillGuide;
import com.runescape.gameserver.world.content.skills.SkillGuide.SkillInfo;
import com.runescape.gameserver.world.content.skills.impl.Prayer.Prayers;
import com.runescape.gameserver.world.content.skills.impl.cooking.Cooking;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.mob.MobCooldowns.CooldownFlags;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class ButtonPacket extends IncomingPacket {

	public ButtonPacket(Player player) {
		super(player);
	}

	@Override
	public void handle(Packet packet) throws Throwable {
		final int buttonId = packet.getShort();

		if(player.getPrayer().handleActivate(Prayers.forButtonId(buttonId)))
			return;
		
		QuestHandler questHandler = player.getQuestHandler();
		if(questHandler.forId(buttonId) != null) {
			questHandler.openQuestLog(questHandler.forId(buttonId));
			return;
		}

		SkillInfo skillGuide = SkillInfo.forButtonId(buttonId);
		if(skillGuide != null) {
			player.setActiveSkillGuide(new SkillGuide(player, skillGuide));
			return;
		}
		int skillTabIndex = SkillGuide.getTabIndex(buttonId);
		if(skillTabIndex != -1) {
			SkillGuide guide = player.getActiveSkillGuide();
			if(guide != null) {
				guide.openTab(skillTabIndex);
				return;
			}
		}
		
		if(DialogueButton.forId(buttonId) != null) {
			DialogueSequence dialogue = player.getCurrentDialogue();
			if(dialogue != null) {
				if(dialogue instanceof OptionDialogueSequence) {
					((OptionDialogueSequence) dialogue).showNextSequence(buttonId);
				} else {
					dialogue.showNextDialogue();
				}
				return;
			}
		}
		
		ItemDialogueButton itemButton = ItemDialogueButton.forButtonId(buttonId);
		if(itemButton != null) {
			ItemButton button = ItemDialogueButton.ItemButtonForId(buttonId);
			ItemDialogueSequence seq = (ItemDialogueSequence) player.getCurrentDialogue();
			seq.showNextSequence(itemButton.getIndex(), button.getItemIndex());
			return;
		}

		if(Cooking.isACookingButton(buttonId)) {
			switch(buttonId) {
			case 13720:
				player.getCooking().cookItem(1);
				break;
			case 13719:
				player.getCooking().cookItem(5);
				break;
			case 13718:
				player.getInterfaceState().setNumberInputListener(new NumberInputListener() {

					@Override
					public void execute(int amount) {
						player.getCooking().cookItem(amount);

					}

				});
				break;
			case 13717:
				player.getCooking().cookItem(player.getInventory().getItemAmount(player.getCooking().getItem().getRawItem()));
				break;
			}
			return;
		}
		switch(buttonId) {
		/*
		 * Show Equipment Stats
		 */
		case 18969:
			player.getPacketSender().sendInterface(18841);
			break;
		/*
		 * Close
		 */
		case 18917:
		case 19050:
			player.getPacketSender().sendCloseInterfaces();
			break;
		/*
		 * Run Toggle
		 */
		case 152:
			if(player.getSettings().getBool("running")) {
				player.getSettings().setBool("running", false);
			}
			break;
		case 153:
			if(!player.getSettings().getBool("running")) {
				player.getSettings().setBool("running", true);
			}
			break;

		/*
		 * Auto Retaliate Toggle
		 */
		case 150:
			if(!player.getSettings().getBool("autoRetaliate")) {
				player.getSettings().setBool("autoRetaliate", true);
			}
			break;
		case 151:
			if(player.getSettings().getBool("autoRetaliate")) {
				player.getSettings().setBool("autoRetaliate", false);
			}
			break;

		case 161:
			player.playAnimation(Animation.CRY);
			break;
		case 162:
			player.playAnimation(Animation.THINKING);
			break;
		case 163:
			player.playAnimation(Animation.WAVE);
			break;
		case 164:
			player.playAnimation(Animation.BOW);
			break;
		case 165:
			player.playAnimation(Animation.ANGRY);
			break;
		case 166:
			player.playAnimation(Animation.DANCE);
			break;
		case 167:
			player.playAnimation(Animation.BECKON);
			break;
		case 168:
			player.playAnimation(Animation.YES_EMOTE);
			break;
		case 169:
			player.playAnimation(Animation.NO_EMOTE);
			break;
		case 170:
			player.playAnimation(Animation.LAUGH);
			break;
		case 171:
			player.playAnimation(Animation.CHEER);
			break;
		case 172:
			player.playAnimation(Animation.CLAP);
			break;
		case 13362:
			player.playAnimation(Animation.PANIC);
			break;
		case 13363:
			player.playAnimation(Animation.JIG);
			break;
		case 13364:
			player.playAnimation(Animation.SPIN);
			break;
		case 13365:
			player.playAnimation(Animation.HEADBANG);
			break;
		case 13366:
			player.playAnimation(Animation.JOYJUMP);
			break;
		case 13367:
			player.playAnimation(Animation.RASPBERRY);
			break;
		case 13368:
			player.playAnimation(Animation.YAWN);
			break;
		case 13383:
			player.playAnimation(Animation.GOBLIN_BOW);
			break;
		case 13384:
			player.playAnimation(Animation.GOBLIN_DANCE);
			break;
		case 13369:
			player.playAnimation(Animation.SALUTE);
			break;
		case 13370:
			player.playAnimation(Animation.SHRUG);
			break;
		case 11100:
			player.playAnimation(Animation.BLOW_KISS);
			break;
		case 667:
			player.playAnimation(Animation.GLASS_BOX);
			break;
		case 6503:
			player.playAnimation(Animation.CLIMB_ROPE);
			break;
		case 6506:
			player.playAnimation(Animation.LEAN);
			break;
		case 666:
			player.playAnimation(Animation.GLASS_WALL);
			break;
		case 2458:
			if(!player.getMobCooldowns().get(CooldownFlags.COMBAT)) {
				player.getPacketSender().sendLogout();
			} else {
				player.getPacketSender().sendMessage("You can't log out until 10 seconds after the end of combat.");
			}
			break;
		case 5387:
			// player.getSettings().setWithdrawAsNotes(false);
			player.getSettings().setBool("withdrawNotes", false);
			break;
		case 5386:
			// player.getSettings().setWithdrawAsNotes(true);
			player.getSettings().setBool("withdrawNotes", true);
			break;
		case 8130:
			// player.getSettings().setSwapping(true);
			player.getSettings().setBool("swapping", true);
			break;
		case 8131:
			// player.getSettings().setSwapping(false);
			player.getSettings().setBool("swapping", false);
			break;

		/*
		 * Close Interface Buttons
		 */
		/* Begin level up interfaces */
		case 6250:
		case 6256:
		case 6209:
		case 6219:
		case 6147:
		case 6245:
		case 6214:
		case 6229:
		case 4275:
		case 6234:
		case 6261:
		case 4285:
		case 6266:
		case 6224:
		case 6240:
		case 4280:
		case 4440:
		case 4265:
		case 12125:
		case 11867:
		case 13932:
		case 4270:
			/* End level up interfaces */
		case 3651:
		case 358:
		case 362:
			player.getPacketSender().sendCloseInterfaces();
			break;

		/*
		 * Unarmed & Unknown Attack Styles
		 */
		case 5860:
			player.setCombatStyle(CombatStyle.ACCURATE);
			player.setAttackType(AttackType.CRUSH);
			player.getSettings().setInt("attackStyle", 0);
			break;
		case 5862:
			player.setCombatStyle(CombatStyle.AGGRESSIVE);
			player.setAttackType(AttackType.CRUSH);
			player.getSettings().setInt("attackStyle", 1);
			break;
		case 5861:
			player.setCombatStyle(CombatStyle.DEFENSIVE);
			player.setAttackType(AttackType.CRUSH);
			player.getSettings().setInt("attackStyle", 2);
			break;

		/*
		 * Dagger & (Short) Sword Attack Styles
		 */
		case 2282:
			player.setCombatStyle(CombatStyle.ACCURATE);
			player.setAttackType(AttackType.STAB);
			player.getSettings().setInt("attackStyle", 0);
			break;
		case 2285:
			player.setCombatStyle(CombatStyle.AGGRESSIVE);
			player.setAttackType(AttackType.STAB);
			player.getSettings().setInt("attackStyle", 1);
			break;
		case 2284:
			player.setCombatStyle(CombatStyle.AGGRESSIVE);
			player.setAttackType(AttackType.SLASH);
			player.getSettings().setInt("attackStyle", 2);
			break;
		case 2283:
			player.setCombatStyle(CombatStyle.DEFENSIVE);
			player.setAttackType(AttackType.STAB);
			player.getSettings().setInt("attackStyle", 3);
			break;

		/*
		 * Longsword & Scimiter Attack Styles
		 */
		case 2429:
			player.setCombatStyle(CombatStyle.ACCURATE);
			player.setAttackType(AttackType.SLASH);
			player.getSettings().setInt("attackStyle", 0);
			break;
		case 2432:
			player.setCombatStyle(CombatStyle.AGGRESSIVE);
			player.setAttackType(AttackType.SLASH);
			player.getSettings().setInt("attackStyle", 1);
			break;
		case 2431:
			player.setCombatStyle(CombatStyle.CONTROLLED);
			player.setAttackType(AttackType.STAB);
			player.getSettings().setInt("attackStyle", 2);
			break;
		case 2430:
			player.setCombatStyle(CombatStyle.DEFENSIVE);
			player.setAttackType(AttackType.SLASH);
			player.getSettings().setInt("attackStyle", 3);
			break;

		default:
			System.out.println("Unhandled Button Id: " + buttonId);
		}

	}

}
