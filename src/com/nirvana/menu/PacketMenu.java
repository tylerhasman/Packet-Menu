package com.nirvana.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface PacketMenu
{	
	
	/**
	 * 
	 * @return this packet menu's size
	 */
	public int getSize();
	
	/**
	 * Add a general handler that will activate whenever the inventory is clicked
	 * regardless of slot
	 * @param handler the handler
	 */
	public void addGeneralHandler(PacketMenuSlotHandler handler);
	
	/**
	 * Open the packet menu for a player
	 * @param pl the player
	 */
	public void open(Player pl);
	
	/**
	 * 
	 * Close the packet menu for a player
	 * @param pl the player
	 * @deprecated Use {@link #close()}
	 */
	@Deprecated
	public void close(Player pl);
	
	/**
	 * Close this packet menu (for all players looking at it)
	 */
	public void close();
	
	/**
	 * Trigger a click event for this menu
	 * @param slot the slot
	 * @param type the click type
	 * @param player the player
	 * @param item the item the player thinks they clicked
	 */
	public void onClick(int slot, ClickType type, Player player, ItemStack item);

	/**
	 * Get an item in a slot
	 * @param slot the slot id
	 * @return the item or null if the slot is emptys
	 */
	public ItemStack getItem(int slot);

	/**
	 * Get the window id that all minecraft clients require when dealing with inventory menus
	 * @return the window id
	 */
	public int getWindowId();
	
	/**
	 * Create a task that will automatically update the menu until it closes
	 * @param runnable the runnable
	 * @param delay the initial start delay
	 * @param period the time in-between each run
	 */
	public void createUpdateTask(Runnable runnable, long delay, long period);
	
	public void cancelUpdateTask();
	
	/**
	 * Set a sound to play whenever this menu is clicked
	 * @param sound
	 */
	public void setClickSound(Sound sound);

	public void setHeldItem(Player player, ItemStack item);

	public ItemStack[] getItems();
	
}
