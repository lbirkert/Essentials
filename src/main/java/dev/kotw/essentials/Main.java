package dev.kotw.essentials;

import com.google.gson.Gson;
import dev.kotw.essentials.language.LanguageProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dev.kotw.essentials.commands.*;

import java.io.File;

public final class Main extends JavaPlugin {

    public static Gson gson = new Gson();
    public static FileConfiguration config;

    public static Plugin plugin;

    // Declare a static prefix
    public static final String PREFIX = "§7[§cEssentials§7] §r";

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4Successfully enabled.");

        // Register commands
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("heal").setTabCompleter(new HealCommand());

        getCommand("language").setExecutor(new LanguageCommand());
        getCommand("language").setTabCompleter(new LanguageCommand());

        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("fly").setTabCompleter(new FlyCommand());

        // This method is only that the class is being loaded and the static fields too
        LanguageProvider.classLoad();

        // Load config
        config = getConfig();
        config.setDefaults(YamlConfiguration.loadConfiguration(new File(getClassLoader().getResource("config.yml").getFile())));
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§6Successfully disabled.");

        LanguageProvider.savePlayers();
    }
}
