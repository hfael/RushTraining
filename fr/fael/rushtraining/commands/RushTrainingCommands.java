package fr.fael.rushtraining.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.fael.rushtraining.RushTraining;
import fr.fael.rushtraining.RushTrainingFunction;
import fr.fael.rushtraining.listeners.RushTrainingListeners;
import fr.fael.rushtraining.runnable.RushTrainingRunnable;

public class RushTrainingCommands implements CommandExecutor {

	RushTraining rt;
	
	public RushTrainingCommands(RushTraining rt) {
		this.rt = rt;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("rushtraining")) {
				if(arg[0].equalsIgnoreCase("start")) {
					if(RushTrainingFunction.getState().equals("STARTING")) {
						if(RushTrainingFunction.hasMinPlayers() == true) {
							if(RushTrainingRunnable.countdown > 10) {
								RushTrainingRunnable.countdown = 3;
								Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§3" + sender.getName() + " §eforce le démarrage de la partie !");
							}else {
								sender.sendMessage(RushTrainingFunction.getPrefix() + "§cLa partie et sur le point de commencer !");
							}
							return true;
						}
					}
					if(RushTrainingFunction.getState().equals("WAITING")) {
						sender.sendMessage(RushTrainingFunction.getPrefix() + "§cIl n'y a pas assez de joueurs !");
						return true;
					}
					if(RushTrainingFunction.getState().equals("INGAME")) {
						sender.sendMessage(RushTrainingFunction.getPrefix() + "§cLa partie est déjà en cours !");
						return true;
					}
				}
			}
			if(arg[0].equalsIgnoreCase("blueEnd")) {
				if(RushTrainingFunction.getState().equals("INGAME")) {
					RushTraining.bluePoints = 4;
					sender.sendMessage(RushTrainingFunction.getPrefix() + "§7Ajout de §e4 points §7a l'équipe §9Bleu§7.");
				}
				if(RushTrainingFunction.getState().equals("WAITING") || RushTrainingFunction.getState().equals("STARTING")) {
					sender.sendMessage(RushTrainingFunction.getPrefix() + "§cLa partie n'est pas en cours !");
				}
			}
			
		}
		if(label.equalsIgnoreCase("resetblock")) {
			if(sender.hasPermission("rushtraining.admin")) {
				RushTrainingFunction.resetBlock();
				sender.sendMessage(RushTrainingFunction.getPrefix() + "§aRetrait des blocks !");
			}else {
				sender.sendMessage(RushTrainingFunction.getPrefix() + "§cVous n'avez pas la permission !");
			}
		}
		if(label.equalsIgnoreCase("allbridge")) {
			if(sender.hasPermission("rushtraining.admin")) {
				if(RushTrainingListeners.bridgeBlockBlue != null) {
					for(Location loc : RushTrainingListeners.bridgeBlockBlue) {
						loc.getBlock().setType(Material.SANDSTONE);
					}
				}
				if(RushTrainingListeners.bridgeBlockRed != null) {
					for(Location loc : RushTrainingListeners.bridgeBlockRed) {
						loc.getBlock().setType(Material.SANDSTONE);
					}
				}
				sender.sendMessage(RushTrainingFunction.getPrefix() + "§aBlocs des ponts fait !");
			}else {
				sender.sendMessage(RushTrainingFunction.getPrefix() + "§cVous n'avez pas la permission !");
			}
		}
		return true;
	}

}
