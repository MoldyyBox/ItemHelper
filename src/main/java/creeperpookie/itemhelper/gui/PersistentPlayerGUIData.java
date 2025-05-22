package creeperpookie.itemhelper.gui;

import creeperpookie.itemhelper.ItemHelperPlugin;
import creeperpookie.itemhelper.util.Utility;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class PersistentPlayerGUIData
{
	private ArrayList<ItemStack> unretrievedItems = new ArrayList<>();
	private ArrayList<LastSuccessfulAction> lastSuccessfulActions = new ArrayList<>();
	private final UUID playerUUID;
	private final File playerDataFile;

	public PersistentPlayerGUIData(@NotNull UUID playerUUID)
	{
		this.playerUUID = playerUUID;
		this.playerDataFile = new File(ItemHelperPlugin.getInstance().getDataFolder(), playerUUID + ".yml");
	}

	public boolean hasUnretrievedItems()
	{
		return !unretrievedItems.isEmpty();
	}

	public ArrayList<ItemStack> getUnretrievedItems()
	{
		return unretrievedItems;
	}

	public void addUnretrievedItem(@NotNull ItemStack itemStack)
	{
		if (!itemStack.isEmpty()) unretrievedItems.add(itemStack);
	}

	public void addUnretrievedItems(ArrayList<ItemStack> items)
	{
		items.forEach(this::addUnretrievedItem);
	}

	public void clearUnretrievedItems()
	{
		unretrievedItems.clear();
	}
	public boolean hasLastSuccessfulActions()
	{
		return !lastSuccessfulActions.isEmpty();
	}


	public ArrayList<LastSuccessfulAction> getLastSuccessfulActions()
	{
		return lastSuccessfulActions;
	}

	public void addLastSuccessfulAction(LastSuccessfulAction action, int maxSize)
	{
		if (!lastSuccessfulActions.contains(action))
		{
			lastSuccessfulActions.add(action);
			if (lastSuccessfulActions.size() >= maxSize) lastSuccessfulActions.removeFirst();
		}
	}

	public void load()
	{
		if (playerDataFile == null || !playerDataFile.exists()) return;
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerDataFile);
		ConfigurationSection unretrievedItemsSection = playerConfig.getConfigurationSection("unretrieved_items");
		if (unretrievedItemsSection != null)
		{
			int count = unretrievedItemsSection.getInt("count");
			ArrayList<ItemStack> newUnretrievedItems = new ArrayList<>();
			boolean elementsExist = true;
			if (count > 0) for (int index = 0; index < count; index++)
			{
				ItemStack item = unretrievedItemsSection.getItemStack(String.valueOf(index));
				if (item != null)
				{
					newUnretrievedItems.add(item);
				}
				else elementsExist = false;
			}
			if (!elementsExist) ItemHelperPlugin.getInstance().getLogger().warning("Some unretrieved items for player " + playerUUID.toString() + " are missing in player data config!");
			else unretrievedItems = newUnretrievedItems;
		}
		ConfigurationSection historySection = playerConfig.getConfigurationSection("history");
		if (historySection != null)
		{
			int count = historySection.getInt("count");
			ArrayList<LastSuccessfulAction> newLastSuccessfulActions = new ArrayList<>();
			boolean indexSectionsExist = true;
			if (count > 0) for (int index = 0; index < count; index++)
			{
				ConfigurationSection baseSection = historySection.getConfigurationSection(String.valueOf(index));
				if (baseSection == null)
				{
					indexSectionsExist = false;
					continue;
				}
				ConfigurationSection attributesSection = baseSection.getConfigurationSection("attributes");
				newLastSuccessfulActions.add(new LastSuccessfulAction());
				if (attributesSection != null)
				{
					for (String attributeKey : attributesSection.getKeys(false))
					{
						Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(new NamespacedKey("minecraft", attributeKey));
						if (attribute == null)
						{
							ItemHelperPlugin.getInstance().getLogger().warning("Invalid attribute type " + attributeKey + "in player data config for player " + playerUUID + "!");
						}
						else newLastSuccessfulActions.get(index).addAttribute(attribute, attributesSection.getInt(attributeKey));
					}
				}
				ConfigurationSection enchantmentsSection = baseSection.getConfigurationSection("enchantments");
				if (enchantmentsSection != null)
				{
					for (String enchantmentKey : enchantmentsSection.getKeys(false))
					{
						Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(new NamespacedKey("minecraft", enchantmentKey));
						if (enchantment == null)
						{
							ItemHelperPlugin.getInstance().getLogger().warning("Invalid enchantment type " + enchantmentKey + "in player data config for player " + playerUUID + "!");
						}
						else newLastSuccessfulActions.get(index).addEnchantment(enchantment, enchantmentsSection.getInt(enchantmentKey));
					}
				}
				newLastSuccessfulActions.get(index).setSmallText(baseSection.getBoolean("small_text", true));
			}
			if (!indexSectionsExist) ItemHelperPlugin.getInstance().getLogger().warning("Saved history for player " + playerUUID + " does not have enough data for saved size " + count + "!");
			else lastSuccessfulActions = newLastSuccessfulActions;
		}
	}

	public void save()
	{
		if (playerDataFile == null) return;
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerDataFile);
		if (!unretrievedItems.isEmpty())
		{
			ConfigurationSection unretrievedItemsSection = playerConfig.getConfigurationSection("unretrieved_items") == null ? playerConfig.createSection("unretrieved_items") : playerConfig.getConfigurationSection("unretrieved_items");
			for (String indexKey : unretrievedItemsSection.getKeys(true)) unretrievedItemsSection.set(indexKey, null);
			unretrievedItemsSection.set("count", unretrievedItems.size());
			for (int index = 0; index < unretrievedItems.size(); index++)
			{
				unretrievedItemsSection.set(String.valueOf(index), unretrievedItems.get(index));
			}
		}
		if (!lastSuccessfulActions.isEmpty())
		{
			ConfigurationSection historySection = playerConfig.getConfigurationSection("history") == null ? playerConfig.createSection("history") : playerConfig.getConfigurationSection("history");
			for (String indexKey : historySection.getKeys(true)) historySection.set(indexKey, null);
			historySection.set("count", lastSuccessfulActions.size());
			for (int index = 0; index < lastSuccessfulActions.size(); index++)
			{
				LastSuccessfulAction action = lastSuccessfulActions.get(index);
				ConfigurationSection indexSection = historySection.getConfigurationSection(String.valueOf(index)) == null ? historySection.createSection(String.valueOf(index)) : historySection.getConfigurationSection(String.valueOf(index));
				ConfigurationSection attributesSection = indexSection.getConfigurationSection("attributes") == null ? indexSection.createSection("attributes") : indexSection.getConfigurationSection("attributes");
				action.getAttributes().forEach(attributeData -> attributesSection.set(attributeData.getLeft().getKey().getKey().toLowerCase(), attributeData.getRight()));
				ConfigurationSection enchantmentsSection = indexSection.getConfigurationSection("enchantments") == null ? indexSection.createSection("enchantments") : indexSection.getConfigurationSection("enchantments");
				action.getEnchantments().forEach(enchantmentData -> enchantmentsSection.set(enchantmentData.getLeft().getKey().getKey().toLowerCase(), enchantmentData.getRight()));
				indexSection.set("small_text", action.isSmallText());
			}
		}
		try
		{
			playerConfig.save(playerDataFile);
		}
		catch (IOException e)
		{
			ItemHelperPlugin.getInstance().getLogger().severe("Could not save player data config for player " + playerUUID + ":");
			Utility.printException(ItemHelperPlugin.getInstance().getLogger(), e);
		}
	}
}
