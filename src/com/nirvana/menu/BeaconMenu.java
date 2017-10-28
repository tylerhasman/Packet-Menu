package com.nirvana.menu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

public class BeaconMenu implements PacketMenu {

	private ItemStack item;
	
	private BeaconMenuListener beaconListener;
	
	private PacketMenuSlotHandler handler;
	
	private List<UUID> viewers;
	
	private boolean closed;
	
	private int beaconTier;
	
	private BeaconEffect primaryEffect, secondaryEffect;
	
	public BeaconMenu() {
		viewers = new ArrayList<>();
		closed = true;
		primaryEffect = BeaconEffect.NONE;
		secondaryEffect = BeaconEffect.NONE;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
	}
	
	public BeaconEffect getPrimaryEffect() {
		return primaryEffect;
	}
	
	public BeaconEffect getSecondaryEffect() {
		return secondaryEffect;
	}
	
	public void setPrimaryEffect(BeaconEffect effect){
		if(effect == null){
			throw new IllegalArgumentException("null is not a valid value");
		}
		primaryEffect = effect;
	}
	
	public void setSecondaryEffect(BeaconEffect effect) {
		if(effect == null){
			throw new IllegalArgumentException("null is not a valid value");
		}
		this.secondaryEffect = effect;
	}

	@Override
	public int getSize() {
		return 1;
	}
	
	public void setBeaconTier(int beaconTier) {
		if(beaconTier < 0 || beaconTier > 4){
			throw new IllegalArgumentException("must be between 0 and 4 inclusively. Got "+beaconTier);
		}
		this.beaconTier = beaconTier;
	}
	
	public void setBeaconListener(BeaconMenuListener beaconListener) {
		this.beaconListener = beaconListener;
	}

	@Override
	public void addGeneralHandler(PacketMenuSlotHandler handler) {
		this.handler = handler;
	}

	@Override
	public void open(Player pl) {
		viewers.add(pl.getUniqueId());
		closed = false;
		
		PacketMenuUtilities.sendWindowOpenPacketGuaranteedSync(getWindowId(), 1, PacketMenuUtilities.BEACON_TYPE, "Beacon", pl);
		PacketMenuUtilities.sendSetSlotGuaranteedSync(0, getWindowId(), item, pl);
		PacketMenuUtilities.sendWindowPropertyGuaranteedSync(getWindowId(), 0, beaconTier, pl);
		PacketMenuUtilities.sendWindowPropertyGuaranteedSync(getWindowId(), 1, primaryEffect.getMagicValue(), pl);
		PacketMenuUtilities.sendWindowPropertyGuaranteedSync(getWindowId(), 2, secondaryEffect.getMagicValue(), pl);
	
		PacketMenuPlugin.getPacketMenuManager().setPacketMenu(pl, this);
		
	}

	@Override
	public void close(Player pl) {
		close();
	}

	@Override
	public void close() {
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
			viewers.clear();
		}
	}

	@Override
	public void onClick(int slot, ClickType clickType, Player player, ItemStack item) {
		if(handler != null){
			PacketMenuUtilities.notifyPacketHandlerSynchronously(handler, player, getItem(slot), this, clickType, slot, null);
		}
		
		PacketMenuUtilities.sendSetSlotGuaranteedSync(slot, getWindowId(), getItem(slot), player);
	}

	public void onSelectEffects(BeaconEffect primary, BeaconEffect secondary, Player player){
		if(beaconListener != null){
			beaconListener.onSelectPower(primary, secondary, this, player);
		}
	}
	
	@Override
	public ItemStack getItem(int slot) {
		if(slot == 0){
			return item;
		}
		return null;
	}

	@Override
	public int getWindowId() {
		return 127;
	}

	@Override
	public void createUpdateTask(Runnable runnable, long delay, long period) {
		
	}

	@Override
	public void cancelUpdateTask() {
		
	}

	@Override
	public void setClickSound(Sound sound) {
		
	}

	@Override
	public void setHeldItem(Player player, ItemStack item) {
		PacketMenuUtilities.sendSetSlotGuaranteedSync(-1, -1, item, player);
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack [] { item };
	}

}
