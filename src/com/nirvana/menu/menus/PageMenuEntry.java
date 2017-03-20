package com.nirvana.menu.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nirvana.menu.PacketMenuSlotHandler;

public interface PageMenuEntry extends Comparable<PageMenuEntry> {
	
	/**
	 * Get the item to display for this entry
	 * @return
	 */
	public ItemStack getItem();
	
	/**
	 * Get the handler to handle events if this entry is clicked.
	 * If it returns null no actions will be taken.
	 * @return the slot handler
	 */
	public PacketMenuSlotHandler getHandler();
	
}
