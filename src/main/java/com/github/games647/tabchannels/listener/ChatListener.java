package com.github.games647.tabchannels.listener;

import com.github.games647.tabchannels.Channel;
import com.github.games647.tabchannels.Subscriber;
import com.github.games647.tabchannels.TabChannels;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final TabChannels plugin;

    public ChatListener(TabChannels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent playerChatEvent) {
        Player sender = playerChatEvent.getPlayer();
        String message = playerChatEvent.getMessage();
        String format = playerChatEvent.getFormat();
        String chatMessage = String.format(format, sender.getDisplayName(), message);

        Subscriber subscriber = plugin.getSubscribers().get(sender.getUniqueId());
        Channel messageChannel = subscriber.getCurrentChannel();
        messageChannel.addMessage(chatMessage);

        //Notify changes
        for (UUID recipient : messageChannel.getRecipients()) {
            Subscriber receiver = plugin.getSubscribers().get(recipient);
            if (receiver != null) {
                onNewMessage(recipient, receiver, messageChannel);
            }
        }

        //remove the recipients from normal chats without hiding log messages
        playerChatEvent.getRecipients().clear();
    }

    private void onNewMessage(UUID recipient, Subscriber receiver, Channel messageChannel) {
        Player recipientPlayer = Bukkit.getPlayer(recipient);

        receiver.notifyNewMessage(messageChannel);
        Channel subscriberUsedChannel = receiver.getCurrentChannel();

        recipientPlayer.spigot().sendMessage(messageChannel.getFormattedHeader(messageChannel.getName(recipient)));
        recipientPlayer.spigot().sendMessage(subscriberUsedChannel.getContent());
        recipientPlayer.spigot().sendMessage(receiver.getChannelSelection());
    }
}
