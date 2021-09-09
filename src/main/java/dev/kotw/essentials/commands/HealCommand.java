package dev.kotw.essentials.commands;

import dev.kotw.essentials.Main;
import dev.kotw.essentials.language.LanguageProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
// Tab completer and command executor in one
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand implements TabExecutor {
    public static final String SYNTAX = "/heal [Player|healPoints] [healPoints]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for permissions
        if(!LanguageProvider.requirePermissions(sender, "heal")) return false;

        if(args.length == 0) {
            // Heal self full -> must be a player
            if(sender instanceof Player) {
                Player player = (Player) sender;
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                LanguageProvider.send(sender, "heal.successSelfFull");
            } else LanguageProvider.send(sender, "noPlayer");

        } else if(args.length == 1) {
            // Heal self x / Heal other full
            try {
                double health = Double.parseDouble(args[0]);
                // Heal self -> must be a player
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+health));
                    player.setFoodLevel(20);
                    LanguageProvider.send(sender, "heal.successSelf", ""+health/2);
                } else LanguageProvider.send(sender, "noPlayer");
            } catch (NumberFormatException e) {
                // Heal other
                Player player = Bukkit.getPlayerExact(args[0]);
                if(player != null) {
                    player.setHealth(player.getMaxHealth());
                    player.setFoodLevel(20);
                    LanguageProvider.send(sender, "heal.successOtherFull", args[0]);
                } else LanguageProvider.send(sender, "noOnlinePlayer", args[0]);
            }

        } else if(args.length == 2) {
            // Heal other x
            try {
                double health = Double.parseDouble(args[1]);
                Player player = Bukkit.getPlayerExact(args[0]);
                if(player != null) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+health));
                    player.setFoodLevel(20);
                    LanguageProvider.send(sender, "heal.successOther", args[0], ""+health/2);
                } else LanguageProvider.send(sender, "noOnlinePlayer", args[0]);
            } catch (NumberFormatException e) {
                LanguageProvider.send(sender, "typeError", "<health>", "double");
            }
        } else LanguageProvider.send(sender, "syntax", SYNTAX);

        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if(args.length == 1) {
            // Append online players to the tab result if the first argument isn't a double
            try {
                Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().toLowerCase().startsWith(args[0].toLowerCase())) result.add(player.getName());
                }
            }
        }
        return result;
    }
}
