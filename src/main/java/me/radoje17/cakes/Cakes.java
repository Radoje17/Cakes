package me.radoje17.cakes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Cakes extends JavaPlugin implements Listener {

    private boolean gameStarted = false;
    private boolean pvp = true;

    private Location spawn;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("start")) {
            if (gameStarted) {
                sender.sendMessage("Igra je već u toku.");
                return false;
            }
            if (Bukkit.getOnlinePlayers().size() < 2) {
                sender.sendMessage("Nema dovoljno igrača.");
                return false;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getInventory().clear();
                p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
                p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
                p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
                p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
                p.setHealth(20);
                p.setFoodLevel(20);
                p.teleport(spawn);
            }
            Bukkit.getWorlds().get(0).setTime(0);
            for (Entity e : spawn.getWorld().getEntities()) {
                if (e instanceof Monster) {
                    e.remove();
                }
            }
            pvp = false;
            gameStarted = true;
            Bukkit.broadcastMessage("Igra počinje!");
            Bukkit.broadcastMessage("PvP ce biti uključen za 2 minuta.");
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("PvP ce biti uključen za 1 minut.");
                }
            }, 20L*60);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage("PvP je uključen.");
                    pvp = true;
                }
            }, 20L*120);
        }
        return false;
    }

    @EventHandler
    public void pvp(EntityDamageByEntityEvent e) {
        if (!pvp && e.getDamager() instanceof Player && e.getEntity() instanceof Player)
            e.setCancelled(true);
    }

    @EventHandler
    public void craft(CraftItemEvent e) {
        if (e.getCurrentItem().getType() == Material.CAKE) {
            gameStarted = false;
            pvp = true;
            Bukkit.broadcastMessage(e.getWhoClicked().getName() + " je pobedio/la!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getInventory().clear();
                p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
                p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
                p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
                p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
                p.setHealth(20);
                p.setFoodLevel(20);
                p.teleport(spawn);
            }
            for (Entity entity : spawn.getWorld().getEntities()) {
                if (entity instanceof Monster) {
                    entity.remove();
                }
            }
            Bukkit.getWorlds().get(0).setTime(0);
        }
    }
}
