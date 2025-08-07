package org.rexi.customPack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.customPack.listeners.PluginMessageListener;

public final class CustomPack extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "custompack:main", new PluginMessageListener(this));
        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been enabled!").color(NamedTextColor.GREEN));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "custompack:main");
        Bukkit.getConsoleSender().sendMessage(Component.text("CustomPack has been disabled!").color(NamedTextColor.RED));
        Bukkit.getConsoleSender().sendMessage(Component.text("Thank you for using Rexi666 plugins :D").color(NamedTextColor.BLUE));
    }
}
