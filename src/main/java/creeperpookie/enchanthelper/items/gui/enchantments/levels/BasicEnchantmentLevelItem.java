package creeperpookie.enchanthelper.items.gui.enchantments.levels;

import creeperpookie.enchanthelper.items.CustomItem;
import creeperpookie.enchanthelper.items.ItemConstants;
import creeperpookie.enchanthelper.items.ItemType;
import creeperpookie.enchanthelper.util.DefaultTextColor;
import creeperpookie.enchanthelper.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BasicEnchantmentLevelItem implements CustomItem
{
	@Override
	public ItemStack getItemStack(int data)
	{
		if (data < 0 || data > 15) throw new IllegalArgumentException("Light level must be between 0 and 15");
		ItemStack item = new ItemStack(Material.LIGHT);
		item.editMeta(BlockDataMeta.class, meta ->
		{
			if (data < 15) meta.setBlockData(Material.LIGHT.createBlockData("[level=" + data + ",waterlogged=false]"));
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Level", DefaultTextColor.AQUA).appendSpace().append(Component.text(Utility.getEnchantingLevel(data)).color(DefaultTextColor.DARK_AQUA)).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		return getItemStack(0);
	}

	@Override
	@NotNull
	public String getName()
	{
		return "basic_enchantment_level";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.BASIC_ENCHANTMENT_LEVEL_MODEL_DATA;
	}

	@Override
	public boolean isItem(ItemStack item)
	{
		ItemStack currentItem = getItemStack();
		return item.getType() == currentItem.getType() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == currentItem.getItemMeta().getCustomModelData() && item.getEnchantmentLevel(Enchantment.INFINITY) == 1 && item.getItemFlags().equals(currentItem.getItemFlags());
	}

	@Override
	public boolean isItemType(@NotNull ItemType type)
	{
		CustomItem inputtedCustomItem = CustomItem.getItem(type);
		return inputtedCustomItem != null && this.equals(inputtedCustomItem);
	}

	@Override
	public boolean equals(@NotNull CustomItem item)
	{
		return item instanceof BasicEnchantmentLevelItem;
	}
}
