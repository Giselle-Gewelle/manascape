package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.util.TextUtils;
import com.runescape.gameserver.world.ChatMessage;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;

public class ChatPacket extends IncomingPacket {

	private static final int CHAT_QUEUE_SIZE = 4;

	public ChatPacket(Player player) {
		super(player);
	}
	
	@Override
	public void handle(Packet packet) {
		int effects = packet.getByteC() & 0xFF;
		int colour = packet.getByteA() & 0xFF;
		int size = packet.getLength() - 2;
		
		System.out.println("Effect; "+effects+" - Colour; "+colour+" - Size; "+size);
		byte[] rawChatData = new byte[size];
		packet.get(rawChatData);
		if (player.getChatMessageQueue().size() >= CHAT_QUEUE_SIZE) {
			return;
		}
		String unpacked = TextUtils.textUnpack(rawChatData, size);
		unpacked = TextUtils.filterText(unpacked);
		unpacked = TextUtils.optimizeText(unpacked);
		byte[] packed = new byte[size];
		TextUtils.textPack(packed, unpacked);
		player.getChatMessageQueue().add(new ChatMessage(effects, colour, packed));
	}

}
