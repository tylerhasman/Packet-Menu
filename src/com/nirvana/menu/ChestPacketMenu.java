package com.nirvana.menu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChestPacketMenu implements PacketMenu
{
	
	private static int nextId = 0;
	
	private int size;
	private String title;
	protected final int id;
	protected final int uniqueId;
	private int addIndex;
	
	private ItemStack[] items;
	private PacketMenuSlotHandler[] handlers;
	
	private PacketMenuSlotHandler generalHandler;
	
	private BukkitTask updateTask;
	
	private boolean closed;
	
	private long lastClick;
	private int minimumClickDelay;
	
	private List<UUID> viewers;
	
	/**
	 * 
	 * @param size the menu's size must be a multiple of 9
	 * and greater than 0
	 */
	public ChestPacketMenu(int size, String title)
	{
		
		if(size == 0 || size % 9 != 0){
			throw new RuntimeException("menu size must be a multiple of 9 and greater than 0");
		}
		
		this.size = size;
		this.title = title;
		
		id = 127;
		uniqueId = nextId++;
		
		items = new ItemStack[size];
		addIndex = 0;
		handlers = new PacketMenuSlotHandler[size];
		closed = true;
		lastClick = 0;
		minimumClickDelay = 0;
		viewers = new ArrayList<>();
	}
	
	@Deprecated
	public ChestPacketMenu(int size, String title, Player player){
		this(size, title);
	}
	
	public void setMinimumClickDelay(int minimumClickDelay) {
		this.minimumClickDelay = minimumClickDelay;
	}
	
	public int getMinimumClickDelay() {
		return minimumClickDelay;
	}
	
	public void setAddIndex(int addIndex) {
		Validate.isTrue(addIndex < size, "addIndex must be less than size. addIndex: "+addIndex+" Size: "+size);
		Validate.isTrue(addIndex >= 0, "addIndex must be greater than or equal to 0. addIndex: "+addIndex);
		this.addIndex = addIndex;
	}
	
	@Override
	public int getSize()
	{
		return size;
	}
	
	private int nextOpenSlot(){
		for(int i = addIndex; i < items.length;i++){
			if(items[i] == null){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void addItem(ItemStack item, PacketMenuSlotHandler handler)
	{
		if(nextOpenSlot() == -1){
			size += 9;
			items = Arrays.copyOf(items, size);
			handlers = Arrays.copyOf(handlers, size);
		}
		
		addItem(nextOpenSlot(), item, handler);
	}
	
	@Override
	public void addItem(int x, int y, ItemStack item)
	{
		addItem(translateCoord(x, y), item);
	}
	
	@Override
	public void addItem(int x, int y, ItemStack item, PacketMenuSlotHandler handler)
	{
		addItem(translateCoord(x, y), item, handler);
	}
	
	@Override
	public void addItem(int slot, ItemStack item)
	{
		
		items[slot] = item;
		
		for(UUID viewer : viewers){
			Player pl = Bukkit.getPlayer(viewer);
			if(pl != null){
				if(PacketMenuPlugin.getPacketMenuManager().isOpenMenuOfType(pl, ChestPacketMenu.class)){
					ChestPacketMenu menu = PacketMenuPlugin.getPacketMenuManager().getOpenMenu(pl);
					
					if(menu == null){
						return;
					}
					
					if(menu.uniqueId == uniqueId){
						PacketMenuUtilities.sendSetSlotGuaranteedSync(slot, id, items[slot], pl);
					}
				}
			}
		}
	}
	
	@Override
	public void addItem(int slot, ItemStack item, PacketMenuSlotHandler handler)
	{
		addItem(slot, item);
		handlers[slot] = handler;
	}
	
	//Translate x,y coordinates to a minecraft inventory coordinate
	public int translateCoord(int x, int y){
		
		 return ((y - 1) * 9) + (x - 1);
	}
	
	@Override
	public void open(Player pl)
	{
		
		WrappedChatComponent titleComponent = WrappedChatComponent.fromText(title);
		
		PacketMenuUtilities.sendWindowOpenPacketGuaranteedSync(id, size, PacketMenuUtilities.CHEST_TYPE, titleComponent, pl);
		
		PacketMenuUtilities.sendWindowItemPacketGuaranteedSync(id, items, pl);
		
		PacketMenuPlugin.getPacketMenuManager().setPacketMenu(pl, this);
		
		closed = false;
		
		viewers.add(pl.getUniqueId());
		
	}

	@Override
	public void close(Player pl)
	{
		close();
	}
	
	@Override
	public void close(){
		if(!closed){
			for(UUID viewer : viewers){
				Player pl = Bukkit.getPlayer(viewer);
				PacketContainer packet = PacketUtil.closeWindow(this.getWindowId());
				
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(pl, packet);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
				PacketMenuPlugin.getPacketMenuManager().unsetPacketMenu(pl);
				cancelUpdateTask();
				closed = true;
			}
		}
	}

	@Override
	public void onClick(int slot, ClickType clickType, Player player, ItemStack clicked)
	{		
		
		if(System.currentTimeMillis() - lastClick < minimumClickDelay){
			return;
		}
		
		lastClick = System.currentTimeMillis();
		
		PacketMenuSlotHandler handler = handlers[slot];
		
		if(handler != null){
			PacketMenuUtilities.notifyPacketHandlerSynchronously(handler, player, items[slot], this, clickType, slot);
		}
		
		if(generalHandler != null){
			PacketMenuUtilities.notifyPacketHandlerSynchronously(generalHandler, player, items[slot], this, clickType, slot);
		}
		
		PacketMenuUtilities.sendSetSlotGuaranteedSync(slot, id, items[slot], player);
		
		
	}
	
	public void clearInventory(){
		for(int i = 0; i < items.length;i++){
			addItem(i, new ItemStack(Material.AIR));
		}
	}

	@Override
	public void addGeneralHandler(PacketMenuSlotHandler handler)
	{
		generalHandler = handler;
	}
	
	@Override
	public ItemStack getItem(int slot)
	{
		return items[slot];
	}

	@Override
	public int getWindowId()
	{
		return id;
	}
	
	@Override
	public void createUpdateTask(Runnable runnable, long delay, long period ) {
		Validate.notNull(runnable);
		Validate.isTrue(delay >= 0);
		Validate.isTrue(period >= 0);
		cancelUpdateTask();
		
		runnable.run();//Run a single time to initialize
		
		updateTask = Bukkit.getScheduler().runTaskTimer(PacketMenuPlugin.getInstance(), runnable, delay, period);
	}

	@Override
	public void cancelUpdateTask() {
		if(updateTask != null){
			updateTask.cancel();
		}
	}
	
}
