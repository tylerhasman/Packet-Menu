package com.nirvana.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.WrapperPlayServerOpenWindow;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.packetwrapper.WrapperPlayServerWindowItems;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

class PacketMenuUtilities
{	
	
	public static final String CHEST_TYPE = "minecraft:container";
	public static final String ANVIL_TYPE = "minecraft:anvil";
	
	public static void sendSetSlotGuaranteedSync(int slot, int windowId, ItemStack item, Player player){
		
		WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();
		
		packet.setSlot(slot);
		packet.setWindowId(windowId);
		packet.setSlotData(item);
		
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			packet.sendPacket(player);
		});
		
	}
	
	public static void sendWindowItemPacketGuaranteedSync(int windowId, ItemStack[] items, Player player){
		WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems();
		
		packet.setWindowId(windowId);
		packet.setSlotData(items);
		
		Bukkit.getScheduler().runTaskLater(PacketMenuPlugin.getInstance(), () -> {
			packet.sendPacket(player);
		}, 2);
	}
	
	public static void sendWindowOpenPacketGuaranteedSync(int windowId, int slots, String type, WrappedChatComponent title, Player player){
		WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow();
		
		packet.setInventoryType(type);
		packet.setNumberOfSlots(slots);
		packet.setWindowID(windowId);
		packet.setWindowTitle(title);
		
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			packet.sendPacket(player);
		});
	}
	
	public static void notifyPacketHandlerSynchronously(PacketMenuSlotHandler handler, Player player, ItemStack item, PacketMenu menu, ClickType clickType, int slot){
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			
			Interaction in = new Interaction(item, clickType, slot);
			
			handler.onClicked(player, menu, in);
		});
	}
	
	public static void notifyPacketAnvilHandleSyncronously(AnvilPacketMenuHandler handler, String text, Player pl)
	{
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			handler.onResult(text, pl);
		});
	}
	
}

