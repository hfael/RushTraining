package fr.fael.rushtraining;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class RushTrainingGame {
	
	public RushTrainingGame(){
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
			if(!RushTrainingFunction.redTeam.contains(player)) {
				if(!RushTrainingFunction.blueTeam.contains(player)) {
					RushTrainingFunction.RandomTeam(player);
				}
			}
		}
		for(Player player : RushTrainingFunction.redTeam) {
			player.teleport(RushTrainingFunction.getRedLocation());
			RushTrainingFunction.GiveKit(player, "Rouge");
		}
		for(Player player : RushTrainingFunction.blueTeam) {
			player.teleport(RushTrainingFunction.getBlueLocation());
			RushTrainingFunction.GiveKit(player, "Bleu");
		}
	}
	
}
