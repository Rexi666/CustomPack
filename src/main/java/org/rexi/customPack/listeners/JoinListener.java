package org.rexi.customPack.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.rexi.customPack.CustomPack;

public class JoinListener implements Listener {

    private final CustomPack plugin;

    public JoinListener(CustomPack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().contains(".") && plugin.getConfig().getBoolean("geyser_players")) return;

        String url = plugin.getServerPackUrl();
        String hash = plugin.getServerPackHash();
        boolean applyOnJoin = plugin.isApplyOnJoinEnabled();

        if (url != null && !url.isBlank() && applyOnJoin) {
            try {
                player.setResourcePack(url, hash.isBlank() ? null : hash.getBytes());
                player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.player_applied", "&aYou have applied the texture pack!")));
            } catch (Exception e) {
                plugin.getLogger().warning("Error applying resourcepack to " + player.getName());
                e.printStackTrace();
            }
        }
    }
}
