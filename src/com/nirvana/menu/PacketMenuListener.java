package com.nirvana.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.packetwrapper.WrapperPlayClientWindowClick;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketMenuListener extends PacketAdapter implements Listener 
{

	public PacketMenuListener(Plugin plugin)
	{
		super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.CLOSE_WINDOW, 
				PacketType.Play.Client.WINDOW_CLICK);
		Bukkit.getPluginManager().registerEvents(this, PacketMenuPlugin.getInstance());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerOpenMenu(InventoryOpenEvent event){
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		
		PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(player);
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event)
	{
		
		PacketType type = event.getPacketType();
		Player player = event.getPlayer();
		
		if(type == PacketType.Play.Client.CLOSE_WINDOW){
			
			Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
				
				PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(player);
				
			});
			
		}else if(type == PacketType.Play.Client.WINDOW_CLICK){
			WrapperPlayClientWindowClick clickPacket = new WrapperPlayClientWindowClick(event.getPacket());
			
			int slot = clickPacket.getSlot();
			
			if(slot < 0)
				return;
			
			PacketMenu menu = PacketMenuPlugin.getPacketMenuManager().getOpenMenu(player);
			
			if(menu != null){
				
				if(menu.geWindowId() != clickPacket.getWindowId()){
					return;
				}
				
				if(slot >= menu.getSize()){
					return;
				}
				
				PacketMenuUtilities.sendSetSlotGuaranteedSync(-1, -1, null, player);
				
				ClickType clickType = getClickType(clickPacket.getActionNumber(), clickPacket.getButton());
				
				PacketMenuUtilities.sendSetSlotGuaranteedSync(clickPacket.getSlot(), clickPacket.getWindowId(), menu.getItem(clickPacket.getSlot()), player);
				
				if(clickType == ClickType.NUMBER_KEY || clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT)
				{
					Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> player.updateInventory());
				}
				
				if(clickPacket.getClickedItem() != null){
					menu.onClick(slot, clickType, player, clickPacket.getClickedItem());
				}
				
				event.setReadOnly(false);
				event.setCancelled(true);
			}
		}
	}
	
	public ClickType getClickType(int mode, int button){
		if(mode == 0){
			if(button == 0)
			{
				return ClickType.LEFT;
			}else{
				return ClickType.RIGHT;
			}
		}else if(mode == 1){
			if(button == 0){
				return ClickType.SHIFT_LEFT;
			}else{
				return ClickType.SHIFT_RIGHT;
			}
		}else if(mode == 2){
			return ClickType.NUMBER_KEY;
		}else if(mode == 3 && button == 2){
			return ClickType.MIDDLE;
		}else if(mode == 4){
			if(button == 0)
			{
				return ClickType.DROP;
			}else if(button == 1){
				return ClickType.CONTROL_DROP;
			}
		}else if(mode == 6){
			return ClickType.DOUBLE_CLICK;
		}
		
		return ClickType.UNKNOWN;
			
	}

}