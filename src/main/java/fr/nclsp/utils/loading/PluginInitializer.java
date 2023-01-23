package fr.nclsp.utils.loading;

import java.util.Objects;

import fr.nclsp.Main;
import fr.nclsp.commands.chat.BlockChat;
import fr.nclsp.commands.chat.Broadcast;
import fr.nclsp.commands.chat.Clearchat;
import fr.nclsp.commands.chat.Msg;
import fr.nclsp.commands.environement.Times;
import fr.nclsp.commands.environement.Weathers;
import fr.nclsp.commands.moderation.Ban;
import fr.nclsp.commands.moderation.Kick;
import fr.nclsp.commands.moderation.Mute;
import fr.nclsp.commands.moderation.Unban;
import fr.nclsp.commands.moderation.Unmute;
import fr.nclsp.commands.teleport.Rtp;
import fr.nclsp.commands.teleport.TeleportRequest;
import fr.nclsp.events.chat.OnChatWithBlockChat;
import fr.nclsp.events.moderation.OnJoinModeration;
import fr.nclsp.events.moderation.OnLeaveModeration;
import fr.nclsp.events.moderation.PlayerChatModeration;

public class PluginInitializer {
    private Main main;

    public PluginInitializer(Main plugin) {
        this.main = plugin;
    }

    public void initializeCommands() {
        // /MSG COMMAND
        if (main.getConfig().getBoolean("config.activation.msg")) {
            Objects.requireNonNull(main.getCommand("msg")).setExecutor(new Msg(main));
        }
        // /CLEARCHAT COMMAND
        if (main.getConfig().getBoolean("config.activation.clearchat")) {
            Objects.requireNonNull(main.getCommand("clearchat")).setExecutor(new Clearchat(main));
        }
        // BLOCKCHAT COMMAND
        if (main.getConfig().getBoolean("config.activation.blockchat")) {
            main.getCommand("blockchat").setExecutor(new BlockChat(main));
            main.getServer().getPluginManager().registerEvents(new OnChatWithBlockChat(main), main);
        }
        // /BROADCAST COMMAND
        if (main.getConfig().getBoolean("config.activation.broadcast")) {
            Objects.requireNonNull(main.getCommand("broadcast")).setExecutor(new Broadcast(main));
        }
        // /TPA /TPYES /TPNO COMMAND
        if (main.getConfig().getBoolean("config.activation.tpa")) {
            Objects.requireNonNull(main.getCommand("tpa")).setExecutor(new TeleportRequest(main));
            Objects.requireNonNull(main.getCommand("tpyes")).setExecutor(new TeleportRequest(main));
            Objects.requireNonNull(main.getCommand("tpno")).setExecutor(new TeleportRequest(main));
        }
        // /BAN /UNBAN COMMAND
        if (main.getConfig().getBoolean("config.activation.ban")) {
            Objects.requireNonNull(main.getCommand("ban")).setExecutor(new Ban(main));
            Objects.requireNonNull(main.getCommand("unban")).setExecutor(new Unban(main));
        }
        // /KICK COMMAND
        if (main.getConfig().getBoolean("config.activation.kick")) {
            Objects.requireNonNull(main.getCommand("kick")).setExecutor(new Kick(main));
        }
        // /MUTE /UNMUTE COMMAND
        if (main.getConfig().getBoolean("config.activation.mute")) {
            Objects.requireNonNull(main.getCommand("mute")).setExecutor(new Mute(main));
            Objects.requireNonNull(main.getCommand("unmute")).setExecutor(new Unmute(main));
        }
        // /RTP COMMAND
        if (main.getConfig().getBoolean("config.activation.rtp")) {
            Objects.requireNonNull(main.getCommand("rtp")).setExecutor(new Rtp(main));
        }
        if (main.getConfig().getBoolean("config.activation.time")) {
            Objects.requireNonNull(main.getCommand("night")).setExecutor(new Times(main));
            Objects.requireNonNull(main.getCommand("day")).setExecutor(new Times(main));
        }
        // /WEATHER COMMAND
        if (main.getConfig().getBoolean("config.activation.weather")) {
            Objects.requireNonNull(main.getCommand("sun")).setExecutor(new Weathers(main));
            Objects.requireNonNull(main.getCommand("rain")).setExecutor(new Weathers(main));
        }
        // JOIN MODERATION
        if (main.getConfig().getBoolean("config.activation.joinmoderation")) {
            main.getServer().getPluginManager().registerEvents(new OnJoinModeration(main), main);
            main.getServer().getPluginManager().registerEvents(new OnLeaveModeration(main), main);
        }
        // CHAT MODERATION
        if (main.getConfig().getBoolean("config.activation.chatmoderation")) {
            main.getServer().getPluginManager().registerEvents(new PlayerChatModeration(main), main);
        }
    }
}