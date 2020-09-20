package me.jimmybenoit.HelixParticle;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener {
	
	HashMap<UUID, Particle> map = new HashMap<UUID, Particle>();
	HashMap<UUID, BukkitTask> godpleasework = new HashMap<UUID, BukkitTask>();
	
	@Override
	public void onEnable() {	
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(label.equalsIgnoreCase("helix")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can execute this.");
				return true;
			}
			Player player = (Player) sender;
			
			if (args.length == 0) {
				if (map.containsKey(player.getUniqueId())) {
					map.remove(player.getUniqueId());
					player.sendMessage(ChatColor.RED + "Disabled particle.");
					return true;
				}
				player.sendMessage(ChatColor.RED + "Please include a particle.");
				return true;
			}
			
			Particle particle;
			
			try{
				//checking if second line is a valid entity
				particle = Particle.valueOf(args[0].toUpperCase());
			}catch(IllegalArgumentException exp){
				player.sendMessage(ChatColor.RED + "Please include a valid particle.");
				return true;
			}
			
			if (!(map.containsKey(player.getUniqueId()))) {
				map.put(player.getUniqueId(), particle);
				makeHelix(player);
				return true;
			}
			map.remove(player.getUniqueId());
			player.sendMessage(ChatColor.RED + "Disabled particle.");
			
			return true;
		}
		
		return false;
	}
	
	@EventHandler()
	public void onJoin(PlayerJoinEvent event) {
		System.out.println("Player joined");
		Player player = (Player) event.getPlayer();
		if (map.containsKey(player.getUniqueId())) {
			makeHelix(player);
		}
	}
	
	@EventHandler()
	public void onLeave(PlayerQuitEvent event) {
		Player player = (Player) event.getPlayer();
		if (godpleasework.containsKey(player.getUniqueId())) {
			godpleasework.get(player.getUniqueId()).cancel();
		}
	}
	
	public Location getPoint(double t, Location playerLoc) {
		double r = 1;
		double c = 0.1;
		double x = (r*Math.cos(t))+playerLoc.getX();
		double z = (r*Math.sin(t))+playerLoc.getZ();
		double y = (c*t)+playerLoc.getY();
		Location loc = new Location(playerLoc.getWorld(), x, y, z);
		
		return loc;
	}
	
	public void makeHelix(Player player) {
		Particle particle = map.get(player.getUniqueId());
		BukkitTask task = new BukkitRunnable(){
			double t = 0;
			public void run(){
				t = t + Math.PI/8;
				player.getWorld().spawnParticle(particle, getPoint(t, player.getLocation()), 0);
				if (!(map.containsKey(player.getUniqueId()))) {
					this.cancel();
				}
				if (t > Math.PI*4) {
					//this.cancel();
					t = 0;
				} 
			}
		}.runTaskTimer(this, 0, 1);
		godpleasework.put(player.getUniqueId(), task);
		//player.sendMessage(loc.getX()+" "+loc.getY()+" "+loc.getZ());
	}

}
