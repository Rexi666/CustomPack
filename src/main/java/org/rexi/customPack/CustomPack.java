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
import org.rexi.customPack.listeners.JoinListener;
import org.rexi.customPack.listeners.MessageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CustomPack extends JavaPlugin {

    private FileConfiguration config;
    private final Set<UUID> playersWithLocalPack = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        getServer().getMessenger().registerIncomingPluginChannel(this, "custompack:main", new MessageListener(this));

        getCommand("texturepack").setExecutor(new TexturePackCommand(this));
        getCommand("custompack").setExecutor(new CustomPackCommand(this));

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);

        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been enabled!").color(NamedTextColor.GREEN));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "custompack:main");

        playersWithLocalPack.clear();

        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been disabled!").color(NamedTextColor.RED));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }

    public String getServerPackUrl() {
        return config.getString("server_pack.url", "");
    }

    public String getServerPackHash() {
        return config.getString("server_pack.hash", "");
    }

    public boolean isApplyOnJoinEnabled() {
        return config.getBoolean("server_pack.apply_on_join", true);
    }

    public Set<UUID> getPlayersWithLocalPack() {
        return playersWithLocalPack;
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
}
