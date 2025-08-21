package org.rexi.customPack.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.rexi.customPack.CustomPack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerJoin implements Listener {

    private final CustomPack plugin;
    private final Set<UUID> protectedPlayers = new HashSet<>();

    public PlayerJoin(CustomPack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().contains(".") && plugin.getConfig().getBoolean("geyser_players")) return;

        if (plugin.config.getBoolean("server_pack.enabled") && plugin.config.getBoolean("server_pack.apply_on_join")) {
            String url = plugin.config.getString("server_pack.url", "");
            String hash = plugin.config.getString("server_pack.hash", "");
            protectedPlayers.add(player.getUniqueId());
            player.setResourcePack(url, hash.isBlank() ? null : plugin.hexStringToByteArray(hash));
            player.sendMessage(plugin.deserialize(plugin.getConfig().getString("messages.player_applied", "&aYou have applied the texture pack!")));
        }
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED:
                // Quitar protección
                protectedPlayers.remove(player.getUniqueId());
                break;
            case DECLINED:
                if (plugin.config.getBoolean("kick_if_declined", true)) {
                    player.kick(plugin.deserialize(plugin.config.getString("messages.decline_texturepack", "&cYou have declined the texture pack request. Enable it if you want to play on this server.")));
                } else {
                    player.sendMessage(plugin.deserialize(plugin.config.getString("messages.decline_texturepack", "&cYou have declined the texture pack request. Enable it if you want to play on this server.")));
                }
            case FAILED_DOWNLOAD:
                if (plugin.config.getBoolean("kick_if_failed", true)) {
                    player.kick(plugin.deserialize(plugin.config.getString("messages.failed_texturepack", "&cFailed to apply the texture pack. Please try again later. If the problem persists, contact the server administrator.")));
                } else {
                    player.sendMessage(plugin.deserialize(plugin.config.getString("messages.failed_texturepack", "&cFailed to apply the texture pack. Please try again later. If the problem persists, contact the server administrator.")));
                }
            case ACCEPTED:
        }
    }

    // Prevenir daño mientras se aplica el resource pack
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && protectedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Prevenir movimiento mientras se aplica el resource pack
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (protectedPlayers.contains(player.getUniqueId())) {
            // Solo cancelar si realmente se mueve (evitar cancelaciones innecesarias)
            if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
                event.setTo(event.getFrom());
            }
        }
    }
}
