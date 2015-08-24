package com.runescape.gameserver.world.entity.mob.player;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.runescape.gameserver.event.impl.DeathEvent;
import com.runescape.gameserver.io.IsaacCipher;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.util.IoBufferUtils;
import com.runescape.gameserver.world.ChatMessage;
import com.runescape.gameserver.world.InterfaceState;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.RequestManager;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Bank;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.container.Inventory;
import com.runescape.gameserver.world.container.impl.EquipmentContainerListener;
import com.runescape.gameserver.world.container.impl.InterfaceContainerListener;
import com.runescape.gameserver.world.container.impl.WeaponContainerListener;
import com.runescape.gameserver.world.content.combat.CombatFormulas;
import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.content.dialogue.sequence.BasicDialogueSequence;
import com.runescape.gameserver.world.content.quest.QuestHandler;
import com.runescape.gameserver.world.content.skills.SkillGuide;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.content.skills.impl.cooking.Cooking;
import com.runescape.gameserver.world.definitions.ItemBonuses;
import com.runescape.gameserver.world.entity.Damage.Hit;
import com.runescape.gameserver.world.entity.Damage.HitType;
import com.runescape.gameserver.world.entity.action.impl.AttackAction;
import com.runescape.gameserver.world.entity.action.impl.Eating;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.UpdateFlags.UpdateFlag;
import com.runescape.gameserver.world.entity.mob.player.packet.PacketReceiver;
import com.runescape.gameserver.world.entity.mob.player.packet.PacketSender;
import com.runescape.gameserver.world.region.Region;

public class Player extends Mob {
	
	public enum Rights {
		
		PLAYER(0),
		MODERATOR(1),
		ADMINISTRATOR(2);
		
		private int value;
		
		private Rights(int value) {
			this.value = value;
		}
		
		public int toInteger() {
			return value;
		}

		public static Rights getRights(int value) {
			if(value == 1) {
				return MODERATOR;
			} else if(value == 2) {
				return ADMINISTRATOR;
			} else {
				return PLAYER;
			}
		}
	}
	
	private final IoSession session;
	private final IsaacCipher inCipher;
	private final IsaacCipher outCipher;
	private PacketSender packetSender = new PacketSender(this);
	private PacketReceiver incomingPacketManager = new PacketReceiver(this);
	
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();
	private ChatMessage currentChatMessage;
	
	private boolean active = false;
	
	private final InterfaceState interfaceState = new InterfaceState(this);
	
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();
	
	private final RequestManager requestManager = new RequestManager(this);
	
	private final int uid;
	private String password;
	private Rights rights = Rights.PLAYER;
	private boolean members = true;

	private String forcedChat;
	
	private final Container inventory = new Container(Container.Type.STANDARD, Inventory.SIZE);
	private final Container bank = new Container(Container.Type.ALWAYS_STACK, Bank.SIZE);
	private final Settings settings = new Settings(this);
	
	private Packet cachedUpdateBlock;
	
	private int selectedAttackStyleId = 0;
	
	private double runEnergy = 100;
	@SuppressWarnings("unused")
	private int weight = 0;
	
	private SkillGuide activeSkillGuide = null;
	private DialogueSequence currentDialogue = null;
	private QuestHandler questHandler = new QuestHandler(this);
	
