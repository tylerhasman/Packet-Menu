package com.nirvana.menu;

import org.bukkit.entity.Player;

public interface PacketMenuSlotHandler
{	
	
	public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo);
	
}
