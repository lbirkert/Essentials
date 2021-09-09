package dev.kotw.essentials.language;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.kotw.essentials.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

public class LanguageProvider {
    public static final LanguageProvider fallback = new LanguageProvider(loadFallbackLanguageMap());
    public static final HashMap<String, LanguageProvider> languages = loadLanguages();
    public static final FileConfiguration players = loadPlayers();

    private HashMap<String, String> keys;

    public LanguageProvider(HashMap<String, String> keys) {
        this.keys = keys;
    }

    public LanguageProvider(String resourceName) throws IOException {
        StringBuilder result = new StringBuilder();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        // Parse using gson
        HashMap<String, String> keys = Main.gson.fromJson(result.toString(), new TypeToken<HashMap<String, String>>(){}.getType());

        this.keys = keys;
    }

    public String get(String key) {
        String res = keys.get(key);
        return res==null?fallback.get(key):res;
    }

    public String format(String key, String... args) {
        String res = get(key);
        for(int i = 0; i < args.length; i++) {
            res = res.replaceAll("!!" + i, args[i]);
        }
        return res;
    }

    public static LanguageProvider get(Player player) {
        return languages.getOrDefault(players.getString(player.getUniqueId().toString()), fallback);
    }

    public static String getF(String key) {
        return fallback.get(key);
    }

    public static String formatF(String key, String... args) {
        return fallback.format(key, args);
    }

    public static void send(CommandSender sender, String key, String... args) {
        LanguageProvider lang = sender instanceof Player?get((Player) sender):fallback;
        sender.sendMessage(Main.PREFIX + lang.format(key, args));
    }

    public static boolean requirePermissions(CommandSender sender, String cmdName) {
        String permission = Main.config.getString("permissions." + cmdName);
        if(permission == null || permission.equals("") || sender.hasPermission(permission)) {
            return true;
        } else send(sender, "noPermission");
        return false;
    }

    public static boolean requirePlayer(CommandSender sender) {
        if(sender instanceof Player) {
            return true;
        } else send(sender, "noPlayer");
        return false;
    }


    public static void classLoad() {};

    public static YamlConfiguration loadPlayers() {
        // Create File and Data folder if they don't exist
        File path = Main.plugin.getDataFolder();
        path.mkdirs();

        File configFile = new File(path, "players.yml");

        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public static HashMap<String, LanguageProvider> loadLanguages() {
        String[] languagesAvailable = new String[]{"en_us", "de_de"};
        HashMap<String, LanguageProvider> languagesLoaded = new HashMap<>();
        for(String lang : languagesAvailable) {
            try {
                languagesLoaded.put(lang, new LanguageProvider("lang/" + lang + ".json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return languagesLoaded;
    }

    private static HashMap<String, String> loadFallbackLanguageMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", "fallback");
        result.put("type", "fall_back");
        result.put("noPermission", "§cYou don't have the permissions to execute this command!");
        result.put("noPlayer", "§cYou have to be a player to use this command!");
        result.put("noOnlinePlayer", "§cThe player §6!!0 §cis not online!");
        result.put("noOfflinePlayer", "§cThe player §6!!0 §cwasn't found!");
        result.put("syntax", "§cPlease use §6!!0§c!");
        result.put("typeError", "§cArgument §6!!0 §chas to be of type §6!!1§c!");
        result.put("notFound", "§cError couldn't find !!0 §6'!!1'§c!");

        // Language command
        result.put("language.success", "§aSuccessfully set your language to §6'!!0'§a!");

        // Heal command
        result.put("heal.successSelf", "§aSuccessfully healed your self by §6!!0 heart(s)§a!");
        result.put("heal.successSelfFull", "§aSuccessfully healed your self!");
        result.put("heal.successOther", "§aSuccessfully healed §6!!0 §aby §6!!1 heart(s)§a!");
        result.put("heal.successOtherFull", "§aSuccessfully healed §6!!0 §a!");

        // Fly command
        result.put("fly.successSelf", "§aYou are no longer able to fly!");
        result.put("fly.successSelfFly", "§aYou are now able to fly!");
        result.put("fly.successOther", "§aMade §6!!0 §ano longer able to fly!");
        result.put("fly.successOtherFly", "§aMade §6!!0 §aable to fly!");
        return result;
    }

    public static void savePlayers() {
        try {
            players.save(new File(Main.plugin.getDataFolder(), "players.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
