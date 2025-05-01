package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.util.Utility;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PersistentPlayerGUIData
{
	private final ArrayList<ItemStack> lastItems = new ArrayList<>();
	private ArrayList<ItemStack> unretrievedItems = new ArrayList<>();
	private LastSuccessfulAction lastSuccessfulAction = new LastSuccessfulAction();

	public ArrayList<ItemStack> getLastItems()
	{
		return lastItems;
	}

	public ArrayList<ItemStack> getUnretrievedItems()
	{
		return unretrievedItems;
	}

	public void addUnretrievedItem(ItemStack itemStack)
	{
		if (!Utility.isItemSimilar(unretrievedItems, itemStack)) unretrievedItems.add(itemStack);
	}

	public void addUnretrievedItems(ArrayList<ItemStack> items)
	{
		items.forEach(this::addUnretrievedItem);
	}

	public void setUnretrievedItems(ArrayList<ItemStack> unretrievedItems)
	{
		this.unretrievedItems = unretrievedItems;
	}

	public boolean hasUnretrievedItems()
	{
		return unretrievedItems != null && !unretrievedItems.isEmpty();
	}

	public LastSuccessfulAction getLastSuccessfulAction()
	{
		return lastSuccessfulAction;
	}

	public void setLastSuccessfulAction(LastSuccessfulAction lastSuccessfulAction)
	{
		this.lastSuccessfulAction = lastSuccessfulAction;
	}
}
