package org.rexi.customPack.listeners;

import org.bukkit.entity.Player;
import org.rexi.customPack.CustomPack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageListener implements PluginMessageListener {

    private final CustomPack plugin;

    public PluginMessageListener(CustomPack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("custompack:main")) return;

        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(message))) {
            String action = input.readUTF();

            if (action.equals("SET_PACK")) {
                String url = input.readUTF();
                String hash = input.readUTF();
                String reason = input.readUTF();

                plugin.getLogger().info("Aplicando resource pack a " + player.getName() + " (motivo: " + reason + ")");
                plugin.getLogger().info("URL: " + url);
                plugin.getLogger().info("SHA1: " + hash);

                player.setResourcePack(url, hash.isEmpty() ? null : hash.getBytes());
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Error al leer el mensaje del canal de plugin.");
            e.printStackTrace();
        }
    }
}
