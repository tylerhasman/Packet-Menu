package com.nirvana.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.md_5.bungee.api.ChatColor;

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
		
		if(player == null){
			return;
		}
		
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
			PacketContainer packet = event.getPacket();

			int windowId = packet.getIntegers().read(0);
			int slot = packet.getIntegers().read(1);
			int button = packet.getIntegers().read(2);
			
			ItemStack clickedItem = packet.getItemModifier().read(0);
			
			ClickMode mode = packet.getEnumModifier(ClickMode.class, 5).read(0);
			
			if(slot < 0)
				return;
			
			PacketMenu menu = PacketMenuPlugin.getPacketMenuManager().getOpenMenu(player);
			
			if(menu != null){
				
				event.setReadOnly(false);
				event.setCancelled(true);
				
				if(menu.getWindowId() != windowId){
					return;
				}
				
				ClickType clickType = getClickType(mode, button);
				
				if(clickType == null){//Special return for the drag
					Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> player.updateInventory());
					PacketMenuUtilities.sendWindowItemPacketGuaranteedSync(windowId, menu.getItems(), player);
					return;
				}
				
				if(slot >= menu.getSize() && clickType == ClickType.LEFT){
					menu.setHeldItem(player, clickedItem);
					return;
				}else if(slot >= menu.getSize()){
					PacketMenuUtilities.sendSetSlotGuaranteedSync(slot, windowId, clickedItem, player);
					return;
				}
				
				if(clickType == ClickType.UNKNOWN){
					player.sendMessage(ChatColor.RED+"Unknown click type! Mode: "+mode+" button: "+button);
				}

				if(clickedItem != null){
					menu.onClick(slot, clickType, player, clickedItem);
				}
				
				if(PacketMenuPlugin.getPacketMenuManager().getOpenMenu(player) == menu){//Did the menu change?
					//If not do this
					PacketMenuUtilities.sendSetSlotGuaranteedSync(-1, -1, null, player);
					
					PacketMenuUtilities.sendSetSlotGuaranteedSync(slot, windowId, menu.getItem(slot), player);
					

					menu.setHeldItem(player, new ItemStack(Material.AIR));
				}
				
				if(clickType == ClickType.NUMBER_KEY || clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT)
				{
					Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> player.updateInventory());
				}
				
				
			}
		}
	}
	
	public ClickType getClickType(ClickMode mode, int button){
		if(mode == ClickMode.PICKUP){
			if(button == 0)
			{
				return ClickType.LEFT;
			}else{
				return ClickType.RIGHT;
			}
		}else if(mode == ClickMode.QUICK_MOVE){
			if(button == 0){
				return ClickType.SHIFT_LEFT;
			}else{
				return ClickType.SHIFT_RIGHT;
			}
		}else if(mode == ClickMode.SWAP){
			return ClickType.NUMBER_KEY;
		}else if(mode == ClickMode.CLONE && button == 2){
			return ClickType.MIDDLE;
		}else if(mode == ClickMode.THROW){
			if(button == 0){
				return ClickType.DROP;
			}else if(button == 1){
				return ClickType.CONTROL_DROP;
			}
		}else if(mode == ClickMode.PICKUP_ALL){
			return ClickType.DOUBLE_CLICK;
		}else if(mode == ClickMode.QUICK_CRAFT){
			return null;
		}
		
		return ClickType.UNKNOWN;
			
	}

}
