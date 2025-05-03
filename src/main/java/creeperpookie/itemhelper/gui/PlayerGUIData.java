package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.util.Utility;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PlayerGUIData
{
	@NotNull private final ItemStack initialItem;
	@NotNull private final ArrayList<ItemStack> currentItems;
	private int guiPage;
	@Nullable GUIType parentGUIType;
	@NotNull private GUIType guiType;
	@Nullable Enchantment enchantment;
	@Nullable Attribute attribute;
	private boolean smallText;

	public PlayerGUIData(@NotNull ItemStack initialItem, @NotNull ArrayList<ItemStack> items, int guiPage, @Nullable GUIType parentGUIType, @NotNull GUIType guiType, @Nullable Enchantment enchantment, @Nullable Attribute attribute, boolean smallText)
	{
		this.initialItem = initialItem;
		this.currentItems = items;
		this.guiPage = guiPage;
		this.parentGUIType = parentGUIType;
		this.guiType = guiType;
		this.enchantment = enchantment;
		this.attribute = attribute;
		this.smallText = smallText;
	}

	@NotNull
	public ItemStack getInitialItem()
	{
		return initialItem;
	}

	public boolean hasItems()
	{
		return !currentItems.isEmpty();
	}

	public boolean hasItem(ItemStack item)
	{
		return hasItems() && currentItems.contains(item);
	}

	@NotNull
	public ArrayList<ItemStack> getCurrentItems()
	{
		return currentItems;
	}

	public void setCurrentItems(@NotNull ArrayList<ItemStack> items)
	{
		if (this.currentItems != items && !items.isEmpty())
		{
			clearCurrentItems();
			items.forEach(item ->
			{
				if (item != null && !item.isEmpty()) this.currentItems.add(item);
			});
		}
	}

	public void clearCurrentItems()
	{
		this.currentItems.clear();
	}

	public void addCurrentItem(@NotNull ItemStack item)
	{
		if (item.isEmpty()) return;
		else if (!Utility.isItemSimilar(currentItems, item)) this.currentItems.add(item);
		else
		{
			int index = Utility.getFirstSimilarIndex(currentItems, item);
			currentItems.get(index).setAmount(item.getAmount());
		}
	}

	public void removeCurrentItem(ItemStack item)
	{
		int index = Utility.getFirstSimilarIndex(currentItems, item);
		if (index > -1) this.currentItems.remove(index);
	}

	public int getGUIPage()
	{
		return guiPage;
	}

	public void setGUIPage(int guiPage)
	{
		this.guiPage = guiPage;
	}

	@Nullable
	public GUIType getParentGUIType()
	{
		return parentGUIType;
	}

	public void setParentGUIType(@Nullable GUIType parentGUIType)
	{
		this.parentGUIType = parentGUIType;
	}

	public GUIType getGUIType()
	{
		return guiType;
	}

	public void setGUIType(GUIType guiType)
	{
		this.guiType = guiType;
	}

	public boolean hasEnchantment()
	{
		return enchantment != null;
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

	public boolean hasAttribute()
	{
		return attribute != null;
	}

	@Nullable
	public Attribute getAttribute()
	{
		return attribute;
	}

	public void setAttribute(@Nullable Attribute attribute)
	{
		this.attribute = attribute;
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
