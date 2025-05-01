package creeperpookie.itemhelper.items.gui;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.items.ItemType;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurrentItemsItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack(int data)
	{
		ItemStack item = new ItemStack(Material.CHEST);
		item.editMeta(meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text(data, DefaultTextColor.BLUE).appendSpace().append(Component.text("Item", DefaultTextColor.AQUA)).append(Component.text(data == 1 ? "" : "s", DefaultTextColor.AQUA)).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
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
		return getItemStack(1);
	}

	@Override
	@NotNull
	public String getName()
	{
		return "current_items";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.CURRENT_ITEMS_MODEL_DATA;
	}

	@Override
	public boolean isItem(ItemStack item)
	{
		ItemStack currentItem = getItemStack();
		return item != null && item.getType() == currentItem.getType() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == currentItem.getItemMeta().getCustomModelData() && item.getEnchantmentLevel(Enchantment.INFINITY) == 1 && item.getItemFlags().equals(currentItem.getItemFlags());
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
		return item instanceof CurrentItemsItem;
	}
}
