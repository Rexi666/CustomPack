package org.rexi.customPack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.customPack.commands.CustomPackCommand;
import org.rexi.customPack.commands.TexturePackCommand;
import org.rexi.customPack.listeners.MessageListener;
import org.rexi.customPack.listeners.PlayerJoin;

public final class CustomPack extends JavaPlugin {

    public FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        getServer().getMessenger().registerIncomingPluginChannel(this, "custompack:main", new MessageListener(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "custompack:main");

        getCommand("texturepack").setExecutor(new TexturePackCommand(this));
        getCommand("custompack").setExecutor(new CustomPackCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);

        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been enabled!").color(NamedTextColor.GREEN));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "custompack:main");

        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been disabled!").color(NamedTextColor.RED));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }

    public Component deserialize(String input) {
        // Si contiene <...> asumimos que es MiniMessage
        if (input.contains("<") && input.contains(">")) {
            try {
                return MiniMessage.miniMessage().deserialize(input);
            } catch (Exception e) {
                // En caso de error, usa como texto plano
                return Component.text(input);
            }
        }

        // Si no, asumimos que es con códigos &
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    public byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public void reloadsConfig() {
        reloadConfig();
        config = getConfig();
    }
}
