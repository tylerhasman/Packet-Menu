package com.nirvana.menu.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.nirvana.menu.ChestPacketMenu;
import com.nirvana.menu.Interaction;
import com.nirvana.menu.Item;
import com.nirvana.menu.PacketMenu;
import com.nirvana.menu.PacketMenuSlotHandler;

import net.md_5.bungee.api.ChatColor;

public class PagedMenu extends ChestPacketMenu {

	private int page;
	
	private List<PageMenuEntry> entries;
	
	private String menuTitle;
	
	public PagedMenu(List<PageMenuEntry> entries, String name) {
		this(entries, 0, name);
	}
	
	public PagedMenu(List<PageMenuEntry> entries, int page, String name) {
		super(54, name+" Page "+(page + 1));
		this.page = page;
		this.entries = entries;
		this.menuTitle = name;
		initialize();
	}
	
	private void initialize(){
		
		List<PageMenuEntry> entries = new ArrayList<>(this.entries);
		
		trimToPage(entries);
		
		Collections.sort(entries);
		
		for(int y = 0; y < 3 && entries.size() > 0;y++){
			for(int x = 0; x < 7 && entries.size() > 0;x++){
				
				PageMenuEntry info = entries.remove(0);
				
				addItem(translateCoord(x + 2, y + 2), info.getItem(), info.getHandler());
				
			}
		}
		
		if(page > 0){
			addItem(1, 6, new Item(Material.ARROW).setTitle(ChatColor.GRAY+"Last Page").build(), new PacketMenuSlotHandler() {
				
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					menu.close();
					new PagedMenu(PagedMenu.this.entries, page - 1, PagedMenu.this.menuTitle);
				}
			});
		}
		
		if(entries.size() > 0){
			addItem(9, 6, new Item(Material.ARROW).setTitle(ChatColor.GRAY+"Next Page").build(), new PacketMenuSlotHandler() {
				
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					menu.close();
					new PagedMenu(PagedMenu.this.entries, page - 1, PagedMenu.this.menuTitle);
				}
			});
		}
		
	}
	
	private void trimToPage(List<PageMenuEntry> info){
		
		int start = page * 21;
		while(start > 0){
			info.remove(start--);
		}
		
	}
	
}
