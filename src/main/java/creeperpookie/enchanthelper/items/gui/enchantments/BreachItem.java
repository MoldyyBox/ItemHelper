package creeperpookie.enchanthelper.items.gui.enchantments;

import creeperpookie.enchanthelper.items.CustomItem;
import creeperpookie.enchanthelper.items.ItemConstants;
import creeperpookie.enchanthelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreachItem implements CustomItem
{
	@Override
	@NotNull
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		item.editMeta(Damageable.class, meta ->
		{
			meta.setCustomModelData(getModelData());
			meta.setDamage(ItemConstants.DIAMOND_CHESTPLATE_MAX_DURABILITY - 1);
			meta.displayName(Component.text("Breach", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));
			meta.lore(List.of());
		});
		item.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		return item;
	}

	@Override
	@NotNull
	public String getName()
	{
		return "breach";
	}

	@Override
	public int getModelData()
	{
		return ItemConstants.BREACH_MODEL_DATA;
	}
}
