package fr.fazdix.nclscore.commands.moderation;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.fazdix.nclscore.Main;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ban implements CommandExecutor {
    Main main;

    public Ban(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix"))
                .replace('&', '§');
        if (sender.hasPermission("ncls.moderation.ban")) // Check permission
        {
            if (args.length >= 1) { // Check if args are set
                String reason = Objects.requireNonNull(main.getConfig().getString("messages.ban.defaultReason"))
                        .replace('&', '§'); // Default reason
                String targetName = args[0];
                if (args.length == 1) { // /BAN PLAYER
                    banAndDBAction(targetName, reason, sender, 0);
                } else if (args.length == 3) { // /BAN PLAYER 1 d
                    Boolean args1isInt = isInt(args[1]);
                    Boolean args2ContainsLadder = containsLadder(args[2]);
                    if (args1isInt && args2ContainsLadder) { // If both are true: /BAN PLAYER 1 D
                        Integer time = Integer.valueOf(args[1]); // We get the time
                        String ladder = args[2]; // we get the ladder (month, days, minutes ...)
                        // Convert the time in seconds
                        int seconds = convertInSeconds(time, ladder);
                        banAndDBAction(targetName, reason, sender, seconds);
                    } else {
                        // Ban permanent with the specified reason
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        banAndDBAction(targetName, reason.replace('&', '§'), sender, 0);
                    }
                } else if (args.length >= 4) {
                    Boolean args1isInt = isInt(args[1]);
                    Boolean args2ContainsLadder = containsLadder(args[2]);
                    if (args1isInt && args2ContainsLadder) { // If both are true: /BAN PLAYER 1 D THE REASON ...
                        Integer time = Integer.valueOf(args[1]); // We get the time
                        String ladder = args[2]; // we get the ladder (month, days, minutes ...)
                        // Convert the time in seconds
                        int seconds = convertInSeconds(time, ladder);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        banAndDBAction(targetName, reason, sender, seconds);
                    } else {
                        // Ban permanent with the specified reason
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        banAndDBAction(targetName, reason.replace('&', '§'), sender, 0);
                    }
                }
            } else // NO ARGS
                sender.sendMessage(prefix
                        + Objects.requireNonNull(main.getConfig().getString("messages.ban.noArgs"))
                                .replace('&', '§'));
        } else // NO PERM
            sender.sendMessage(prefix
                    + Objects.requireNonNull(main.getConfig().getString("messages.ban.noperm"))
                            .replace('&', '§'));
        return false;
    }

    private int convertInSeconds(Integer time, String ladder) {
        switch (ladder) {
            case "s":
                return time;
            case "m":
                return time * 60;
            case "h":
                return time * 3600;
            case "d":
                return time * 86400;
            case "mo":
                return time * 2628000;
            case "y":
                return time * 31536000;
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    public void banAndDBAction(String targetName, String reason, CommandSender sender, int time) {
        String targetUUID;
        String staffUUID = "";
        if (Bukkit.getServer().getPlayerExact(targetName) != null) { // TARGET IS ONLINE
            Player target = Bukkit.getServer().getPlayerExact(targetName);
            assert target != null;
            targetUUID = target.getUniqueId().toString();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = null;
            if (time != 0)
                end = now.plusSeconds(time);
            if (time != 0)
                target.kickPlayer(Objects.requireNonNull(main.getConfig().getString("messages.ban.banMessage"))
                        .replace('&', '§')
                        .replace("%staff%", sender.getName())
                        .replace("%reason%", reason.replace('&', '§'))
                        .replace("%time%", end.toString()));
            else
                target.kickPlayer(Objects.requireNonNull(main.getConfig().getString("messages.ban.banMessage"))
                        .replace('&', '§')
                        .replace("%staff%", sender.getName())
                        .replace("%reason%", reason.replace('&', '§'))
                        .replace("%time%", "permanent"));
        } else { // Target is offline
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            targetUUID = target.getUniqueId().toString();
        }
        if (sender instanceof Player) // IF SENDER IS A PLAYER GET THE UUID
        {
            Player p = (Player) sender;
            staffUUID = p.getUniqueId().toString();
        }
        try { // Insert into the database and ad players to the HashMap to kick on connect
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = null;
            if (time != 0)
                end = now.plusSeconds(time);
            main.hMBannedDatetimeunban.put(target.getUniqueId(), end);
            main.dbModeration.insertBanDatabase(main.statementMod, targetName, targetUUID, reason, sender.getName(),
                    staffUUID, time); // Insert into Database
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("ncls.moderation.ban.see")) {
                    if (time != 0)
                        p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.ban.see"))
                                .replace('&', '§')
                                .replace("%sender%", sender.getName())
                                .replace("%target%", targetName)
                                .replace("%time%", end.toString())
                                .replace("%reason%", reason.replace('&', '§')));
                    else
                        p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.ban.see"))
                                .replace('&', '§')
                                .replace("%sender%", sender.getName())
                                .replace("%target%", targetName)
                                .replace("%time%", "permanent")
                                .replace("%reason%", reason.replace('&', '§')));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Boolean containsLadder(String s) {
        boolean result = false;
        switch (s) {
            case "y":
                result = true;
            case "mo":
                result = true;
            case "d":
                result = true;
            case "h":
                result = true;
            case "m":
                result = true;
            case "s":
                result = true;
        }
        return result;
    }

    private Boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
/*
 * - Check perm
 * - else send message NOPERM
 * - Check if at least 1 arg
 * - else send message NOARGS
 * - Check
 * /ban player -> Ban perm with default reason == 1 Arg
 * ---------------------------------------- CHECK
 * /ban player reason -> ban perm the reason specified == 2 args or more -->
 * Have to check if arg[1] == String ////////////
 * /ban player 1 d -> ban specified time default reason == 3 args
 * ----------------------------- CHECK
 * /ban player 1 s reason -> ban specified time the reason specified == 4 args
 * or more --> Have to check if arg[1] == int ------------------ CHECK
 * 
 * if(sender.haspermission("ncls.*********")){
 * if(args.length >= 1){
 * if(args.length == 1){ /BAN PLAYER
 * Kick target
 * add target UUID to the banned players HashMap (permanent ban)
 * add target UUID to the database for permanent ban
 * }
 * else if(args.length == 3){ /BAN PLAYER 1 D
 * if(args[2] == int && args[3} == String){
 * time = args[2]
 * ladder = args[3]
 * add to the hashmap && add to the database && kick player
 * }
 * else{ /BAN PLAYER REASON
 * ALL BAN STUFF
 * }
 * }
 * else{
 * if(args[2] == String)
 * }
 * }
 * else
 * sender.sendmessage(main.*********)
 * }
 * else
 * sender.sendmessage(main.config.getString("********"))
 * 
 */