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
        Channel currentChannel = subscriber.getCurrentChannel();
        currentChannel.addMessage(chatMessage);

        //channel selection
        for (UUID recipient : currentChannel.getRecipients()) {
            Subscriber receiver = plugin.getSubscribers().get(recipient);
            if (receiver != null) {
                Player recipientPlayer = Bukkit.getPlayer(recipient);

                receiver.notifyNewMessage(currentChannel);
                Channel subscriberUsedChannel = receiver.getCurrentChannel();

                sendHeader(recipientPlayer, subscriberUsedChannel);
                recipientPlayer.spigot().sendMessage(subscriberUsedChannel.getContent());
                recipientPlayer.spigot().sendMessage(receiver.getChannelSelection());
            }
        }

        //remove the recipients from normal chats without hiding log messages
        playerChatEvent.getRecipients().clear();
    }

    private void sendHeader(Player recipientPlayer, Channel currentChannel) {
        String header = currentChannel.getName();
        if (currentChannel.isPrivate()) {
            for (UUID recipient : currentChannel.getRecipients()) {
                if (!recipientPlayer.getUniqueId().equals(recipient)) {
                    //found the chat partner who is different to the receiver of this message
                    header = Bukkit.getPlayer(recipient).getName();
                    break;
                }
            }
        }

        recipientPlayer.spigot().sendMessage(currentChannel.getFormattedHeader(header));
    }
}
