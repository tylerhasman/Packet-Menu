package com.nirvana.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class Interaction {

	private final ItemStack item;
	private final ClickType clickType;
	private final int slot;
	
	public Interaction(ItemStack i, ClickType ct, int slot) {
		item = i;
		this.clickType = ct;
		this.slot = slot;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public ClickType getClickType() {
		return clickType;
	}
	
	public int getSlot() {
		return slot;
	}
	
}
