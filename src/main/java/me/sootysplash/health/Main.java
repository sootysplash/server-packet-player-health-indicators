package me.sootysplash.health;

import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public final class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new Lister());
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        Lister.initIndicators();
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " made by " + pdfFile.getAuthors() + " is enabled!");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static Main getInstance() {
        return instance;
    }
}