	public Player(PlayerDetails details) {
		super();
		this.session = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
		setName(details.getName());
		this.password = details.getPassword();
		this.uid = details.getUID();
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);
	}
	
	public void performLogin() {
		setActive(true);
		
		packetSender.sendDetails();
		packetSender.sendMessage("Welcome to ManaScape.");
		
		packetSender.sendRunEnergy();
		
		packetSender.sendFriendServer(2); 
		//packetSender.sendFriendStatus(1); // 1 is World1, -45 is Classic1
		
		//sendSynchronizeConfigs();
		
		packetSender.sendMapRegion();
		
		InterfaceConfigs.setLoginConfigs(this);
		setRunEnergy(runEnergy);
		
		//packetSender.sendHintIconLocation(2, 3225, 3222,(byte) 0);
		packetSender.sendSidebarInterfaces();
		
		packetSender.sendSkills();
		getSkills().updateTooltips();
		
		//packetSender.sendWelcomeScreen();

		//sendWalkableInterface(player, 197);
		int ycoord = getLocation().getY();
		if (ycoord > 6400) {
			ycoord -= 6400;
		}
		
		//int wildernessLevel = 1 + (ycoord - 3520) / 8;
		//sendString(player, 199, "Level: " + wildernessLevel);		
		//sendMultiWayIcon(player, 1);
		
		packetSender.sendTextColor(7332, Color.GREEN);
		
		packetSender.sendRunStatus(settings.getBool("running"));
		packetSender.sendRunEnergy();
		packetSender.sendInterfaceConfig(12323, false);
		packetSender.sendConfig(300, 100 * 10);
		packetSender.sendConfig(301, 0);

		/*packetSender.sendInterfaceConfig(4240, true);
		Item[] whips = {new Item(1673), new Item(1675), new Item(1677) ,new Item(1679) ,new Item(1681) , new Item(1683), new Item(6579)};
		packetSender.sendUpdateItems(4245, whips);*/
		
		packetSender.sendInteractionOption("Follow", 3, true);
		packetSender.sendInteractionOption("Trade with", 4, true);
		
		InterfaceContainerListener inventoryListener = new InterfaceContainerListener(this, Inventory.INTERFACE);
		getInventory().addListener(inventoryListener);
		
		InterfaceContainerListener equipmentListener = new InterfaceContainerListener(this, Equipment.INTERFACE);
		getEquipment().addListener(equipmentListener);
		getEquipment().addListener(new EquipmentContainerListener(this));
		getEquipment().addListener(new WeaponContainerListener(this));
	}
	
	public QuestHandler getQuestHandler() {
		return questHandler;
	}
	
	public void setCurrentDialogue(Dialogue dialogue) {
		BasicDialogueSequence seq = new BasicDialogueSequence(this);
		seq.addDialogue(dialogue);
		setCurrentDialogue(seq);
	}
	public void setCurrentDialogue(DialogueSequence currentDialogue) {
		this.currentDialogue = currentDialogue;
		if(currentDialogue == null) {
			this.getPacketSender().sendCloseInterfaces();
		} else {
			currentDialogue.showNextDialogue();
		}
	}
	public DialogueSequence getCurrentDialogue() {
		return currentDialogue;
	}
	
	public SkillGuide getActiveSkillGuide() {
		return activeSkillGuide;
	}
	
	public void setActiveSkillGuide(SkillGuide skillGuide) {
		activeSkillGuide = skillGuide;
	}
	
	public int getSelectedAttackStyleId() {
		return this.selectedAttackStyleId;
	}
	
	public void setSelectedAttackStyleId(int selectedAttackStyleId) {
		this.selectedAttackStyleId = selectedAttackStyleId;
	}
	
	public double getRunEnergy() {
		return runEnergy;
	}
	
	public void setRunEnergy(double runEnergy) {
		setRunEnergy(runEnergy, true);
	}
	
	public void setRunEnergy(double runEnergy, boolean updateClient) {
		this.runEnergy = runEnergy;
		if(updateClient) {
			getPacketSender().sendRunEnergy();
		}
	}
	
	public RequestManager getRequestManager() {
		return requestManager;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public void write(Packet packet) {
		synchronized(this) {
			if(!active) {
				//pendingPackets.add(packet);
				return;
			} else {
				for(Packet pendingPacket : pendingPackets) {
					session.write(pendingPacket);
				}
				pendingPackets.clear();
				session.write(packet);
			}
		}
	}
	
	public Container getBank() {
		return bank;
	}
	
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}
	
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}
	
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}
	
	/**
	 * Gets the cached update block.
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}
	
	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}
	
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}
	
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}
	
	/*public InterfaceConfigs getInterfaceConfigs() {
		return interfaceConfigs;
	}*/
	
	public IsaacCipher getInCipher() {
		return inCipher;
	}
	public IsaacCipher getOutCipher() {
		return outCipher;
	}
	
	public PacketSender getPacketSender() {
		return packetSender;
	}
	public PacketReceiver getPacketReceiver() {
		return incomingPacketManager;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String pass) {
		this.password = pass;
	}
	
	public int getUID() {
		return uid;
	}
	
	public String getForcedChat() {
		return forcedChat;
	}
	
	public IoSession getSession() {
		return session;
	}
	
	public void setRights(Rights rights) {
		this.rights = rights;
	}
	public Rights getRights() {
		return rights;
	}

	public boolean isMembers() {
		return members;
	}
	
	public boolean isAdmin() {
		return this.getRights() == Rights.ADMINISTRATOR;
	}
	
	public boolean isMod() {
		return this.getRights() == Rights.MODERATOR;
	}
	
	public boolean isStaff() {
		return isMod() || isAdmin();
	}
	
	public void setMembers(boolean members) {
		this.members = members;
	}
	
	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + getName() + " rights=" + rights + " members=" + members + " index=" + this.getIndex() + "]";
	}
	
	public void setActive(boolean active) {
		synchronized(this) {
			this.active = active;
		}
	}

	public boolean isActive() {
		synchronized(this) {
			return active;
		}
	}

	public Container getInventory() {
		return inventory;
	}
	
	public void updatePlayerAttackOptions(boolean enable) {
		if(enable) {
			getPacketSender().sendInteractionOption("Attack", 1, true);
			//actionSender.sendOverlay(381);
		} else {
			
		}
	}
	
	public void requestForcedChat(String text) {
		this.forcedChat = text;
		this.getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
	}
	
	/**
	 * Manages updateflags and HP modification when a hit occurs.
	 * @param source The Entity dealing the blow.
	 */
	public void inflictDamage(Hit inc, Mob source) {
		if(!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if(!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		getSkills().detractLevel(Skill.HITPOINTS, inc.getDamage());
		if(getSkills().getLevel(Skill.HITPOINTS) <= 0) {
			if(!this.isDead()) {
				World.getInstance().submit(new DeathEvent(this));
			}
			return;
		}
		if(!this.isInCombat()) {
			this.setInCombat(true);
			this.setAggressorState(false);
			if(getSettings().getBool("autoRetaliate")) {
				this.getActionQueue().addAction(new AttackAction(this, source));
			}
		}
	}
	
	public void inflictDamage(Hit inc) {
		this.inflictDamage(inc, null);
	}

	public void deserialize(IoBuffer buf) {
		//this.name = IoBufferUtils.getRS2String(buf);
		//this.nameLong = NameUtils.nameToLong(this.name);
		this.password = IoBufferUtils.getRS2String(buf);
		this.rights = Player.Rights.getRights(buf.getUnsigned());
		this.members = buf.getUnsigned() == 1 ? true : false;
		setLocation(Location.create(buf.getUnsignedShort(), buf.getUnsignedShort(), buf.getUnsigned()));
		int[] look = new int[13];
		for(int i = 0; i < 13; i++) {
			look[i] = buf.getUnsigned();
		}
		getAppearance().setLook(look);
		for(int i = 0; i < Equipment.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				getEquipment().set(i, item);
			}
		}
		for (Skill s : Skill.values()) {
			getSkills().setSkill(s, buf.getUnsigned(), buf.getUnsigned(), buf.getDouble());
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				inventory.set(i, item);
			}
		}
		if(buf.hasRemaining()) { // backwards compat
			for(int i = 0; i < Bank.SIZE; i++) {
				int id = buf.getUnsignedShort();
				if(id != 65535) {
					int amt = buf.getInt();
					Item item = new Item(id, amt);
					bank.set(i, item);
				}
			}
		}
		if(buf.hasRemaining()) {
			for(int i = 0; i < Settings.SIZE; i++) {
				double setting = buf.getDouble();
				if(setting != 65565) {
					//settings.set(i, setting);
				}
			}
		}
		try {
			setCurrentSpellBook(SpellBook.valueOf(IoBufferUtils.getRS2String(buf).toUpperCase()));
		} catch(Exception e) {
			setCurrentSpellBook(SpellBook.MODERN);//Only if it's an older account that wouldn't have the first save creation.
		}
		try {
			this.isYellBanned = buf.getUnsigned() == 1 ? true : false;
		} catch(Exception e) {
			this.isYellBanned = false;
		}
	}

	public void serialize(IoBuffer buf) {
		//IoBufferUtils.putRS2String(buf, NameUtils.formatName(name));
		IoBufferUtils.putRS2String(buf, password);
		buf.put((byte) rights.toInteger());
		buf.put((byte) (members ? 1 : 0));
		buf.putShort((short) getLocation().getX());
		buf.putShort((short) getLocation().getY());
		buf.put((byte) getLocation().getHeight());
		int[] look = getAppearance().getLook();
		for(int i = 0; i < 13; i++) {
			buf.put((byte) look[i]);
		}
		for(int i = 0; i < Equipment.SIZE; i++) {
			Item item = getEquipment().get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for (Skill s : Skill.values()) {
			buf.put((byte) getSkills().getLevel(s));
			buf.put((byte) getSkills().getRealLevel(s));
			buf.putDouble((double) getSkills().getExperience(s));
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			Item item = inventory.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Bank.SIZE; i++) {
			Item item = bank.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Settings.SIZE; i++) {
			//double setting = settings.get(i);
			//buf.putDouble(setting);
		}
		IoBufferUtils.putRS2String(buf, currentSpellBook.toString());
		buf.put((byte) (this.isYellBanned ? 1 : 0));
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	@Override
	public void inflictDamage(int damage, HitType type) {
		
	}

	@Override
	public boolean isPlayer() {
		return true;
	}
	
	public void sendOMessage(String message) {
		if(this.isAdmin()) {
			getPacketSender().sendMessage(message);
		}
	}
	
	Cooking cooking = new Cooking(this);
	
	public long eatingDelay = 0;
	
	public Cooking getCooking() {
		return cooking;
	}
	
	Eating eating = new Eating(this);
	
	public Eating getEating() {
		return eating;
	}

	@Override
	public int getLockIndex() {
			return -this.getIndex()- 1;
	}

	@Override
	public int getSize() {
		return 1;
	}
	
	public enum SpellBook {
		MODERN(1151), ANCIENT(12855);
		
		private int interfaceId;
		
		SpellBook(int interfaceId) {
			this.interfaceId = interfaceId;
		}
		
		public int getInterfaceId() {
			return interfaceId;
		}
		
		public int getSidebarIconId()	{
			return 6;
		}
		
	}
	
	private SpellBook currentSpellBook = SpellBook.MODERN;
	
	public SpellBook getCurrentSpellBook() {
		return currentSpellBook;
	}
	
	public void setCurrentSpellBook(SpellBook book) {
		this.currentSpellBook = book;
		getPacketSender().sendSidebarInterface(6, book.getInterfaceId());
	}
	
	public long buryDelay = 0;
	
	private boolean isYellBanned = false;
	
	public boolean isYellBanned() {
		return isYellBanned;
	}
	
	public void yellBan() {
		this.isYellBanned = true;
	}
	
	public void unYellBan() {
		this.isYellBanned = false;
	}
	
	private ItemBonuses playerBonuses = new ItemBonuses(0, 0, 0, 0, 0, 0 ,0 ,0, 0, 0, 0, 0);
	
	public ItemBonuses getPlayerBonuses() {
		return playerBonuses;
	}

	@Override
	public int meleeDef() {
		return CombatFormulas.bestMeleeDefence(this);
	}

	@Override
	public int meleeAtk() {
		return CombatFormulas.npcMeleeAttack(this);
	}

	@Override
	public int mageDef() {
		return CombatFormulas.mageDefence(this);
	}

	@Override
	public int mageAtk() {
		return CombatFormulas.npcMagicAttack(this);
	}

	@Override
	public int rangeAtk() {
		return 0;
	}

	@Override
	public int rangeDef() {
		return 0;
	}

}
