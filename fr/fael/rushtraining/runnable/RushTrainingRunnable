package fr.fael.rushtraining.runnable;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import fr.fael.rushtraining.RushTraining;
import fr.fael.rushtraining.RushTrainingFunction;
import fr.fael.rushtraining.RushTrainingGame;

public class RushTrainingRunnable implements Runnable{
	
	public static int countdown = 90;
	public static int GameTimeSec = 0;
	public static int GameTimeMin = 0;
	RushTraining rt;
	BukkitTask task;
	
	public RushTrainingRunnable(RushTraining rt) throws IOException {
		this.rt = rt;
		this.task = Bukkit.getServer().getScheduler().runTaskTimer(RushTraining.getPlugin(), this, 0, 20);
		RushTrainingFunction.setState("STARTING");
	}
	
	public void run() {
		if(GameTimeSec == 9999+10) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
		}
		if(RushTrainingFunction.getState().equalsIgnoreCase("STARTING")) {
			if(RushTrainingFunction.hasMinPlayers() == true) {
				if(countdown == 90) Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§7Début de la partie dans §61 minute 30 secondes§7.");
				if(countdown == 60) Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§7Début de la partie dans §61 minute§7.");
				if(countdown <= 90 || countdown <= 60 && countdown > 1) {
					if(countdown == 30) Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§7Début de la partie dans §6"+ countdown +" secondes§7.");
					if(countdown == 1) Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§7Début de la partie dans §61 seconde§7.");
	
					countdown--;
				}else {
					Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§aDébut de la partie ! Bonne chance :)");
					new RushTrainingGame();
					try {
						RushTrainingFunction.setState("INGAME");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else {
				task.cancel();
				countdown = 90;
				Bukkit.broadcastMessage(RushTrainingFunction.getPrefix() + "§cIl n'y a plus assez de joueurs pour le début de la partie !");
			}
		}
		if(RushTrainingFunction.getState().equalsIgnoreCase("INGAME")) {
			GameTimeSec++;
			if(GameTimeSec == 60) GameTimeSec = 0; GameTimeMin += 1;
		}
	}
	
}
