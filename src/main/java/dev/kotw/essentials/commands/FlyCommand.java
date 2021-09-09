package dev.kotw.essentials.commands;

import dev.kotw.essentials.language.LanguageProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements TabExecutor {
    public static final String SYNTAX = "/fly [Player]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Test for permissions
        if(!LanguageProvider.requirePermissions(sender, "fly")) return false;

        if(args.length == 0) {
            if(LanguageProvider.requirePlayer(sender)) {
                Player player = (Player) sender;
                boolean flying = !player.getAllowFlight();
                player.setAllowFlight(flying);
                player.setFlying(flying);
                LanguageProvider.send(sender, "fly.successSelf" + (flying?"Fly":""));
            }
        } else if(args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if(player!=null) {
                boolean flying = !player.getAllowFlight();
                player.setAllowFlight(flying);
                player.setFlying(flying);
                LanguageProvider.send(sender, "fly.successOther" + (flying?"Fly":""), args[0]);
                LanguageProvider.send(player, "fly.successSelf" + (flying?"Fly":""));
            } else LanguageProvider.send(sender, "noOnlinePlayer");
        } else LanguageProvider.send(sender, SYNTAX);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if(args.length==1) {
            // Add all online players
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getName().toLowerCase().startsWith(args[0].toLowerCase())) result.add(player.getName());
            }
        }
        return result;
    }
}
