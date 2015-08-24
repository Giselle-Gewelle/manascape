package com.runescape.gameserver.world.content.dialogue;

public enum ChatAnimation {

	CONTENT(591),
	HAPPY(588),
	EVIL(592),
	EVIL_CONTINUED(592),
	DELIGHTED_EVIL(594),
	ANNOYED(595),
	CALM(589),
	CALM_TALK(590),
    DISTRESSED(596),
    DISTRESSED_CONTINUED(597),
    ALMOST_CRYING(598),
    BOWS_HEAD_WHILE_SAD(599), 
    DRUNK_TO_LEFT(600),
    DRUNK_TO_RIGHT(601),
    DISINTERESTED(602),
    SLEEPY(603),
    PLAIN_EVIL(604),
    LAUGH_1(605),
    LAUGH_2(606),
    LAUGH_3(607),
    LAUGH_4(608),
    EVIL_LAUGH(609),
    SAD(610), 
    MORE_SAD(611),
    THINKING(612), 
    NEARLY_CRYING(613),
    ANGER_1(614),
    ANGER_2(615),
    ANGER_3(616),
    ANGER_4(617);
	
	private int id;
	
	private ChatAnimation(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}
