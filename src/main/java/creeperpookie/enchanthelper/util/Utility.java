package creeperpookie.enchanthelper.util;

import creeperpookie.enchanthelper.ItemHelperPlugin;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utility
{
	private static final char[] SMALL_CAPS_CHAR_ARRAY = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".toCharArray(); // s and x are normal characters for some reason

	public static String formatText(String message)
	{
		StringBuilder newMessage = new StringBuilder();
		String[] itemTypeSegments = message.split("_");
		for (int i = 0; i < itemTypeSegments.length; i++)
		{
			if (itemTypeSegments[i].equalsIgnoreCase("to") || itemTypeSegments[i].equalsIgnoreCase("and") || itemTypeSegments[i].equalsIgnoreCase("or") || itemTypeSegments[i].equalsIgnoreCase("a")) itemTypeSegments[i] = itemTypeSegments[i].toLowerCase();
			else itemTypeSegments[i] = itemTypeSegments[i].substring(0, 1).toUpperCase() + itemTypeSegments[i].substring(1).toLowerCase();
			newMessage.append(itemTypeSegments[i]);
			if (i < itemTypeSegments.length - 1) newMessage.append(" ");
		}
		return newMessage.toString();
	}

	public static String getSmallCapsString(String input, boolean preserveCaps)
	{
		StringBuilder output = new StringBuilder();
		for (char $char : input.toCharArray())
		{
			if (!(($char >= 'a' && $char <= 'z') || ($char >= 'A' && $char <= 'Z'))) output.append($char);
			else
			{
				if (!preserveCaps && ($char >= 'A' && $char <= 'Z')) output.append(SMALL_CAPS_CHAR_ARRAY[$char - 'A']);
				else output.append(SMALL_CAPS_CHAR_ARRAY[$char - ($char >= 'a' ? 'a' : 'A')]);
			}
		}
		return output.toString();
	}

	public static <T> int arrayIndexOf(T[] array, T value)
	{
		int index = -1;
		for (T arrayValue : array)
		{
			index++;
			if ((arrayValue == null && value == null) || (arrayValue != null && arrayValue.equals(value))) return index;
		}
		return -1;
	}

	public static int arrayIndexOfSubstring(String[] array, String value)
	{
		int index = -1;
		for (String arrayValue : array)
		{
			index++;
			if (arrayValue.contains(value)) return index;
		}
		return -1;
	}

	public static <T> boolean arrayContains(T[] array, T value)
	{
		for (T arrayValue : array)
		{
			if (array instanceof String[] && value instanceof String && ((String) arrayValue).equalsIgnoreCase((String) value))
			{
				return true;
			}
			else if (arrayValue.equals(value))
			{
				return true;
			}
		}
		return false;
	}

	public static String locationAsString(Location location)
	{
		return locationAsString(location, true, true);
	}

	public static String locationAsString(Location location, boolean asBlock, boolean withWorldName)
	{
		String output = "";
		if (withWorldName)
		{
			output += location.getWorld().getName() + " ";
		}
		if (asBlock)
		{
			output += location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
		}
		else
		{
			output += location.getX() + " " + location.getY() + " " + location.getZ();
		}
		return output;
	}

	public static Location stringAsLocation(World world, String location) // valid formats: "world, x, y, z"; "world,x,y,z"; "world x y z" |
	{
		if (world == null || location == null)
		{
			return null;
		}
		String[] type1 = location.split(", ");
		if (type1.length != 3)
		{
			String[] type2 = location.split(",");
			if (type2.length != 3)
			{
				String[] type3 = location.split(" ");
				if (type3.length != 3)
				{
					return null;
				}
				double x, y, z;
				try
				{
					x = Double.parseDouble(type3[0]);
					y = Double.parseDouble(type3[1]);
					z = Double.parseDouble(type3[2]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				return new Location(world, x, y, z);
			}
			else
			{
				double x, y, z;
				try
				{
					x = Double.parseDouble(type2[0]);
					y = Double.parseDouble(type2[1]);
					z = Double.parseDouble(type2[2]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				return new Location(world, x, y, z);
			}
		}
		else
		{
			double x, y, z;
			try
			{
				x = Double.parseDouble(type1[0]);
				y = Double.parseDouble(type1[1]);
				z = Double.parseDouble(type1[2]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			return new Location(world, x, y, z);
		}
	}

	public static ItemStack stringAsItemStack(String itemStack)
	{
		String materialName = itemStack.split(", ")[0];
		if (materialName.equals(itemStack))
		{
			materialName = itemStack.split(",")[0];
			if (materialName.equals(itemStack))
			{
				materialName = itemStack.split(" ")[0];
				if (materialName.equals(itemStack))
				{
					throw new IllegalArgumentException("String " + itemStack + " is not a valid material");
				}
			}
		}
		return stringAsItemStack(itemStack, formatText(materialName));
	}
	
	public static ItemStack stringAsItemStack(String itemStack, String name)
	{
		return stringAsItemStack(itemStack, name, new ArrayList<>());
	}

	public static ItemStack stringAsItemStack(String itemStack, String name, ArrayList<Component> lore) // valid formats: "material, count"; "material,count"; "material count"
	{
		if (itemStack == null)
		{
			return null;
		}
		String[] type1 = itemStack.split(", ");
		if (type1.length != 2)
		{
			String[] type2 = itemStack.split(",");
			if (type2.length != 2)
			{
				String[] type3 = itemStack.split(" ");
				if (type3.length != 2)
				{
					return null;
				}
				Material material = Material.matchMaterial(type3[0]);
				if (material == null)
				{
					return null;
				}
				int count;
				try
				{
					count = Integer.parseInt(type3[1]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				ItemStack item = new ItemStack(material, count);
				item.editMeta(meta ->
				{
					meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
					meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
				});
				if (!lore.isEmpty()) item.lore(lore);
				return item;
			}
			else
			{
				Material material = Material.matchMaterial(type2[0]);
				if (material == null)
				{
					return null;
				}
				int count;
				try
				{
					count = Integer.parseInt(type2[1]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				ItemStack item = new ItemStack(material, count);
				item.editMeta(meta -> meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false)));
				if (!lore.isEmpty()) item.lore(lore);
				item.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
				return item;
			}
		}
		else
		{
			Material material = Material.matchMaterial(type1[0]);
			if (material == null)
			{
				return null;
			}
			int count;
			try
			{
				count = Integer.parseInt(type1[1]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			ItemStack item = new ItemStack(material, count);
			item.editMeta(meta -> meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false)));
			if (!lore.isEmpty()) item.lore(lore);
			item.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
			return item;
		}
	}

	public static PotionEffect stringAsPotion(String potion) // valid formats: "type, duration, amplifier"; "type,duration,amplifier"; "type duration amplifier";;
	{
		if (potion == null)
		{
			return null;
		}
		String[] type1 = potion.split(", ");
		if (type1.length != 3)
		{
			String[] type2 = potion.split(",");
			if (type2.length != 3)
			{
				String[] type3 = potion.split(" ");
				if (type3.length != 3)
				{
					return null;
				}
				PotionEffectType potionEffectType = PotionEffectType.getByName(type3[0]);
				if (potionEffectType == null)
				{
					return null;
				}
				int duration, amplifier;
				try
				{
					duration = Integer.parseInt(type3[1]);
					amplifier = Integer.parseInt(type3[2]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				return potionEffectType.createEffect(duration, amplifier);
			}
			else
			{
				PotionEffectType potionEffectType = PotionEffectType.getByName(type2[0]);
				if (potionEffectType == null)
				{
					return null;
				}
				int duration, amplifier;
				try
				{
					duration = Integer.parseInt(type2[1]);
					amplifier = Integer.parseInt(type2[2]);
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				return potionEffectType.createEffect(duration, amplifier);
			}
		}
		else
		{
			PotionEffectType potionEffectType = PotionEffectType.getByName(type1[0]);
			if (potionEffectType == null)
			{
				return null;
			}
			int duration, amplifier;
			try
			{
				duration = Integer.parseInt(type1[1]);
				amplifier = Integer.parseInt(type1[2]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
			return potionEffectType.createEffect(duration, amplifier);
		}
	}

	public static ItemStack[] getItemsFromRecipe(ShapedRecipe recipe)
	{
		ArrayList<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			if (i >= recipe.getShape().length)
			{
				for (int j = 0; j < 3; j++)
				{
					items.add(new ItemStack(Material.AIR));
				}
				continue;
			}
			for (int j = 0; j < recipe.getShape()[i].length(); j++)
			{
				items.add(recipe.getIngredientMap().get(recipe.getShape()[i].charAt(j)));
			}
		}
		return items.toArray(ItemStack[]::new);
	}

	public static BlockFace vectorAsBlockFace(Vector vector)
	{
		BlockFace[] values =
		{
				BlockFace.NORTH,
				BlockFace.EAST,
				BlockFace.WEST,
				BlockFace.SOUTH
		};
		double[] value = {-vector.getZ(), vector.getX(), -vector.getX(), vector.getZ()};
		int index = 0;
		double max = value[0];
		for (int i = 1; i < value.length; i++)
		{
			if (value[i] > max)
			{
				max = value[i];
				index = i;
			}
		}
		return values[index];
	}

	public static boolean isPassenger(Player player)
	{
		return player.getWorld().getEntities().stream().anyMatch(entity -> entity != player && entity.getPassengers().contains(player));
	}

	public static boolean isPlayerCritAttacking(Player player)
	{
		return player.getFallDistance() > 0
				&& player.getLocation().toBlockLocation().getBlock().getType() != Material.LADDER
				&& !player.getLocation().toBlockLocation().getBlock().getType().name().contains("VINE")
				&& !player.isInWater()
				&& !player.hasPotionEffect(PotionEffectType.BLINDNESS)
				&& !player.hasPotionEffect(PotionEffectType.SLOW_FALLING)
				&& !isPassenger(player)
				&& !player.isFlying()
				&& player.getAttackCooldown() >= 0.9;
	}

	public static double getAttackDamage(ItemStack item, LivingEntity target, Collection<PotionEffect> activePotionEffects, boolean isCriticalHit)
	{
		double finalDamage = switch (item.getType())
		{
			case WOODEN_PICKAXE -> 2;
			case WOODEN_AXE, DIAMOND_SWORD -> 7;
			case WOODEN_SWORD, IRON_PICKAXE -> 4;
			case WOODEN_SHOVEL -> 2.5;
			case STONE_PICKAXE -> 3;
			case STONE_AXE, IRON_AXE, DIAMOND_AXE, TRIDENT -> 9;
			case STONE_SWORD, DIAMOND_PICKAXE -> 5;
			case STONE_SHOVEL -> 3.5;
			case IRON_SWORD, NETHERITE_PICKAXE -> 6;
			case IRON_SHOVEL -> 4.5;
			case DIAMOND_SHOVEL -> 5.5;
			case NETHERITE_AXE -> 10;
			case NETHERITE_SWORD -> 8;
			case NETHERITE_SHOVEL -> 6.5;
			default -> 1;
		};
		for (PotionEffect potionEffect : activePotionEffects)
		{
			PotionEffectType potionEffectType = potionEffect.getType();
			if (potionEffectType.equals(PotionEffectType.STRENGTH))
			{
				if (potionEffect.getAmplifier() < 0)
				{
					return 0;
				}
				finalDamage += (3 * (potionEffect.getAmplifier() + 1));
			}
			if (potionEffectType.equals(PotionEffectType.WEAKNESS))
			{
				if (potionEffect.getAmplifier() < 0)
				{
					return 0;
				}
				finalDamage -= (4 * (potionEffect.getAmplifier() + 1));
			}
		}
		if (isCriticalHit)
		{
			finalDamage *= 1.5;
		}
		for (Enchantment enchantment : item.getEnchantments().keySet())
		{
			int level = item.getEnchantmentLevel(enchantment);
			if (enchantment.equals(Enchantment.FIRE_ASPECT))
			{
				Bukkit.getScheduler().runTaskLater(ItemHelperPlugin.getInstance(), () -> target.setFireTicks((80 * level) - 20), 20);
			}
			if (enchantment.equals(Enchantment.SHARPNESS))
			{
				finalDamage += (0.5 * Math.max(0, level - 1)) + 1.0;
			}
			// TODO fix deprecation for entity category
			if ((enchantment.equals(Enchantment.SMITE) && target.getCategory() == EntityCategory.UNDEAD) || (enchantment.equals(Enchantment.BANE_OF_ARTHROPODS) && target.getCategory() == EntityCategory.ARTHROPOD))
			{
				finalDamage += level * 2.5;
			}
		}
		return finalDamage;
	}

	public static String formatArray(Object[] objects, boolean lowercase)
	{
		if (objects.length == 1)
		{
			return objects[0].toString();
		}
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < objects.length; i++)
		{
			list.append(lowercase ? objects[i].toString().toLowerCase() : objects[i].toString());
			if (i == 0 && objects.length == 2)
			{
				list.append(" and ");
			}
			else if (i < objects.length - 2)
			{
				list.append(", ");
			}
			else if (i == objects.length - 2)
			{
				list.append(", and ");
			}
		}
		return list.toString();
	}
	
	public static String formatList(List<String> strings, boolean lowercase)
	{
		if (strings.size() == 1)
		{
			return strings.getFirst();
		}
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < strings.size(); i++)
		{
			list.append(lowercase ? strings.get(i).toLowerCase() : strings.get(i));
			if (i == 0 && strings.size() == 2)
			{
				list.append(" and ");
			}
			else if (i < strings.size() - 2)
			{
				list.append(", ");
			}
			else if (i == strings.size() - 2)
			{
				list.append(", and ");
			}
		}
		return list.toString();
	}

	public static void broadcast(String message)
	{
		Bukkit.broadcast(Component.text(message, DefaultTextColor.WHITE));
	}

	public static void sendError(CommandSender commandSender, String message)
	{
		commandSender.sendMessage(Component.text(message, DefaultTextColor.RED));
		if (commandSender instanceof Player player) player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, (float) (0.1 + ((ItemHelperPlugin.getRandom().nextInt(2) == 0 ? -1 : 1) * (ItemHelperPlugin.getRandom().nextDouble() / 20))));
	}

	public static void sendFeedback(CommandSender commandSender, String message)
	{
		commandSender.sendMessage(Component.text(message, DefaultTextColor.WHITE));
	}

	public static String[] getGamemodes()
	{
		String[] gamemodes = new String[GameMode.values().length];
		for (int i = 0; i < gamemodes.length; i++) gamemodes[i] = GameMode.values()[i].name().toLowerCase();
		return gamemodes;
	}
	
	@NotNull
	public static BlockFace yawAsBlockFace(float yaw)
	{
		if (((yaw >= 157.5 && yaw <= 180) || (yaw > -180 && yaw < -157.5))) return BlockFace.NORTH;
		else if (yaw >= -157.5 && yaw < -112.5) return BlockFace.NORTH_EAST;
		else if (yaw >= -112.5 && yaw < -67.5) return BlockFace.EAST;
		else if (yaw >= -67.5 && yaw < -22.5) return BlockFace.SOUTH_EAST;
		else if ((yaw >= -22.5 && yaw <= 0) || (yaw > 0 || yaw < 22.5)) return BlockFace.SOUTH;
		else if (yaw >= 22.5 && yaw <= 67.5) return BlockFace.SOUTH_WEST;
		else if (yaw >= 67.5 && yaw < 122.5) return BlockFace.WEST;
		else if (yaw >= 122.5 && yaw < 157.5) return BlockFace.NORTH_WEST;
		else throw new IllegalArgumentException("provided yaw " + yaw + " cannot be converted to any valid BlockFace");
	}
	
	@NotNull
	public static Vector getDirection(float yaw, float pitch)
	{
		Vector vector = new Vector();
		float adjustedYaw = yaw + 90;
		float adjustedPitch = pitch + 90;
		vector.setX(Math.sin(Math.toRadians(adjustedPitch)) * Math.cos(Math.toRadians(adjustedYaw)));
		vector.setY(Math.cos(Math.toRadians(adjustedPitch)));
		vector.setZ(Math.sin(Math.toRadians(adjustedPitch)) * Math.sin(Math.toRadians(adjustedYaw)));
		return vector;
	}
	
	public static boolean isArrowNextToBlock(Arrow arrow)
	{
		Block currentBlock = arrow.getLocation().getBlock();
		if (!currentBlock.isEmpty() && (currentBlock.isSolid() || currentBlock.isLiquid())) return true;
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					Block relativeBlock = currentBlock.getRelative(x, y, z);
					if (!currentBlock.equals(relativeBlock) && !relativeBlock.isEmpty() && relativeBlock.isSolid() || !relativeBlock.isEmpty() && relativeBlock.isLiquid() && y <= 0) return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determines if a partially (or fully) completed string matches a comparison string.
	 *
	 * @param string The string to check
	 * @param comparator The string to check against
	 *
	 * @return if the provided string matches the comparator
	 */
	public static boolean matches(@NotNull String string, @NotNull String comparator)
	{
		return comparator.startsWith(string) || comparator.contains(string);
	}

	/**
	 * Gets a Component as a pure string.
	 *
	 * @param text the component to convert
	 * @return a String containing the contents of the component (without formatting)
	 */
	public static String getComponentAsString(Component text)
	{
		return PlainTextComponentSerializer.plainText().serialize(text);
	}
	
	/**
	 * Converts a component into a string, converting any special formatting into the <code>&(formatting code)</code> format (or section symbol if requested)
	 *
	 * @param component The component to translate
	 * @param useSectionSymbol Whether to use the section symbol or the ampersand symbol
	 *
	 * @return the escaped component as a string
	 */
	@NotNull
	public static String getColorEscapedString(@NotNull Component component, boolean useSectionSymbol)
	{
		return useSectionSymbol ? LegacyComponentSerializer.legacySection().serialize(component) : LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	/**
	 * Returns a String list of all vanilla enchantments.
	 *
	 * @return a String List of all enchantments
	 */
	@NotNull
	public static List<String> getEnchantmentsAsString()
	{
		return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).stream().map(enchantment -> enchantment.getKey().value()).toList();
	}

	/**
	 * Creates a copy of the given inventory.
	 *
	 * @param inventory The inventory to copy
	 * @param newTitle The title for the new inventory
	 * @return A new inventory with all of its items copied and placed in a new inventory
	 */
	@NotNull
	public static Inventory copyInventory(Inventory inventory, Component newTitle)
	{
		Inventory guiCopy = Bukkit.createInventory(null, inventory.getSize(), newTitle);
		for (int index = 0; index < inventory.getSize(); index++)
		{
			ItemStack item = inventory.getItem(index);
			guiCopy.setItem(index, item == null ? ItemStack.empty() : item.clone());
		}
		return guiCopy;
	}

	/**
	 * Replaces the contents of the base inventory with the new inventory.
	 *
	 * @param base The base inventory to replace
	 * @param newGUI The new inventory to replace the base with
	 */
	public static void replaceGUI(@NotNull Inventory base, @NotNull Inventory newGUI)
	{
		if (base.getSize() != newGUI.getSize()) throw new IllegalArgumentException("Tried to replace an GUI with a different size.");
		else for (int index = 0; index < newGUI.getSize(); index++)
		{
			base.setItem(index, newGUI.getItem(index));
		}
	}

	/**
	 * Gets the enchanting level based on the light level.
	 *
	 * @param lightLevel The light level to check
	 * @return The enchanting level
	 */
	public static int getEnchantingLevel(int lightLevel)
	{
		// https://www.desmos.com/calculator/gm01dyvyzi
		ItemHelperPlugin.getInstance().getLogger().info("Calculating enchanting level for light level " + lightLevel);
		if (lightLevel < 0) return 1;
		return switch (lightLevel)
		{
			case 0, 1 -> 1;
			case 2, 3 -> 2;
			case 4 -> 3;
			case 5 -> 5;
			case 6 -> 7;
			case 7 -> 10;
			case 8 -> 13;
			case 9 -> 18;
			case 10 -> 25;
			case 11 -> 50;
			case 12 -> 100;
			case 13 -> 150;
			case 14 -> 200;
			default -> 255;
		};
	}
}
