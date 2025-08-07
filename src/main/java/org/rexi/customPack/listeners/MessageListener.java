package org.rexi.customPack.listeners;

import org.bukkit.entity.Player;
import org.rexi.customPack.CustomPack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageListener implements PluginMessageListener {

    private final CustomPack plugin;

    public MessageListener(CustomPack plugin) {
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

                plugin.getLogger().info("Aplicando pack global a " + player.getName() + " (motivo: " + reason + ")");
                player.setResourcePack(url, hash.isBlank() ? null : plugin.hexStringToByteArray(hash), true);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Error leyendo el mensaje del canal de plugin.");
            e.printStackTrace();
        }
    }
}
