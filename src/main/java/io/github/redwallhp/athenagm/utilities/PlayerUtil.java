package io.github.redwallhp.athenagm.utilities;


import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.arenas.ArenaHandler;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {


    public static void resetPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setTotalExperience(0);
        ItemStack[] noArmor = new ItemStack[] {
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        };
        player.getInventory().clear();
        player.getInventory().setArmorContents(noArmor);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }


    public static Team getTeamForPlayer(Match match, Player player) {
        for (Team team : match.getTeams().values()) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }


    public static Team getTeamForPlayer(ArenaHandler arenaHandler, Player player) {
        for (Arena arena : arenaHandler.getArenas()) {
            Team team = getTeamForPlayer(arena.getMatch(), player);
            if (team != null) return team;
        }
        return null;
    }


    public static Arena getArenaForPlayer(ArenaHandler arenaHandler, Player player) {
        for (Arena arena : arenaHandler.getArenas()) {
            Team team = getTeamForPlayer(arena.getMatch(), player);
            if (team != null) return arena;
        }
        return null;
    }


}
