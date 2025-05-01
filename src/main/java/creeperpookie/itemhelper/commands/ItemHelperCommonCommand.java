package creeperpookie.itemhelper.commands;

import creeperpookie.itemhelper.gui.GUIType;
import creeperpookie.itemhelper.handlers.ItemListener;
import creeperpookie.itemhelper.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @implNote While this does represent the /itemhelper command, it also contains the common commands both used by /attributehelper and /enchanthelper
 */
public class ItemHelperCommonCommand implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!(sender instanceof Player player))
		{
			sender.sendMessage("This command can only be run by a player!");
			return true;
		}
		else if (!player.isOp() && !player.hasPermission("itemhelper.use"))
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
				// TODO make base GUI for all item types
				//ItemListener.openGUI(player, GUIType.ATTRIBUTE, null, null);
				return true;
			}
			case 1 ->
			{
				switch (args[0])
				{
					case "reset" ->
					{
						if (!ItemListener.isPlayerInGUI(player)) player.sendActionBar(Component.text("You aren't in the attribute GUI!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
						else
						{
							ItemListener.clearPlayerGUIData(player);
							player.sendActionBar(Component.text("Successfully reset the attribute GUI!", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
						}
					}
					case "items" ->
					{
						if (!ItemListener.hasPersistentGUIData(player) || !ItemListener.getPersistentGUIData(player).hasUnretrievedItems()) player.sendActionBar(Component.text("You don't have any unretrieved items to reclaim!"));
						else
						{
							ItemListener.openGUI(player, GUIType.STORED_ITEMS, null, null);
						}
					}
					default -> printHelp(player, command, label, args);
				}
				return true;
			}
		}
		return true;
	}

	@Override
	@Nullable
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args)
	{
		return List.of();
	}

	private void printHelp(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		if (args.length == 1) sender.sendMessage(Component.text("You must specify an attribute level!", DefaultTextColor.RED).decoration(TextDecoration.ITALIC, false));
	}
}
