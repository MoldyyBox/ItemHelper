package creeperpookie.itemhelper.items.gui.attributes;

import creeperpookie.itemhelper.items.CustomItem;
import creeperpookie.itemhelper.items.ItemConstants;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SafeFallDistanceAttributeItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		item.editMeta(EnchantmentStorageMeta.class, meta ->
		{
			meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 4, true);
			meta.setCustomModelData(getModelData());
			meta.displayName(Component.text("Safe Fall Distance", DefaultTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public String getName()
	{
		return "safe_fall_distance_attribute";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.SAFE_FALL_DISTANCE_ATTRIBUTE_MODEL_DATA;
	}
}
