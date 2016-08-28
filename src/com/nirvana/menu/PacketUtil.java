package com.nirvana.menu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class PacketUtil {

	public static PacketContainer openWindowPacket(int windowId, String inventoryType, String windowTitle, int size){
		return openWindowPacket(windowId, inventoryType, WrappedChatComponent.fromText(windowTitle), size);
	}
	
	public static PacketContainer closeWindow(int windowId){
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);

		packet.getIntegers().write(0, windowId);

		return packet;
	}

	public static PacketContainer openWindowPacket(int windowId, String type, WrappedChatComponent title, int slots) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);

		packet.getIntegers().write(0, windowId);
		packet.getStrings().write(0, type);
		packet.getChatComponents().write(0, title);
		packet.getIntegers().write(1, slots);
		
		return packet;
	}
	
}
