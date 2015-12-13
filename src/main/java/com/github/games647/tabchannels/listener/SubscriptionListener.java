package com.github.games647.tabchannels.listener;

import com.github.games647.tabchannels.Channel;
import com.github.games647.tabchannels.Subscriber;
import com.github.games647.tabchannels.TabChannels;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SubscriptionListener implements Listener {

    private final TabChannels plugin;

    public SubscriptionListener(TabChannels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        plugin.loadPlayer(joinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();
        Subscriber subscriber = plugin.getSubscribers().remove(player.getUniqueId());

        //removes the subscriber from all channels
        for (Channel subscription : subscriber.getSubscriptions()) {
            subscription.removeRecipient(player.getUniqueId());
            if (subscription.isPrivate()) {
                //If it's a private chat remove the subscribtion of the partner too
                //so it's no longer referenced and can be garbage collected
                UUID chatPartner = subscription.getRecipients().get(0);
                Subscriber privateChatSubscriber = plugin.getSubscribers().get(chatPartner);
                privateChatSubscriber.unsubsribe(subscription);
                plugin.getChannels().remove(subscription.getId());
            }
        }
    }
}
