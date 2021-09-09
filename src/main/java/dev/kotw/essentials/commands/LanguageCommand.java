package dev.kotw.essentials.commands;

import dev.kotw.essentials.language.LanguageProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LanguageCommand implements TabExecutor {
    public static final String SYNTAX = "/language <Language>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for permissions
        if(!LanguageProvider.requirePermissions(sender, "language")) return false;

        if(args.length==1) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                for(LanguageProvider lang : LanguageProvider.languages.values()) {
                    if(lang.get("name").equalsIgnoreCase(args[0])) {
                        LanguageProvider.players.set(player.getUniqueId().toString(), lang.get("type"));
                        LanguageProvider.send(sender, "language.success", lang.get("name"));
                        return false;
                    }
                }
                LanguageProvider.send(sender, "notFound", "language", args[0]);
            } else LanguageProvider.send(sender, "noPlayer");
        } else LanguageProvider.send(sender, "syntax", SYNTAX);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if(args.length==1) {
            // Add all languages to tab
            for(LanguageProvider lang : LanguageProvider.languages.values()) {
                if(lang.get("name").startsWith(args[0].toLowerCase())) result.add(lang.get("name"));
            }
        }
        return result;
    }
}
