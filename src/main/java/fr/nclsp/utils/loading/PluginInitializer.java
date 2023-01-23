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
    private Main plugin;

    public PluginInitializer(Main plugin) {
        this.plugin = plugin;
    }

    public void initializeCommands() {
        // /MSG COMMAND
        if (plugin.getConfig().getBoolean("config.activation.msg")) {
            Objects.requireNonNull(plugin.getCommand("msg")).setExecutor(new Msg(plugin));
        }
        // /CLEARCHAT COMMAND
        if (plugin.getConfig().getBoolean("config.activation.clearchat")) {
            Objects.requireNonNull(plugin.getCommand("clearchat")).setExecutor(new Clearchat(plugin));
        }
        // BLOCKCHAT COMMAND
        if (plugin.getConfig().getBoolean("config.activation.blockchat")) {
            plugin.getCommand("blockchat").setExecutor(new BlockChat(plugin));
            plugin.getServer().getPluginManager().registerEvents(new OnChatWithBlockChat(plugin), plugin);
        }
        // /BROADCAST COMMAND
        if (plugin.getConfig().getBoolean("config.activation.broadcast")) {
            Objects.requireNonNull(plugin.getCommand("broadcast")).setExecutor(new Broadcast(plugin));
        }
        // /TPA /TPYES /TPNO COMMAND
        if (plugin.getConfig().getBoolean("config.activation.tpa")) {
            Objects.requireNonNull(plugin.getCommand("tpa")).setExecutor(new TeleportRequest(plugin));
            Objects.requireNonNull(plugin.getCommand("tpyes")).setExecutor(new TeleportRequest(plugin));
            Objects.requireNonNull(plugin.getCommand("tpno")).setExecutor(new TeleportRequest(plugin));
        }
        // /BAN /UNBAN COMMAND
        if (plugin.getConfig().getBoolean("config.activation.ban")) {
            Objects.requireNonNull(plugin.getCommand("ban")).setExecutor(new Ban(plugin));
            Objects.requireNonNull(plugin.getCommand("unban")).setExecutor(new Unban(plugin));
        }
        // /KICK COMMAND
        if (plugin.getConfig().getBoolean("config.activation.kick")) {
            Objects.requireNonNull(plugin.getCommand("kick")).setExecutor(new Kick(plugin));
        }
        // /MUTE /UNMUTE COMMAND
        if (plugin.getConfig().getBoolean("config.activation.mute")) {
            Objects.requireNonNull(plugin.getCommand("mute")).setExecutor(new Mute(plugin));
            Objects.requireNonNull(plugin.getCommand("unmute")).setExecutor(new Unmute(plugin));
        }
        // /RTP COMMAND
        if (plugin.getConfig().getBoolean("config.activation.rtp")) {
            Objects.requireNonNull(plugin.getCommand("rtp")).setExecutor(new Rtp(plugin));
        }
        if (plugin.getConfig().getBoolean("config.activation.time")) {
            Objects.requireNonNull(plugin.getCommand("night")).setExecutor(new Times(plugin));
            Objects.requireNonNull(plugin.getCommand("day")).setExecutor(new Times(plugin));
        }
        // /WEATHER COMMAND
        if (plugin.getConfig().getBoolean("config.activation.weather")) {
            Objects.requireNonNull(plugin.getCommand("sun")).setExecutor(new Weathers(plugin));
            Objects.requireNonNull(plugin.getCommand("rain")).setExecutor(new Weathers(plugin));
        }
        // JOIN MODERATION
        if (plugin.getConfig().getBoolean("config.activation.joinmoderation")) {
            plugin.getServer().getPluginManager().registerEvents(new OnJoinModeration(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new OnLeaveModeration(plugin), plugin);
        }
        // CHAT MODERATION
        if (plugin.getConfig().getBoolean("config.activation.chatmoderation")) {
            plugin.getServer().getPluginManager().registerEvents(new PlayerChatModeration(plugin), plugin);
        }
    }
}