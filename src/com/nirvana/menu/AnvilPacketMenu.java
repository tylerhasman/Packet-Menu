package com.nirvana.menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.packetwrapper.WrapperPlayServerOpenWindow;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class AnvilPacketMenu implements PacketMenu
{
	
	private int id;
	
	private ItemStack result;
	
	private String defaultText;
	
	private AnvilPacketMenuHandler handler;
	
	public AnvilPacketMenu(Player player)
	{
		id = NMSManager.invokeMethodForEntityHandle(player, "nextContainerCounter");
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
	public void addItem(ItemStack item, PacketMenuSlotHandler handler)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addItem(int x, int y, ItemStack item)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addItem(int x, int y, ItemStack item, PacketMenuSlotHandler handler)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addItem(int slot, ItemStack item)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addItem(int slot, ItemStack item, PacketMenuSlotHandler handler)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addGeneralHandler(PacketMenuSlotHandler handler)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void open(Player pl)
	{
		WrapperPlayServerOpenWindow windowPacket = new WrapperPlayServerOpenWindow();
		
		windowPacket.setWindowID(id);
		windowPacket.setInventoryType(PacketMenuUtilities.ANVIL_TYPE);
		windowPacket.setNumberOfSlots(0);
		windowPacket.setWindowTitle(WrappedChatComponent.fromText(ChatColor.DARK_GREEN+defaultText));
		
		windowPacket.sendPacket(pl);
		
		sendItemsPacket(pl);
		
		PacketMenuPlugin.getPacketMenuManager().setPacketMenu(pl, this);
	}
	
	@Override
	public void close(Player pl)
	{
		pl.closeInventory();
		PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(pl);
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
	}
	
	public void sendItemsPacket(Player pl){
		ItemStack[] items = new ItemStack[] { new Item(result).setTitle(defaultText).build(), null, new Item(result).setTitle(defaultText).build() };
		
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
	public int geWindowId()
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
	
}