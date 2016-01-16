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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    private final TabChannels plugin;

    public ChatListener(TabChannels plugin) {
        this.plugin = plugin;
    }

    //listen to the highest priority in order to let other plugins interpret it as successfull event
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent playerChatEvent) {
        Player sender = playerChatEvent.getPlayer();
        String message = playerChatEvent.getMessage();
        String format = playerChatEvent.getFormat();
        String chatMessage = String.format(format, sender.getDisplayName(), message);

        Subscriber subscriber = plugin.getSubscribers().get(sender.getUniqueId());
        Channel messageChannel = subscriber.getCurrentChannel();
        messageChannel.addMessage(chatMessage);

        notifyChanges(messageChannel);

        //remove the recipients from normal chats without hiding log messages
        playerChatEvent.getRecipients().clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        String joinMessage = joinEvent.getJoinMessage();

        if (joinMessage != null && !joinMessage.isEmpty()) {
            Subscriber subscriber = plugin.getSubscribers().get(player.getUniqueId());
            for (Channel messageChannel : subscriber.getSubscriptions()) {
                messageChannel.addMessage(joinMessage);

                notifyChanges(messageChannel);
            }

            joinEvent.setJoinMessage("");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();
        String quitMessage = quitEvent.getQuitMessage();

        if (quitMessage != null && !quitMessage.isEmpty()) {
            Subscriber subscriber = plugin.getSubscribers().get(player.getUniqueId());
            for (Channel messageChannel : subscriber.getSubscriptions()) {
                messageChannel.addMessage(quitMessage);

                notifyChanges(messageChannel);
            }

            quitEvent.setQuitMessage("");
        }
    }

    private void notifyChanges(Channel messageChannel) {
        for (UUID recipient : messageChannel.getRecipients()) {
            Subscriber receiver = plugin.getSubscribers().get(recipient);
            if (receiver != null) {
                onNewMessage(recipient, receiver, messageChannel);
            }
        }
    }

    private void onNewMessage(UUID recipient, Subscriber receiver, Channel messageChannel) {
        Player recipientPlayer = Bukkit.getPlayer(recipient);

        receiver.notifyNewMessage(messageChannel);
        Channel subscriberUsedChannel = receiver.getCurrentChannel();

        recipientPlayer.spigot().sendMessage(messageChannel.getHeader(messageChannel.getName(recipient)));
        recipientPlayer.spigot().sendMessage(subscriberUsedChannel.getContent());
        recipientPlayer.spigot().sendMessage(receiver.getChannelSelection());
    }
}
