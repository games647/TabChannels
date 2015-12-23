package com.github.games647.tabchannels.commands;

import com.github.games647.tabchannels.Channel;
import com.github.games647.tabchannels.Subscriber;
import com.github.games647.tabchannels.TabChannels;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class SwitchCommand implements TabExecutor {

    private final TabChannels plugin;

    public SwitchCommand(TabChannels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            //switch the channel
            if (args.length > 0) {
                String channelName = args[0];

                Player player = (Player) sender;
                Subscriber selfSubscriber = plugin.getSubscribers().get(player.getUniqueId());
                Channel channel = plugin.getChannels().get(channelName);
                if (channel == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "A channel with that name doesn't exist");
                } else {
                    selfSubscriber.switchChannel(channel);
                    player.spigot().sendMessage(channel.getFormattedHeader(channel.getName(player.getUniqueId())));
                    player.spigot().sendMessage(channel.getContent());
                    player.spigot().sendMessage(selfSubscriber.getChannelSelection());
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Missing channel name");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "Only players could have chat channels");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Set<Channel> subscriptions = plugin.getSubscribers().get(player.getUniqueId()).getSubscriptions();

            List<String> suggestions = Lists.newArrayList();
            for (Channel channel : subscriptions) {
                //show only channels where the player is already in
                if (channel.getRecipients().contains(player.getUniqueId())) {
                    suggestions.add(channel.getName(((Player) sender).getUniqueId()));
                }
            }

            Collections.sort(suggestions, String.CASE_INSENSITIVE_ORDER);
            return suggestions;
        } else {
            //only players have channels
            return null;
        }
    }
}
