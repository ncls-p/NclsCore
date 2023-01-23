package fr.nclsp.commands.teleport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;
import fr.nclsp.utils.CountdownTimer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Rtp implements CommandExecutor {
    Main main;

    public Rtp(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("nclsp.rtp.use")) {
                if (!(main.hMRtp.containsKey(p)))
                    main.hMRtp.put(p, false);
                if (!main.hMRtp.get(p)) {
                    int maxRange = main.getConfig().getInt("config.parameters.rtp.range");
                    int centerX = main.getConfig().getInt("config.parameters.rtp.center.y");
                    int centerZ = main.getConfig().getInt("config.parameters.rtp.center.z");
                    int randomNumX = ThreadLocalRandom.current().nextInt(centerX - maxRange, centerX + maxRange);
                    int randomNumZ = ThreadLocalRandom.current().nextInt(centerZ - maxRange, centerZ + maxRange);
                    int randomNumY = p.getWorld().getHighestBlockYAt(centerX, centerZ);
                    if (!(p.hasPermission("nclscore.rtp.bypass"))) {
                        int time = 0;
                        if (p.hasPermission("nclscore.rtp.perm1"))
                            time = main.getConfig().getInt("config.parameters.rtp.timer.perm1");
                        if (p.hasPermission("nclscore.rtp.perm2"))
                            time = main.getConfig().getInt("config.parameters.rtp.timer.perm2");
                        if (p.hasPermission("nclscore.rtp.perm3"))
                            time = main.getConfig().getInt("config.parameters.rtp.timer.perm3");
                        if (p.hasPermission("nclscore.rtp.perm4"))
                            time = main.getConfig().getInt("config.parameters.rtp.timer.perm4");
                        int finalTime = time;
                        Runnable beforeTimer = () -> {
                            sender.sendMessage(main.getConfig().getString("messages.rtp.teleport").replace('&', '§')
                                    .replace("%timer%", finalTime + "").replace("%location%",
                                            "X: " + randomNumX + " Y: " + randomNumY + " Z: " + randomNumZ));
                            main.hMRtp.replace(p, true);
                            Location tplocation = new Location(p.getWorld(), randomNumX, randomNumY, randomNumZ);
                            p.teleport(tplocation);
                        };
                        Runnable afterTimer = () -> {
                            sender.sendMessage(main.getConfig().getString("messages.rtp.teleportagain")
                                    .replace('&', '§').replace("%timer%", finalTime + ""));
                            main.hMRtp.replace(p, false);
                        };
                        Consumer<CountdownTimer> everySecond = countdownTimer -> {
                        };
                        CountdownTimer timer = new CountdownTimer(main, time, beforeTimer, afterTimer, everySecond);
                        timer.scheduleTimer();
                    } else {
                        sender.sendMessage(main.getConfig().getString("messages.rtp.teleportbypass").replace('&', '§')
                                .replace("%location%", "X: " + randomNumX + " Y: " + randomNumY + " Z: " + randomNumZ));
                        Location tplocation = new Location(p.getWorld(), randomNumX, randomNumY, randomNumZ);
                        main.hMRtp.replace(p, false);
                        p.teleport(tplocation);
                    }
                } else
                    sender.sendMessage(main.getConfig().getString("messages.rtp.wait").replace('&', '§'));
            } else
                sender.sendMessage(main.getConfig().getString("messages.rtp.noPerm").replace('&', '§'));
        } else
            sender.sendMessage(main.getConfig().getString("messages.rtp.noPlayer").replace('&', '§'));

        return false;
    }
}