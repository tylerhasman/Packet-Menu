package com.nirvana.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class Interaction {

	private final ItemStack item;
	private final ClickType clickType;
	private final int slot;
	private final ItemStack heldItem;
	
	public Interaction(ItemStack i, ClickType ct, int slot, ItemStack heldItem) {
		item = i;
		this.clickType = ct;
		this.slot = slot;
		this.heldItem = heldItem;
	}
	
	public ItemStack getItem() {
		if(item == null){
			return new ItemStack(Material.AIR);
		}
		return item;
	}
	
	public ClickType getClickType() {
		return clickType;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public ItemStack getHeldItem() {
		if(heldItem == null){
			return new ItemStack(Material.AIR);
		}
		return heldItem;
	}
	
}
