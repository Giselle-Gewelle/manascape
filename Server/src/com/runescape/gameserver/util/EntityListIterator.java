package com.runescape.gameserver.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.runescape.gameserver.world.entity.mob.Mob;

/**
 * An implementation of an iterator for an entity list.
 * @author Graham Edgecombe
 *
 * @param <E> The type of entity.
 */
public class EntityListIterator<E extends Mob> implements Iterator<E> {
	
	/**
	 * The entities.
	 */
	private Mob[] entities;
	
	/**
	 * The entity list.
	 */
	private EntityList<E> entityList;
	
	/**
	 * The previous index.
	 */
	private int lastIndex = -1;
	
	/**
	 * The current index.
	 */
	private int cursor = 0;
	
	/**
	 * The size of the list.
	 */
	private int size;

	/**
	 * Creates an entity list iterator.
	 * @param entityList The entity list.
	 */
	public EntityListIterator(EntityList<E> entityList) {
		this.entityList = entityList;
		entities = entityList.toArray(new Mob[0]);
		size = entities.length;
	}

	@Override
	public boolean hasNext() {
		return cursor < size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		lastIndex = cursor++;
		return (E) entities[lastIndex];
	}

	@Override
	public void remove() {
		if(lastIndex == -1) {
			throw new IllegalStateException();
		}
		entityList.remove(entities[lastIndex]);
	}

}
