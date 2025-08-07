package org.rexi.customPack.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.rexi.customPack.CustomPack;

import java.util.List;

public class CustomPackCommand implements TabExecutor {

    private final CustomPack plugin;

    public CustomPackCommand(CustomPack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("custompack.admin")) {
            sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.no_permission", "&cYou do not have permission to use this command.")));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.config_reloaded", "&aConfiguration reloaded successfully!")));
            }
            case "version" -> {
                sender.sendMessage(Component.text("CustomPack Version: " + plugin.getDescription().getVersion()).color(NamedTextColor.YELLOW));
            }
            case "send" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("/cp send <player>").color(NamedTextColor.RED));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.send_player_offline", "&cThat player is not online.")));
                    return true;
                }

                String url = plugin.config.getString("server_pack.url", "");
                String hash = plugin.config.getString("server_pack.hash", "");

                if (url == null || url.isBlank()) {
                    sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.no_special_pack", "&cThis server does not have a special custom texture pack.")));
                    return true;
                }

                target.setResourcePack(url, hash.isBlank() ? null : hash.getBytes());
                sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.send_player", "&aYou have sent the texture pack to &e{player}&a!").replace("{player}", target.getName())));
                target.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.player_applied", "&aYou have applied the texture pack!")));
            }
            default -> sendUsage(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("CustomPack Commands:").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/cp reload - Reloads configuration").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/cp version - Shows plugin version").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/cp send <jugador> - Sends the server resourcepack to the player").color(NamedTextColor.WHITE));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "version", "send");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}