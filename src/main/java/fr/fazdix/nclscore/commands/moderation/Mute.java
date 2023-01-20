package fr.fazdix.nclscore.commands.moderation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.fazdix.nclscore.Main;

public class Mute implements CommandExecutor {
    private final Main main;

    public Mute(Main main) {
        this.main = main;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = Objects.requireNonNull(main.getConfig().getString("messages.prefix")).replace('&', '§');
        if (sender.hasPermission("ncls.moderation.mute")) // Check permission
        {
            if (args.length >= 1) { // Check if args are set
                String reason = Objects.requireNonNull(main.getConfig().getString("messages.mute.defaultReason"))
                        .replace('&', '§'); // Default reason
                String targetName = args[0];
                if (args.length == 1) { // /mute PLAYER
                    muteAndDBAction(targetName, reason, sender, 0);
                } else if (args.length == 3) { // /mute PLAYER 1 d
                    Boolean args1isInt = isInt(args[1]);
                    Boolean args2ContainsLadder = containsLadder(args[2]);
                    if (args1isInt && args2ContainsLadder) { // If both are true: /mute PLAYER 1 D
                        Integer time = Integer.valueOf(args[1]); // We get the time
                        String ladder = args[2]; // we get the ladder (month, days, minutes ...)
                        // Convert the time in seconds
                        int seconds = convertInSeconds(time, ladder);
                        muteAndDBAction(targetName, reason, sender, seconds);
                    } else {
                        // Mute permanent with the specified reason
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        muteAndDBAction(targetName, reason.replace('&', '§'), sender, 0);
                    }
                } else if (args.length >= 4) {
                    Boolean args1isInt = isInt(args[1]);
                    Boolean args2ContainsLadder = containsLadder(args[2]);
                    if (args1isInt && args2ContainsLadder) { // If both are true: /MUTE PLAYER 1 D THE REASON ...
                        Integer time = Integer.valueOf(args[1]); // We get the time
                        String ladder = args[2]; // we get the ladder (month, days, minutes ...)
                        // Convert the time in seconds
                        int seconds = convertInSeconds(time, ladder);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        muteAndDBAction(targetName, reason, sender, seconds);
                    } else {
                        // Mute permanent with the specified reason
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        reason = sb.toString();
                        muteAndDBAction(targetName, reason.replace('&', '§'), sender, 0);
                    }
                }
            } else // NO ARGS
                sender.sendMessage(prefix
                        + Objects.requireNonNull(main.getConfig().getString("messages.mute.noArgs")).replace('&', '§'));
        } else // NO PERM
            sender.sendMessage(prefix
                    + Objects.requireNonNull(main.getConfig().getString("messages.mute.noperm")).replace('&', '§'));
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
    public void muteAndDBAction(String targetName, String reason, CommandSender sender, int time) {
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
                target.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.muteMessage"))
                        .replace('&', '§').replace("%staff%", sender.getName())
                        .replace("%reason%", reason.replace('&', '§')).replace("%time%", end.toString()));
            else
                target.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.muteMessage"))
                        .replace('&', '§').replace("%staff%", sender.getName())
                        .replace("%reason%", reason.replace('&', '§')).replace("%time%", "permanent"));
        } else { // Target is offline
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            targetUUID = target.getUniqueId().toString();
        }
        if (sender instanceof Player) // IF SENDER IS A PLAYER GET THE UUID
        {
            Player p = (Player) sender;
            staffUUID = p.getUniqueId().toString();
        }
        try { // Insert into the database and ad players to the HashMap to mute on connect
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = null;
            if (time != 0)
                end = now.plusSeconds(time);
            main.hMMutedDatetimeunban.put(target.getUniqueId(), end);
            main.dbModeration.insertMuteDatabase(main.statementMod, targetName, targetUUID, reason, sender.getName(),
                    staffUUID, time); // Insert into Database
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("ncls.moderation.mute.see")) {
                    if (time != 0)
                        p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.see"))
                                .replace('&', '§').replace("%sender%", sender.getName()).replace("%target%", targetName)
                                .replace("%time%", end.toString()).replace("%reason%", reason.replace('&', '§')));
                    else
                        p.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.mute.see"))
                                .replace('&', '§').replace("%sender%", sender.getName()).replace("%target%", targetName)
                                .replace("%time%", "permanent").replace("%reason%", reason.replace('&', '§')));
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