package fr.fael.rushtraining.runnable;


import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import fr.fael.rushtraining.RushTraining;
import fr.fael.rushtraining.RushTrainingFunction;
import fr.fael.rushtraining.utils.ActionBarUtils;

public class RushTrainingRunnablePlayers implements Runnable{

	Player player;
	BukkitTask task;
	
	public RushTrainingRunnablePlayers(Player player) {
		this.player = player;
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setPlayerListName("§f" + player.getName());
		player.setDisplayName("§f" + player.getName());
		player.setCustomName("§f" + player.getName());
		for(PotionEffect effect :player.getActivePotionEffects ()){
			player.removePotionEffect(effect.getType());
		}
		this.task = Bukkit.getServer().getScheduler().runTaskTimer(RushTraining.getPlugin(), this, 0, 18);
	}
	public void run() {
		if(player.isOnline()) {
			player.setFoodLevel(20);
			if(RushTrainingFunction.getState().equalsIgnoreCase("WAITING") || RushTrainingFunction.getState().equalsIgnoreCase("STARTING")) {
				ActionBarUtils.sendActionBarMessage(player, "§cRouge§7: §e" + RushTrainingFunction.redTeam.size() + " §8| §9Bleu§7: §e" + RushTrainingFunction.blueTeam.size() + " §7Début dans §6" + RushTrainingRunnable.countdown + "sec§7.");
				if(player.getLocation().getY() < 30) {
					player.teleport(RushTrainingFunction.getLobbyLocation());
					if(RushTrainingFunction.getPrefix() != null) {
						Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§7Vous ne pouvez pas quitter la salle d'attente !");
					}
				}
			}

			if(RushTrainingFunction.getState().equalsIgnoreCase("InGame")) {
				Location loc = player.getLocation();
				loc.subtract(0,1,0);
				Block block = loc.getBlock();
				ActionBarUtils.sendActionBarMessage(player, "§cRouge§7: §e" + RushTraining.redPoints + " §8| §9Bleu§7: §e" + RushTraining.bluePoints);
				if(block.getType() == Material.WOOL) {
					BlockState state = block.getState();
					Wool wool = (Wool)state.getData();
					if(RushTrainingFunction.redTeam.contains(player)) {
						if(wool.getColor() == DyeColor.BLUE){
							try {
								RushTrainingFunction.newPoint(player, "Rouge");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					if(RushTrainingFunction.blueTeam.contains(player)) {
						if(wool.getColor() == DyeColor.RED){
							try {
								RushTrainingFunction.newPoint(player, "Bleu");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if(player.getLocation().getY() < 30) {
					RushTrainingFunction.playerDie(player);
					if(RushTrainingFunction.getPrefix() != null) {
						Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + player.getDisplayName() + " §7est mort du vide !");
					}
				}
			}
		}else {
			task.cancel();
		}
	}
}
