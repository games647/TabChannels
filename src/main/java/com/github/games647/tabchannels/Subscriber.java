package com.github.games647.tabchannels;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;

public class Subscriber {

    private static final String CHANNEL_SEPERATOR = " || ";

    private Channel currentChannel;
    private Map<Channel, MutableInt> unreadChannels = Maps.newHashMapWithExpectedSize(5);

    public Subscriber(Channel global) {
        this.currentChannel = global;
        this.unreadChannels.put(global, new MutableInt());
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public Set<Channel> getSubscriptions() {
        return unreadChannels.keySet();
    }

    public void subscribe(Channel toSubscribe) {
        unreadChannels.put(toSubscribe, new MutableInt());
    }

    public void unsubsribe(Channel toSubscribe) {
        unreadChannels.remove(toSubscribe);
    }

    public boolean switchChannel(Channel channel) {
        //test if the player is in this channel
        if (unreadChannels.containsKey(channel)) {
            this.currentChannel = channel;
            //mark all messages as read
            unreadChannels.get(channel).setValue(0);
            return true;
        }

        return false;
    }

    public void notifyNewMessage(Channel fromChannel) {
        if (!fromChannel.equals(currentChannel)) {
            MutableInt missedMessages = unreadChannels.get(fromChannel);
            if (missedMessages != null) {
                missedMessages.increment();
            }
        }
    }

    public int getUnreadMessages(Channel channel) {
        MutableInt result = unreadChannels.get(channel);
        if (result == null) {
            return -1;
        }

        return result.intValue();
    }

    public BaseComponent[] getChannelSelection() {
        ComponentBuilder builder = new ComponentBuilder(" ");
        builder.append(StringUtils.capitalize(currentChannel.getName())).bold(true).color(ChatColor.GREEN);

        for (Map.Entry<Channel, MutableInt> entry : unreadChannels.entrySet()) {
            Channel channel = entry.getKey();
            int unreadMessage = entry.getValue().intValue();
            if (!channel.equals(currentChannel)) {
                builder.append(CHANNEL_SEPERATOR).reset();
                builder.append(StringUtils.capitalize(channel.getName()))
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/switch " + channel.getId()));

                //show the number of unread messages
                if (unreadMessage > 0) {
                    builder.append("(").reset()
                            .append(Integer.toString(unreadMessage)).color(ChatColor.YELLOW)
                            .append(")").reset();
                }
            }
        }

        return builder.create();
    }
}
