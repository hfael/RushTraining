package fr.fael.rushtraining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.fael.rushtraining.listeners.RushTrainingListeners;
import fr.fael.rushtraining.runnable.RushTrainingRunnable;

public class RushTrainingFunction {
	
	public static List<Player> redTeam = new ArrayList<Player>();
	public static List<Player> blueTeam = new ArrayList<Player>();
	
	public static void resetBlock() {
		for(Location loc : RushTrainingListeners.blockPlaced) {
			if(loc == null) break;
			loc.getBlock().setType(Material.AIR);
		}
		for(Location loc : RushTrainingListeners.bridgeBlockRed) {
			if(loc == null) break;
			loc.getBlock().setType(Material.AIR);
		}
		for(Location loc : RushTrainingListeners.bridgeBlockBlue) {
			if(loc == null) break;
			loc.getBlock().setType(Material.AIR);
		}
		System.out.println("Retrait de " + RushTrainingListeners.blockPlaced.size() + " block(s) !");
		RushTrainingListeners.blockPlaced.clear();
	}
	
	public static String getState() {
		final File file = new File(RushTraining.getPlugin().getDataFolder(), "config.yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final ConfigurationSection confsec = config.getConfigurationSection("config.");
		final String state = confsec.getString("gamestate");
		return state;
	}
	public static void setState(String str) throws IOException {
		final File file = new File(RushTraining.getPlugin().getDataFolder(), "config.yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("config.gamestate", str);
		config.save(file);
	}
	public static String getPrefix() {
		final File file = new File(RushTraining.getPlugin().getDataFolder(), "config.yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final ConfigurationSection confsec = config.getConfigurationSection("config.");
		final String prefix = confsec.getString("prefix");
		return prefix;
	}
	public static Location getLobbyLocation() {
		Location loc = new Location(Bukkit.getWorld("world"), 0.5, 51.5, 0.5, -90, 0);
		return loc;
	}
	public static Location getRedLocation() {
		Location loc = new Location(Bukkit.getWorld("world"), 5.5, 55.5, 78.5, 180, 0);
		return loc;
	}
	public static Location getBlueLocation() {
		Location loc = new Location(Bukkit.getWorld("world"), -2.5, 55.5, -77.5, 0, 0);
		return loc;
	}
	public static int getMinPlayers() {
		final File file = new File(RushTraining.getPlugin().getDataFolder(), "config.yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final ConfigurationSection confsec = config.getConfigurationSection("config.");
		final int minPlayer = confsec.getInt("minPlayers");
		return minPlayer;
	}
	
	public static boolean hasMinPlayers() {
		if(Bukkit.getOnlinePlayers().size() == getMinPlayers()) return true;
		return false;
	}
	

	@SuppressWarnings("deprecation")
	public static void GiveItemJoin(Player player) {
		ItemStack blueTeam = new ItemStack(Material.INK_SACK, 1, DyeColor.YELLOW.getData());
		ItemMeta blueTeamMeta = blueTeam.getItemMeta();
		blueTeamMeta.setDisplayName("§9Bleu");
		blueTeam.setItemMeta(blueTeamMeta);
		
		ItemStack redTeam = new ItemStack(Material.INK_SACK, 1, DyeColor.ORANGE.getData());
		ItemMeta redTeamMeta = redTeam.getItemMeta();
		redTeamMeta.setDisplayName("§cRouge");
		redTeam.setItemMeta(redTeamMeta);
		
		ItemStack editKit = new ItemStack(Material.EYE_OF_ENDER, 1);
		ItemMeta editKitMeta = editKit.getItemMeta();
		editKitMeta.setDisplayName("§6Edit Kit");
		editKit.setItemMeta(editKitMeta);
		
		ItemStack leaveItem = new ItemStack(Material.BED, 1);
		ItemMeta leaveItemMeta = leaveItem.getItemMeta();
		leaveItemMeta.setDisplayName("§eQuitter la partie");
		leaveItem.setItemMeta(leaveItemMeta);
		
		
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.getInventory().setItem(0, blueTeam);
		player.getInventory().setItem(1, redTeam);
		player.getInventory().setItem(4, editKit);
		player.getInventory().setItem(8, leaveItem);
		player.updateInventory();
	}
	
	public static void JoinTeam(Player player, String team) {
		if(team.equalsIgnoreCase("Rouge")) {
			if(!redTeam.contains(player)) {
				if(redTeam.size() < (Bukkit.getOnlinePlayers().size() / 2)) {
					if(blueTeam.contains(player)) {
						blueTeam.remove(player);
					}
					redTeam.add(player);
					player.sendMessage(getPrefix() + "§7Vous avez rejoint l'équipe §cRouge§7.");
					player.setDisplayName("§c" + player.getName());
					player.setPlayerListName("§c" + player.getName());
					player.setCustomName("§c" + player.getName());
				}else {
					player.sendMessage(getPrefix() + "§cIl y a trop de joueurs dans cette équipe !");
					}
			}else {
				player.sendMessage(getPrefix() + "§cVous êtes déjà dans cette équipe ! ");	
				}
		}
		if(team.equalsIgnoreCase("Bleu")) {
			if(!blueTeam.contains(player)) {
				if(blueTeam.size() < (Bukkit.getOnlinePlayers().size() / 2)) {
					if(redTeam.contains(player)) {
						redTeam.remove(player);
					}
					blueTeam.add(player);
					player.sendMessage(getPrefix() + "§7Vous avez rejoint l'équipe §9Bleu§7.");
					player.setPlayerListName("§9" + player.getName());
					player.setDisplayName("§9" + player.getName());
					player.setCustomName("§9" + player.getName());
				}else {
					player.sendMessage(getPrefix() + "§cIl y a trop de joueurs dans cette équipe !");
					}
			}else {
				player.sendMessage(getPrefix() + "§cVous êtes déjà dans cette équipe !");
				}
			}
		}
	public static void RandomTeam(Player player) {
		if(redTeam.size() == blueTeam.size()) {
			JoinTeam(player, "Rouge");
			return;
		}
		if(redTeam.size() > blueTeam.size()){
			JoinTeam(player, "Bleu");
			return;
		}
		if(redTeam.size() < blueTeam.size()){
			JoinTeam(player, "Rouge");
			return;
		}
	}

	public static void newPoint(Player player, String string) throws IOException {
		resetBlock();
		for(Player redPlayers : redTeam) {
			redPlayers.teleport(getRedLocation());
			if(string.equalsIgnoreCase("Rouge")) {
				redPlayers.sendMessage(getPrefix() + "§b" + player.getName() + " §6a marqué un point !");
				RushTraining.redPoints += 1;
			}
			if(string.equalsIgnoreCase("Bleu")) {
				redPlayers.sendMessage(getPrefix() + "§7L'équipe §9Bleu §7marque un point !");
				spawnFireworks(getBlueLocation(), 4, Color.BLUE);
				spawnFireworks(getRedLocation(), 4, Color.BLUE);
			}
		}
		for(Player bluePlayers : blueTeam) {
			bluePlayers.teleport(getBlueLocation());
			if(string.equalsIgnoreCase("Bleu")) {
				bluePlayers.sendMessage(getPrefix() + "§b" + player.getName() + " §6a marqué un point !");
				RushTraining.bluePoints += 1;
			}
			if(string.equalsIgnoreCase("Rouge")) {
				bluePlayers.sendMessage(getPrefix() + "§7L'équipe §cRouge §7marque un point !");
				spawnFireworks(getBlueLocation(), 4, Color.RED);
				spawnFireworks(getRedLocation(), 4, Color.RED);
			}
		}
		if(RushTraining.bluePoints == 5) endGame("Bleu");
		if(RushTraining.redPoints == 5) endGame("Rouge");
	}
	
	public static void spawnFireworks(Location location, int amount, Color color){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        fwm.setPower(0);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());
       
        fw.setFireworkMeta(fwm);
        fw.detonate();
       
        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

	public static void endGame(String string) throws IOException {
		Location loc = getLobbyLocation();
		RushTrainingRunnable.GameTimeSec = 9999;
		RushTrainingFunction.setState("FINISH");
		if(string == "Bleu") Bukkit.broadcastMessage(getPrefix() + "§6§lVictoire de l'équipe §9§lBleue §b§k!§a§ki§d§k!§e§ki§r §bFélicitations §b§k!§a§ki§d§k!§e§ki"); spawnFireworks(getLobbyLocation(), 2, Color.BLUE);
		if(string == "Rouge") Bukkit.broadcastMessage(getPrefix() + "§6§lVictoire de l'équipe §c§lRouge §b§k!§a§ki§d§k!§e§ki§r §bFélicitations §b§k!§a§ki§d§k!§e§ki"); spawnFireworks(getLobbyLocation(), 2, Color.RED);
		for(Player players : Bukkit.getOnlinePlayers()) {
			players.teleport(loc);
			players.setGameMode(GameMode.SPECTATOR);	
		}
		new BukkitRunnable() {
			int timer = 0;
			public void run() {
				timer++;
				if(timer == 15) {
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
				}
			}	
		}.runTaskTimer(RushTraining.getPlugin(), 0, 20);
	}
	public static void playerDie(Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		player.getWorld().strikeLightningEffect(player.getLocation());
		if(blueTeam.contains(player)) player.teleport(getBlueLocation().add(0, 3, 0));
		if(redTeam.contains(player)) player.teleport(getRedLocation().add(0, 3, 0));
		new BukkitRunnable() {
			int time = 8;
			public void run() {
				time--;
				player.sendMessage(getPrefix() + "§eRéapparition dans §b" + time + "sec§e.");
				if(time == 0) {
					if(blueTeam.contains(player)) {
						player.teleport(getBlueLocation());
						GiveKit(player, "Bleu");
					}
					if(redTeam.contains(player)) {
						player.teleport(getRedLocation());
						GiveKit(player, "Rouge");
						
					}
					player.setGameMode(GameMode.SURVIVAL);
					cancel();
				}
			}
		}.runTaskTimer(RushTraining.getPlugin(), 0, 20);
		player.setHealth(20);
		player.setFoodLevel(20);
	}
	
	public static void GiveKit(Player player, String team) {
		final File file = new File(RushTraining.getPlugin().getDataFolder(), RushTraining.getPlugin()+ "/" + player.getUniqueId() + ".yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		final ConfigurationSection confsec = config.getConfigurationSection("kit.");
		final int swordSlot = confsec.getInt("sword");
		final int pickaxeSlot = confsec.getInt("pickaxe");
		final int gappleSlot = confsec.getInt("goldenApple");
		final int sandstoneSlot = confsec.getInt("sandstone");
		final int tntSlot = confsec.getInt("tnt");
		final int lighterSlot = confsec.getInt("lighter");
		player.getInventory().clear();
		ItemStack sword = new ItemStack(Material.STONE_SWORD);
		ItemMeta swordMeta = sword.getItemMeta();
		swordMeta.spigot().setUnbreakable(true);
		ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta pickaxeMeta = pickaxe.getItemMeta();
		pickaxeMeta.spigot().setUnbreakable(true);
		ItemStack lighter = new ItemStack(Material.FLINT_AND_STEEL);
		ItemMeta lighterMeta = lighter.getItemMeta();
		lighterMeta.spigot().setUnbreakable(true);
		sword.setItemMeta(swordMeta);
		pickaxe.setItemMeta(pickaxeMeta);
		lighter.setItemMeta(lighterMeta);
		player.getPlayer().getInventory().setItem(swordSlot, sword);
		player.getPlayer().getInventory().setItem(pickaxeSlot, pickaxe);
		player.getPlayer().getInventory().setItem(gappleSlot, new ItemStack(Material.GOLDEN_APPLE));
		player.getPlayer().getInventory().setItem(sandstoneSlot, new ItemStack(Material.SANDSTONE));
		player.getPlayer().getInventory().setItem(tntSlot, new ItemStack(Material.TNT));
		player.getPlayer().getInventory().setItem(lighterSlot, lighter);
		
		ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta BootsMeta = (LeatherArmorMeta) Boots.getItemMeta();
		BootsMeta.spigot().setUnbreakable(true);
		BootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
		
		ItemStack Legs = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta LegsMeta = (LeatherArmorMeta) Legs.getItemMeta();
		LegsMeta.spigot().setUnbreakable(true);
		LegsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
		
		ItemStack Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta ChestplateMeta = (LeatherArmorMeta) Chestplate.getItemMeta();
		ChestplateMeta.spigot().setUnbreakable(true);
		ChestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
		
		ItemStack Helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta HelmetMeta = (LeatherArmorMeta) Helmet.getItemMeta();
		HelmetMeta.spigot().setUnbreakable(true);
		HelmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
		if(team.equalsIgnoreCase("Rouge")) {
			HelmetMeta.setColor(Color.RED);
			ChestplateMeta.setColor(Color.RED);
			LegsMeta.setColor(Color.RED);
			BootsMeta.setColor(Color.RED);
			Helmet.setItemMeta(HelmetMeta);
			Chestplate.setItemMeta(ChestplateMeta);
			Legs.setItemMeta(LegsMeta);
			Boots.setItemMeta(BootsMeta);
			
			
			player.getInventory().setHelmet(Helmet);
			player.getInventory().setChestplate(Chestplate);
			player.getInventory().setLeggings(Legs);
			player.getInventory().setBoots(Boots);
		}
		if(team.equalsIgnoreCase("Bleu")) {
			HelmetMeta.setColor(Color.BLUE);
			ChestplateMeta.setColor(Color.BLUE);
			LegsMeta.setColor(Color.BLUE);
			BootsMeta.setColor(Color.BLUE);
			Helmet.setItemMeta(HelmetMeta);
			Chestplate.setItemMeta(ChestplateMeta);
			Legs.setItemMeta(LegsMeta);
			Boots.setItemMeta(BootsMeta);
			
			
			player.getInventory().setHelmet(Helmet);
			player.getInventory().setChestplate(Chestplate);
			player.getInventory().setLeggings(Legs);
			player.getInventory().setBoots(Boots);
		}
		player.updateInventory();
	}
	
	public static void PlaySound(Player player, Sound sound) {
		player.playSound(player.getLocation(), sound, 1, 1);
	}
}
