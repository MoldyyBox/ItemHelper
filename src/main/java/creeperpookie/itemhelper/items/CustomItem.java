package creeperpookie.itemhelper.items;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public interface CustomItem
{
	HashMap<Class<? extends CustomItem>, CustomItem> registeredItems = new HashMap<>();

	default boolean isItem(ItemStack item)
	{
		if (item == null) return false;
		else return getItemStack().isSimilar(item);
	}

	default ItemStack getItemStack(int data)
	{
		return getItemStack();
	}
	@NotNull ItemStack getItemStack();
	@NotNull String getName();
	int getModelData();

	default boolean hasLinkedEnchantment()
	{
		return getLinkedEnchantment() != null && (getLinkedAttribute() == null || getName().equals("luck"));
	}

	default String getLinkedEnchantmentName()
	{
		return getName();
	}

	@Nullable
	default Enchantment getLinkedEnchantment()
	{
		return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(new NamespacedKey("minecraft", getLinkedEnchantmentName()));
	}

	default boolean hasLinkedAttribute()
	{
		return (getLinkedEnchantment() == null || getName().equals("luck")) && getLinkedAttribute() != null;
	}

	default String getLinkedAttributeName()
	{
		return getName();
	}

	@Nullable
	default Attribute getLinkedAttribute()
	{
		return RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(new NamespacedKey("minecraft", getLinkedAttributeName().endsWith("_attribute") ? getLinkedAttributeName().substring(0, getLinkedAttributeName().length() - 10) : getLinkedAttributeName()));
	}

	default boolean isItemType(@NotNull ItemType type)
	{
		return getItem(type).isItem(this.getItemStack());
	}

	default boolean equals(@NotNull CustomItem item)
	{
		return getItemStack().isSimilar(item.getItemStack()) && getName().equalsIgnoreCase(item.getName()) && getModelData() == item.getModelData() && hasLinkedEnchantment() == item.hasLinkedEnchantment() && getLinkedEnchantment() == item.getLinkedEnchantment();
	}
	
	static boolean isCustomItem(@NotNull ItemStack item)
	{
		return registeredItems.values().stream().anyMatch(customItem -> customItem.isItem(item));
	}

	static boolean isCustomItem(@NotNull ItemStack item, ItemType type)
	{
		return getItem(type).isItem(item);
		//return item.isSimilar(customItem.getItemStack());
	}
	
	static boolean isCustomItem(ItemStack item, Class<? extends CustomItem> $class)
	{
		return registeredItems.get($class).isItem(item);
	}
	
	static boolean isCustomItem(ItemStack item, String customItemName)
	{
		return getItem(customItemName) != null && getItem(customItemName).isItem(item);
	}
	
	static boolean isCustomItem(String customItemName)
	{
		return registeredItems.values().stream().anyMatch(customItem -> customItem.getName().equalsIgnoreCase(customItemName));
	}
	
	static boolean isCustomItem(String customItemName, ItemType type)
	{
		return isCustomItem(customItemName) && getItem(customItemName).isItemType(type);
	}

	static boolean isCustomItem(@NotNull CustomItem customItem, @NotNull ItemType itemType)
	{
		return customItem.isItemType(itemType);
	}

	static boolean hasCustomItem(Inventory inventory)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item)) return true;
		}
		return false;
	}

	static boolean hasCustomItem(Inventory inventory, ItemType type)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, type)) return true;
		}
		return false;
	}

	static boolean hasCustomItem(Inventory inventory, Class<? extends CustomItem> $class)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, $class)) return true;
		}
		return false;
	}
	
	static boolean hasCustomItem(Inventory inventory, String customItemName)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, customItemName)) return true;
		}
		return false;
	}

	@NotNull
	static CustomItem getItem(@NotNull ItemType type)
	{
		// Custom items are registered by iterating through ItemType(s), so this can't return null
		return Objects.requireNonNull(getItem(type.getItemClass()));
	}
	
	@Nullable
	static CustomItem getItem(String customItemName)
	{
		return registeredItems.values().stream().filter(customItem -> customItem.getName().equalsIgnoreCase(customItemName)).findFirst().orElse(null);
	}

	@Nullable
	static CustomItem getItem(Class<? extends CustomItem> $class)
	{
		return registeredItems.get($class);
	}

	@Nullable
	static CustomItem getItem(ItemStack item)
	{
		return registeredItems.values().stream().filter(customItem -> customItem.isItem(item)).findFirst().orElse(null);
	}

	static CustomItem[] getCustomItems()
	{
		return registeredItems.values().toArray(new CustomItem[0]);
	}

	static int getRegisteredItemCount()
	{
		return registeredItems.size();
	}

	static void registerAll()
	{
		for (ItemType type : ItemType.values())
		{
			if (type.getItemClass() != null)
			{
				try
				{
					registerItem(type.getItemClass().getDeclaredConstructor().newInstance());
				}
				catch (ReflectiveOperationException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		/*
		registerItem(new BlankSlotItem());
		registerItem(new BackButtonItem());
		registerItem(new NextPageItem());
		registerItem(new PreviousPageItem());
		registerItem(new BasicEnchantmentLevelItem());
		registerItem(new AquaAffinityItem());
		registerItem(new BaneOfArthropodsItem());
		registerItem(new BlastProtectionItem());
		registerItem(new BreachItem());
		registerItem(new ChannelingItem());
		registerItem(new CurseOfBindingItem());
		registerItem(new CurseOfVanishingItem());
		registerItem(new DensityItem());
		registerItem(new DepthStriderItem());
		registerItem(new EfficiencyItem());
		registerItem(new FeatherFallingItem());
		registerItem(new FireAspectItem());
		registerItem(new FireProtectionItem());
		registerItem(new FlameItem());
		registerItem(new FortuneItem());
		registerItem(new FrostWalkerItem());
		registerItem(new ImpalingItem());
		registerItem(new InfinityItem());
		registerItem(new KnockbackItem());
		registerItem(new LootingItem());
		registerItem(new LoyaltyItem());
		registerItem(new LuckOfTheSeaItem());
		registerItem(new LureItem());
		registerItem(new MendingItem());
		registerItem(new MultishotItem());
		registerItem(new PiercingItem());
		registerItem(new PowerItem());
		registerItem(new ProjectileProtectionItem());
		registerItem(new ProtectionItem());
		registerItem(new PunchItem());
		registerItem(new QuickChargeItem());
		registerItem(new RespirationItem());
		registerItem(new RiptideItem());
		registerItem(new SharpnessItem());
		registerItem(new SilkTouchItem());
		registerItem(new SmiteItem());
		registerItem(new SoulSpeedItem());
		registerItem(new SweepingEdgeItem());
		registerItem(new SwiftSneakItem());
		registerItem(new ThornsItem());
		registerItem(new UnbreakingItem());
		registerItem(new WindBurstItem());
		*/
	}

	static void registerItem(CustomItem customItem)
	{
		if (registeredItems.containsKey(customItem.getClass())) return;
		else if (isCustomItem(customItem.getName())) throw new IllegalArgumentException("Custom item implementation name ids must be unique: " + customItem.getName() + " in " + customItem.getClass().getName());
		registeredItems.put(customItem.getClass(), customItem);
	}
}