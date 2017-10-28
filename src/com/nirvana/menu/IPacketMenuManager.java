package com.nirvana.menu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IPacketMenuManager implements PacketMenuManager
{
	
	private Map<UUID, PacketMenu> openMenus;
	
	public IPacketMenuManager()
	{
		openMenus = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public <T extends PacketMenu> T getOpenMenu(Player player)
	{
		if(!openMenus.containsKey(player.getUniqueId())){
			return null;
		}
		return (T) openMenus.get(player.getUniqueId());
	}
	
	@Override
	public Collection<PacketMenu> getOpenMenus()
	{
		return openMenus.values();
	}
	
	@Override
	public void setPacketMenu(Player player, PacketMenu menu)
	{
		unsetPacketMenu(player);
		
		openMenus.put(player.getUniqueId(), menu);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PacketMenu> T unsetPacketMenu(Player player)
	{
		
		T menu = (T) openMenus.remove(player.getUniqueId());
		
		if(menu != null){
			menu.close();
		}
		
		return menu;
	}
	
	@Override
	public boolean isOpenMenuOfType(Player player, Class<? extends PacketMenu> type)
	{
		
		PacketMenu menu = getOpenMenu(player);
		
		if(menu == null)
		{
			return false;
		}
		
		if(type.isInstance(menu)){
			return true;
		}
		
		return false;
	}
	
}
