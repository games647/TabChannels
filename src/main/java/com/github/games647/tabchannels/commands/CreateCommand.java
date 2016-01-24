package com.github.games647.tabchannels.commands;

import com.github.games647.tabchannels.Channel;
import com.github.games647.tabchannels.Subscriber;
import com.github.games647.tabchannels.TabChannels;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand implements CommandExecutor {

    private final TabChannels plugin;

    public CreateCommand(TabChannels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String channelName = args[0];
            if (plugin.getChannels().containsKey(channelName)) {
                sender.sendMessage(ChatColor.DARK_RED + "The channel with this name already exists");
            } else {
                Channel newChannel = new Channel(channelName, false);
                plugin.getChannels().put(channelName, newChannel);
                sender.sendMessage(ChatColor.DARK_GREEN + "Channel created");
                if (sender instanceof Player) {
                    UUID uniqueId = ((Player) sender).getUniqueId();
                    Subscriber subscriber = plugin.getSubscribers().get(uniqueId);
                    if (subscriber != null) {
                        subscriber.subscribe(newChannel);
                        sender.sendMessage(ChatColor.DARK_GREEN + "You auto joined this channel");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "You have to provide the channel name you want to create");
        }

        return true;
    }
}
