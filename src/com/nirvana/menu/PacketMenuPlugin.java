package com.nirvana.menu;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

public class PacketMenuPlugin extends JavaPlugin {

	private PacketMenuManager packetMenuManager;
	
	@Override
	public void onEnable() {
		
		packetMenuManager = new IPacketMenuManager();
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketMenuListener(this));
		
		super.onEnable();
	}
	
	public static PacketMenuManager getPacketMenuManager(){
		return getInstance().packetMenuManager;
	}



	public static PacketMenuPlugin getInstance() {
		return getPlugin(PacketMenuPlugin.class);
	}
	
}
