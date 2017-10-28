package com.nirvana.menu;

import org.bukkit.potion.PotionEffectType;

public enum BeaconEffect {

	SPEED(1, PotionEffectType.SPEED),
	HASTE(3, PotionEffectType.FAST_DIGGING),
	RESISTANCE(11, PotionEffectType.DAMAGE_RESISTANCE),
	JUMP(8, PotionEffectType.JUMP),
	STRENGTH(5, PotionEffectType.INCREASE_DAMAGE),
	REGENERATION(10, PotionEffectType.REGENERATION),
	NONE(-1, null);
	
	private final int magicValue;
	
	private final PotionEffectType effect;
	
	private BeaconEffect(int mv, PotionEffectType type) {
		magicValue = mv;
		effect = type;
	}
	
	public int getMagicValue() {
		return magicValue;
	}

	public PotionEffectType getPotionEffect(){
		return effect;
	}
	
	public static BeaconEffect getById(int id) {
		for(BeaconEffect effect : values()){
			if(effect.magicValue == id){
				return effect;
			}
		}
		return BeaconEffect.NONE;
	}
	
}
