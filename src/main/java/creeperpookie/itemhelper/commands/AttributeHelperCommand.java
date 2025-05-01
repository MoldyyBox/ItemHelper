package creeperpookie.itemhelper.commands;

import creeperpookie.itemhelper.ItemHelperPlugin;
import creeperpookie.itemhelper.gui.GUIType;
import creeperpookie.itemhelper.handlers.ItemListener;
import creeperpookie.itemhelper.util.DefaultTextColor;
import creeperpookie.itemhelper.util.Utility;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AttributeHelperCommand implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!(sender instanceof Player player))
		{
			sender.sendMessage("This command can only be run by a player!");
			return true;
		}
		else if (!player.isOp() && !player.hasPermission("itemhelper.attribute"))
		{
			sender.sendMessage(Component.text("You do not have permission to use this command!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
			return true;
		}
		else if (player.getInventory().getItemInMainHand().isEmpty())
		{
			player.sendActionBar(Component.text("You must be holding an item to use this command!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
			return true;
		}
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		switch (args.length)
		{
			case 0 ->
			{
				if (ItemListener.isPlayerInGUI(player)) ItemListener.clearPlayerGUIData(player);
				ItemListener.openGUI(player, GUIType.ATTRIBUTE, null, null);
				return true;
			}
			case 1 ->
			{
				return ItemHelperPlugin.getInstance().getCommand("itemhelper").getExecutor().onCommand(sender, command, label, args);
			}
			case 2 ->
			{
				Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(new NamespacedKey("minecraft", args[0]));
				if (attribute == null)
				{
					player.sendActionBar(Component.text("The inputted attribute type is not valid!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
					return true;
				}
				double level;
				try
				{
					level = Double.parseDouble(args[1]);
				}
				catch (NumberFormatException e)
				{
					player.sendActionBar(Component.text("The inputted attribute level is not a valid number!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
					return true;
				}
				heldItem.editMeta(meta ->
				{
					meta.removeAttributeModifier(attribute);
					meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey(), level, AttributeModifier.Operation.ADD_NUMBER));
				});
				player.sendActionBar(Component.text("Successfully applied attribute to", DefaultTextColor.GREEN).appendSpace().append(heldItem.displayName().color(DefaultTextColor.LIGHT_PURPLE)).decoration(TextDecoration.ITALIC, false));
			}
		}
		return true;
	}

	@Override
	@Nullable
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!sender.isOp() && !sender.hasPermission("itemhelper.attribute")) return List.of();
		switch (args.length)
		{
			case 1 ->
			{
				ArrayList<String> commands = new ArrayList<>(Utility.getEnchantmentsAsString());
				commands.add("reset");
				if (sender instanceof Player player && ItemListener.hasPersistentGUIData(player) && ItemListener.getPersistentGUIData(player).hasUnretrievedItems()) commands.add("items");
				return commands;
			}
			case 2 ->
			{
				return Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").map(s -> args[1] + s).toList();
			}
		}
		return List.of();
	}

	private void printHelp(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		if (args.length == 1) sender.sendMessage(Component.text("You must specify an attribute level!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
	}
}
