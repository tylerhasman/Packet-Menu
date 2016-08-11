package com.nirvana.menu;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface PacketMenuManager
{	
	
	/**
	 * Get the menu a player has open
	 * @param player the player
	 * @return the packet menu or null if no packet menu is open for that player
	 */
	public <T extends PacketMenu> T getOpenMenu(Player player);
	
	/**
	 * Get a collection of open packet menus
	 * @return the menus
	 */
	public Collection<PacketMenu> getOpenMenus();
	
	/**
	 * Set the open menu for a player 
	 * @param player the player
	 * @param menu the menu
	 */
	public void setPacketMenu(Player player, PacketMenu menu);
	
	/**
	 * Remove a player from the packet menu map
	 * @param player the player
	 * @return the packet menu that was closed, or null if none was open
	 */
	public <T extends PacketMenu> T unsetPacketMenu(Player player);
	
	/**
	 * Check if the open menu for a player is of a specific type
	 * @param type the type
	 * @param player the player
	 * @return true if it is of the type
	 */
	public boolean isOpenMenuOfType(Player player, Class<? extends PacketMenu> type);
	
}
