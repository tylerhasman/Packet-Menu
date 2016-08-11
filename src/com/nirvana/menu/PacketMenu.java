package com.nirvana.menu;

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
	 * Add an item to this packet menu
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param item the item
	 */
	public void addItem(int x, int y, ItemStack item);
	
	/**
	 * Adds an item to the next open slot in the packet menu
	 * @param item the item
	 * @param handler the handler
	 */
	public void addItem(ItemStack item, PacketMenuSlotHandler handler);
	
	/**
	 * Add an item to this packet menu and set its handler
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param item the item
	 * @param handler the handler
	 */
	public void addItem(int x, int y, ItemStack item, PacketMenuSlotHandler handler);
	
	/**
	 * Add an item to this packet menu
	 * @param slot the slot id
	 * @param item the item
	 */
	public void addItem(int slot, ItemStack item);
	
	/**
	 * Add an item to this packet menu and set its handler
	 * @param slot the slot id
	 * @param item the item
	 * @param handler the handler
	 */
	public void addItem(int slot, ItemStack item, PacketMenuSlotHandler handler);
	
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
	 * Close the packet menu for a player
	 * @param pl the player
	 */
	public void close(Player pl);
	
	/**
	 * Called internally, don't call
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
	public int geWindowId();
	
	/**
	 * Create a task that will automatically update the menu until it closes
	 * @param runnable the runnable
	 * @param delay the initial start delay
	 * @param period the time in-between each run
	 */
	public void createUpdateTask(Runnable runnable, long delay, long period);
	
	public void cancelUpdateTask();
	
}
