package com.nirvana.menu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class AnvilPacketMenu implements PacketMenu
{
	
	private int id;
	
	private ItemStack result;
	
	private String defaultText;
	
	private AnvilPacketMenuHandler handler;
	
	private List<UUID> viewers;
	
	private Sound clickSound;
	
	@Deprecated
	public AnvilPacketMenu(Player player){
		this();
	}
	
	public AnvilPacketMenu()
	{
		id = 127;
		viewers = new ArrayList<>();
	}
	
	public void setResult(ItemStack result){
		this.result = result;
	}
	
	public void setDefaultText(String dt){
		defaultText = dt;
	}
	
	public void setHandler(AnvilPacketMenuHandler handler){
		this.handler = handler;
	}
	
	@Override
	public int getSize()
	{
		return 3;
	}
	
	@Override
	public void addGeneralHandler(PacketMenuSlotHandler handler)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void open(Player pl)
	{
		
		PacketContainer packet = PacketUtil.openWindowPacket(id, PacketMenuUtilities.ANVIL_TYPE, ChatColor.DARK_GREEN+defaultText, 0);
		
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(pl, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		sendItemsPacket(pl);
		
		PacketMenuPlugin.getPacketMenuManager().setPacketMenu(pl, this);
		viewers.add(pl.getUniqueId());
	}
	
	@Override
	public void close(Player pl)
	{
		close();
	}
	
	@Override
	public void close() {
		for(UUID viewer : viewers){
			Player pl = Bukkit.getPlayer(viewer);
			pl.closeInventory();
			PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(pl);
		}
	}
	
	public void onClick(int slot, ClickType type, Player pl, ItemStack item){
		
		close(pl);
		
		if(slot != 2)
		{
			return;
		}
		
		if(handler != null){
			
			String text = item.getItemMeta().getDisplayName();
			
			if(text == null){
				text = "";
			}
			
			PacketMenuUtilities.notifyPacketAnvilHandleSyncronously(handler, text, pl);
		}
		
		if(clickSound != null)
			pl.playSound(pl.getLocation(), clickSound, 1F, 1F);
	}
	
	public void sendItemsPacket(Player pl){
		ItemStack[] items = getItems();
		
		PacketMenuUtilities.sendWindowItemPacketGuaranteedSync(id, items, pl);
	}

	@Override
	public ItemStack getItem(int slot)
	{
		if(slot == 2){
			return result;
		}else{
			return null;
		}
		
	}

	@Override
	public int getWindowId()
	{
		return id;
	}

	@Override
	public void createUpdateTask(Runnable runnable, long delay, long period) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void cancelUpdateTask() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClickSound(Sound sound) {
		clickSound = sound;
	}

	@Override
	public void setHeldItem(Player player, ItemStack clickedItem) {
		
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] { new Item(result).setTitle(defaultText).build(), null, new Item(result).setTitle(defaultText).build() };
	}
	
}
