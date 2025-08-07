package org.rexi.customPack.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rexi.customPack.CustomPack;

import java.util.Set;
import java.util.UUID;

public class TexturePackCommand implements CommandExecutor {

    private final CustomPack plugin;

    public TexturePackCommand(CustomPack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.no_console", "&cOnly players can use this command")));
            return true;
        }

        if (!sender.hasPermission("custompack.texturepack")) {
            sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.no_permission", "&cYou do not have permission to use this command.")));
            return true;
        }

        if (player.getName().contains(".") && plugin.getConfig().getBoolean("geyser_players")) {
            sender.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.geyser_players", "&cBedrock players cannot use this command.")));
            return true;
        }

        String url = plugin.config.getString("server_pack.url", "");
        boolean serverpackenabled = plugin.config.getBoolean("server_pack.enabled", true);
        String hash = plugin.config.getString("server_pack.hash", "");
        boolean applyOnJoin = plugin.config.getBoolean("server_pack.apply_on_join", true);

        if (serverpackenabled) {
            if (url != null && !url.isBlank() && !applyOnJoin) {
                Set<UUID> toggled = plugin.getPlayersWithLocalPack();
                if (toggled.contains(player.getUniqueId())) {
                    toggled.remove(player.getUniqueId());
                    player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.special_pack_removed", "&aYou have removed the special texture pack!")));
                    plugin.requestGlobalPack(player, "removed special pack");
                    return true;
                } else {
                    toggled.add(player.getUniqueId());
                    player.setResourcePack(url, hash.isBlank() ? null : plugin.hexStringToByteArray(hash));
                    player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.player_applied_command", "&aYou have applied the texture pack, use the command again to disable the special texture pack!")));
                }
            } else if (applyOnJoin) {
                player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.command_block_onjoin", "&cThe texture pack is set to apply on join, you cannot apply it manually.")));
            }
        } else {
            player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.no_special_pack", "&cThis server does not have a special custom texture pack.")));
        }
        return true;
    }
}