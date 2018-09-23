package net.kaikk.mc.itemrestrict.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandExec implements CommandExecutor {
	private BetterItemRestrict instance;

	CommandExec(BetterItemRestrict instance) {
		this.instance = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("betteritemrestrict")) {
			if (!sender.hasPermission("betteritemrestrict.manage")) {
				sender.sendMessage(ChatColor.RED+"Permission denied");
				return false;
			}

			if (args.length==0) {
				sender.sendMessage("Usage: /"+label+" [<reload/addHand>] [exact]");
				return false;
			}

			switch(args[0].toLowerCase()) {
				case "reload": {
					Bukkit.getPluginManager().disablePlugin(instance);
					Bukkit.getPluginManager().enablePlugin(instance);
					sender.sendMessage("Plugin reloaded.");
					break;
				}
				case "addhand": {

					if ( !(sender instanceof Player)){
						sender.sendMessage("Apenas jogadores físicos podem usar esse comando.");
						return true;
					}

					Player player = (Player) sender;

					if (player.getItemInHand() == null){
						sender.sendMessage("§cVocê precisa estar segurando um item!");
						return true;
					}

					ItemStack itemStack = player.getItemInHand();

					String itemBukktiName = itemStack.getType().name();
					int itemDamage = itemStack.getDurability();

					String resultString;
					if (itemDamage > 0 || (args.length > 1 && args[1].equalsIgnoreCase("exact")) ){
						sender.sendMessage("§a§l(+)§r§2 " + itemBukktiName + ":" + itemDamage);
						resultString = itemBukktiName + "," + itemDamage + "|" + itemBukktiName + "|" + "Item banido";
					}else {
						resultString = itemBukktiName + "|" + itemBukktiName + "|" + "Item banido";
						sender.sendMessage("§a§l(+)§r§2 " + itemBukktiName);
					}


					List<String> list = instance.getConfig().getStringList("Ownership");
					if (!list.contains(resultString)){
						list.add(resultString);
						instance.getConfig().set("Ownership",list);
						instance.saveConfig();
					}
					break;
				}
				default:
					sender.sendMessage("Wrong parameter "+args[0]);
					break;
			}
			return true;
		} else if (cmd.getName().equals("banneditems")) {
			if (!sender.hasPermission("betteritemrestrict.list")) {
				sender.sendMessage(ChatColor.RED+"Permission denied");
				return false;
			}

			sender.sendMessage(ChatColor.GOLD+"--- BetterItemRestrict Banned Items List ---");

			StringBuilder sb = new StringBuilder();
			boolean sw = true;
			for (RestrictedItem ri : instance.config().ownership.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
					sb.append(", ");
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Ownership: "+sb.toString());

			sb.setLength(0);
			sw = true;
			for (RestrictedItem ri : instance.config().usage.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
					sb.append(", ");
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Use/Place: "+sb.toString());
			return true;
		}
		return false;
	}
}
