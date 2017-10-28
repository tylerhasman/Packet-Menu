package com.nirvana.menu;

import org.bukkit.entity.Player;

public interface BeaconMenuListener {

	public void onSelectPower(BeaconEffect primary, BeaconEffect secondary, BeaconMenu menu, Player player);
	
}
