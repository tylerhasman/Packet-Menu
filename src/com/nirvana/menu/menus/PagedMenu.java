package com.nirvana.menu.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		super(54, getMenuName(name, page));
		this.page = page;
		this.entries = entries;
		this.menuTitle = name;
		initialize();
	}
	
	private static String getMenuName(String name, int page){
		return name+" Page "+(page + 1);
	}
	
	public void remake(){
		remake(false);
	}
	
	public void remake(boolean refreshTitle){
		if(refreshTitle){
			setTitle(getMenuName(menuTitle, page));
		}
		
		clearInventory();
		initialize();
		updateItemsForViewers();
	}
	
	private void initialize(){
		
		List<PageMenuEntry> entries = new ArrayList<>(this.entries);

		Collections.sort(entries);
		
		trimToPage(entries);
		
		for(int y = 0; y < 3 && entries.size() > 0;y++){
			for(int x = 0; x < 7 && entries.size() > 0;x++){
				
				PageMenuEntry info = entries.remove(0);
				
				addItem(translateCoord(x + 2, y + 2), info.getItem(), info.getHandler());
				
			}
		}
		
		if(page > 0){
			addItem(1, 6, new Item(Material.ARROW).setTitle(ChatColor.GRAY+"\u2190 Last Page").build(), new PacketMenuSlotHandler() {
				
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					page--;
					remake(true);
				}
			});
		}
		
		if(entries.size() > 0){
			addItem(9, 6, new Item(Material.ARROW).setTitle(ChatColor.GRAY+"Next Page \u2192").build(), new PacketMenuSlotHandler() {
				
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					page++;
					remake(true);
				}
			});
		}
		
	}
	
	private void trimToPage(List<PageMenuEntry> info){
		
		int start = page * 21;
		while(start > 0){
			info.remove(0);
			start--;
		}
		
	}
	
}
