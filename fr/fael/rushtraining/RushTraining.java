package fr.fael.rushtraining;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.fael.rushtraining.commands.RushTrainingCommands;
import fr.fael.rushtraining.listeners.RushTrainingListeners;
import net.md_5.bungee.api.ChatColor;

public class RushTraining extends JavaPlugin{
	
	public static int redPoints;
	public static int bluePoints;
	public final File file = new File(this.getDataFolder(), "config.yml");
	public final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	public final ConfigurationSection confsec = config.getConfigurationSection("config.");
	
	static Plugin plugin;
	
	public void onEnable() {
		redPoints = 0;
		bluePoints = 0;
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new RushTrainingListeners(this), this);
		Bukkit.getWorld("world").setDifficulty(Difficulty.NORMAL);
		super.getCommand("rushtraining").setExecutor(new RushTrainingCommands(this));
		super.getCommand("resetblock").setExecutor(new RushTrainingCommands(this));
		super.getCommand("allbridge").setExecutor(new RushTrainingCommands(this));
		plugin = this;
		config.set("config.gamestate", "WAITING");

		for(Player players : Bukkit.getOnlinePlayers()) {
			players.kickPlayer("Redémarrage du serveur !");
		}
		if(confsec == null) {
			config.set("config.prefix", ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "RushTraining" + ChatColor.GRAY+"]");
			config.set("config.minPlayers", Bukkit.getMaxPlayers()/2);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.saveDefaultConfig();
		super.onEnable();
		super.reloadConfig();
	}

	public static Plugin getPlugin() {
		return plugin;
	}
	public void onDisable() {
		RushTrainingFunction.resetBlock();
		super.onDisable();
	}
}
