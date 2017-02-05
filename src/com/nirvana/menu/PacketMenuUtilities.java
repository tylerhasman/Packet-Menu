package com.nirvana.menu;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

class PacketMenuUtilities
{	
	
	public static final String CHEST_TYPE = "minecraft:container";
	public static final String ANVIL_TYPE = "minecraft:anvil";
	
	public static void sendSetSlotGuaranteedSync(int slot, int windowId, ItemStack item, Player player){
		
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_SLOT);
		
		packet.getIntegers().write(0, windowId);
		packet.getIntegers().write(1, slot);
		packet.getItemModifier().write(0, item);
		
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}
	
	public static void sendWindowItemPacketGuaranteedSync(int windowId, ItemStack[] items, Player player){
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.WINDOW_ITEMS);
		
		packet.getIntegers().write(0, windowId);
		packet.getItemListModifier().write(0, Arrays.asList(items));
		
		Bukkit.getScheduler().runTaskLater(PacketMenuPlugin.getInstance(), () -> {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 2);
	}
	
	public static void sendWindowOpenPacketGuaranteedSync(int windowId, int slots, String type, WrappedChatComponent title, Player player){
		/*WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow();
		
		packet.setInventoryType(type);
		packet.setNumberOfSlots(slots);
		packet.setWindowID(windowId);
		packet.setWindowTitle(title);*/
		
		PacketContainer packet = PacketUtil.openWindowPacket(windowId, type, title, slots);
		
		Bukkit.getScheduler().runTask(PacketMenuPlugin.getInstance(), () -> {
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

