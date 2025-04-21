package creeperpookie.enchanthelper.gui;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PlayerGUIData
{
	private ItemStack item;
	private int enchantingGUIPage;
	private GUIType guiType;
	@Nullable Enchantment enchantment;
	private boolean smallText;

	public PlayerGUIData(ItemStack item, int enchantingGUIPage, GUIType guiType, @Nullable Enchantment enchantment, boolean smallText)
	{
		this.item = item == null ? null : item.clone();
		this.enchantingGUIPage = enchantingGUIPage;
		this.guiType = guiType;
		this.enchantment = enchantment;
		this.smallText = smallText;
	}

	public boolean hasItem()
	{
		return !item.isEmpty();
	}

	@Nullable
	public ItemStack getItem()
	{
		return item;
	}

	public void setItem(ItemStack item)
	{
		this.item = item;
	}

	public int getEnchantingGUIPage()
	{
		return enchantingGUIPage;
	}

	public void setEnchantingGUIPage(int enchantingGUIPage)
	{
		this.enchantingGUIPage = enchantingGUIPage;
	}

	public GUIType getGUIType()
	{
		return guiType;
	}

	public void setGUIType(GUIType guiType)
	{
		this.guiType = guiType;
	}

	@Nullable
	public Enchantment getEnchantment()
	{
		return enchantment;
	}

	public void setEnchantment(@Nullable Enchantment enchantment)
	{
		this.enchantment = enchantment;
	}

	public boolean isSmallText()
	{
		return smallText;
	}

	public void setSmallText(boolean useSmallText)
	{
		this.smallText = useSmallText;
	}
}
