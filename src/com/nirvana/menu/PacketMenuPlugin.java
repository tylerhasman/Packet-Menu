package com.nirvana.menu;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.comphenix.protocol.ProtocolLibrary;

import net.md_5.bungee.api.ChatColor;

public class PacketMenuPlugin extends JavaPlugin implements PluginMessageListener {

	private PacketMenuManager packetMenuManager;
	
	@Override
	public void onEnable() {
		
		packetMenuManager = new IPacketMenuManager();
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketMenuListener(this));
		
		super.onEnable();
		
		getServer().getMessenger().registerIncomingPluginChannel(this, "MC|Beacon", this);
	}
	
	public static PacketMenuManager getPacketMenuManager(){
		return getInstance().packetMenuManager;
	}

	public static PacketMenuPlugin getInstance() {
		return getPlugin(PacketMenuPlugin.class);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(channel.equals("MC|Beacon")){
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(message));
			
			try {
				int primary = dis.readInt();
				int secondary = dis.readInt();
				
				PacketMenu menu = getPacketMenuManager().getOpenMenu(player);
				
				if(menu instanceof BeaconMenu){
					((BeaconMenu) menu).onSelectEffects(BeaconEffect.getById(primary), BeaconEffect.getById(secondary), player);
				}else{
					player.sendMessage(ChatColor.RED+"Your menu is not a beacon!");
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
