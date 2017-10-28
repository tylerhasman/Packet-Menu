package com.nirvana.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Item implements Cloneable
{
	
	private Material type;
	private int data;
	private int amount;
	private String title;
	private List<String> lore = new ArrayList<>();
	private Color color;
	private HashMap<Enchantment, Integer> enchantments;
	private boolean unbreakable;
	
	private List<PotionEffect> effects;

	public Item(Material type)
	{
		this(type, 1);
	}

	public Item(Material type, int amount)
	{
		this(type, amount, 0);
	}

	public Item(Material type, int amount, int data)
	{
		this.type = type;
		this.amount = amount;
		this.data = data;
		this.enchantments = new HashMap<Enchantment, Integer>();
	}

	public Item(org.bukkit.inventory.ItemStack item)
	{
		Validate.notNull(item);
		this.enchantments = new HashMap<Enchantment, Integer>();
		this.type = item.getType();
		this.data = item.getDurability();
		this.amount = item.getAmount();
		if (item.hasItemMeta())
		{
			if (item.getItemMeta().hasDisplayName()) {
				this.title = item.getItemMeta().getDisplayName();
			}
			if (item.getItemMeta().hasLore()) {
				this.lore = item.getItemMeta().getLore();
			}
		}
		if (item.getEnchantments() != null) {
			this.enchantments.putAll(item.getEnchantments());
		}
		if ((item.getType().toString().toLowerCase().contains("leather")) && 
				((item.getItemMeta() instanceof LeatherArmorMeta)))
		{
			LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
			this.color = lam.getColor();
		}
	}

	public Item(Item item)
	{
		this(item.build());
	}
	
	public Item addEffect(PotionEffectType type, int level, int duration){
		PotionEffect effect = type.createEffect(duration, level);
		
		if (effect == null) {
			effects = new ArrayList<>();
		}
		
		effects.add(effect);

		return this;
	}
	
	public Item setUnbreakable(boolean flag){
		unbreakable = flag;
		return this;
	}

	public Item setType(Material type)
	{
		this.type = type;return this;
	}
	
	public Item addLore(String... lore){
		for(String s : lore){
			this.lore.add(s);
		}
		
		return this;
	}
	
	public Item setBase64(String base) {
		//throw new NotImplementedException("setBase64 is no longer implemented please see Item#setOwner");
		return this;
	}
	
	public Item setTexture(String str){
		//throw new NotImplementedException("setTexture is no longer implemented please see Item#setOwner");
		return this;
	}

	public Item setData(int data)
	{
		this.data = data;return this;
	}

	public Item setAmount(int amount)
	{
		this.amount = amount;return this;
	}

	public Item setTitle(String title)
	{
		this.title = title;return this;
	}

	public Item setLore(String... lore)
	{
		this.lore = Arrays.asList(lore);return this;
	}

	public Item setLore(List<String> list)
	{
		this.lore = list;return this;
	}
	
	public Item setSkullType(SkullType type){
		
		Validate.notNull(type);
		
		setData(type.data);
		
		return this;
	}

	public List<String> getLore(){return this.lore;}
	public Material getType(){return this.type;}
	
	public Item addEnchantment(Enchantment e, int level)
	{
		this.enchantments.put(e, Integer.valueOf(level));return this;
	}

	public Item setColor(Color c)
	{
		if (!this.type.toString().toLowerCase().contains("leather")) {
			throw new RuntimeException("Cannot set color of non-leather items.");
		}
		this.color = c;return this;
	}

/*	public Item setOwner(String s)
	{
		if (!this.type.equals(Material.SKULL_ITEM)) {
			throw new RuntimeException("Cannot set the owner of any item besides SKULL_ITEM");
		}
		this.owner = Util.getUUID(s);
		return this;
	}*/

	@SuppressWarnings("deprecation")
	public org.bukkit.inventory.ItemStack build()
	{
		Validate.noNullElements(new Object[] { this.type, Integer.valueOf(this.data), Integer.valueOf(this.amount) });

		org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(this.type, this.amount, (short)this.data);
		ItemMeta im = stack.getItemMeta();
		
		im.addItemFlags(ItemFlag.values());

		if ((this.title != null) && 
				(this.title != "")) {
			im.setDisplayName(this.title);
		}
		
		if ((this.lore != null) && 
				(!this.lore.isEmpty())) {
			im.setLore(this.lore);
		}
		if ((this.color != null) && (this.type.toString().toLowerCase().contains("leather"))) {
			((LeatherArmorMeta)im).setColor(this.color);
		}
		stack.setItemMeta(im);
		if ((this.enchantments != null) && 
				(!this.enchantments.isEmpty())) {
			stack.addUnsafeEnchantments(this.enchantments);
		}
		
		if(effects != null){
			
			PotionMeta meta = (PotionMeta) im;
			
			for(PotionEffect effect : effects){
				meta.addCustomEffect(effect, true);
			}
			
			meta.setMainEffect(effects.get(0).getType());
			
		}
		
		/*if(glow) {
			stack = FakeGlow.apply(stack);
		}
		*/
/*		if ((this.owner != null) && (this.type.equals(Material.SKULL_ITEM))) {
			net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
			if(nms.getTag() == null)
				nms.setTag(new NBTTagCompound());
			PropertyMap map = SkinLib.getProperties(owner, CapeType.NONE);
			GameProfile profile = new GameProfile(owner, owner.toString().split("\\-")[0]);
			if(map != null)
				profile.getProperties().putAll(map);
			nms.getTag().set("SkullOwner", GameProfileSerializer.serialize(new NBTTagCompound(), profile));
			stack = CraftItemStack.asBukkitCopy(nms);
		}*/
		
		if(unbreakable){
			ItemMeta meta = stack.getItemMeta();
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
		}
		
		return stack;
	}
	
	@Override
	public Item clone() 
	{
		return new Item(this);
	}
	
	public static enum SkullType {
		SKELETON(0),
		WITHER_SKELETON(1),
		ZOMBIE(2),
		PLAYER(3),
		CREEPER(4);
	
		private final int data;
		
		SkullType(int data)
		{
			this.data = data;
		}
		
		public int getData()
		{
			return data;
		}
	}
}
