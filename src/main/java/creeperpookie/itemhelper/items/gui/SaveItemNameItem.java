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

public class SaveItemNameItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(Material.BAMBOO_SIGN);
		item.editMeta(meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Save Name", DefaultTextColor.AQUA));
			meta.lore(List.of(Component.text("Click to save the name of the item", DefaultTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public String getName()
	{
		return "save_item_name_item";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.SAVE_ITEM_NAME_MODEL_DATA;
	}

	@Override
	public boolean isItem(ItemStack item)
	{
		ItemStack currentItem = getItemStack();
		int precision = 250; // Precision for custom model data comparison, when comparing from an anvil its value can be off by a few hundred
		return item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() > currentItem.getItemMeta().getCustomModelData() - precision && item.getItemMeta().getCustomModelData() < currentItem.getItemMeta().getCustomModelData() + precision && item.getEnchantmentLevel(Enchantment.INFINITY) == 1 && item.getItemFlags().equals(currentItem.getItemFlags());
	}

	@Override
	public boolean isItemType(@NotNull ItemType type)
	{
		CustomItem inputtedCustomItem = CustomItem.getItem(type);
		return this.equals(inputtedCustomItem);
	}

	@Override
	public boolean equals(@NotNull CustomItem item)
	{
		return item instanceof SaveItemNameItem;
	}
}
