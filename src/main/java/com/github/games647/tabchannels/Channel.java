package com.github.games647.tabchannels;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

public class Channel {

    //-1 Header line -2 Chat channel selection
    private static final int QUEUE_SIZE = ChatPaginator.OPEN_CHAT_PAGE_HEIGHT - 1 - 2;

    private final String id;
    private final String name;
    private final boolean privateChannel;
    private final List<String> chatHistory = Lists.newArrayListWithExpectedSize(QUEUE_SIZE);
    private final List<UUID> recipients = Lists.newArrayList();

    public Channel(String id, String name, boolean privateChannel) {
        this.id = id;
        this.name = StringUtils.capitalize(name);
        this.privateChannel = privateChannel;
    }

    public Channel(String name, boolean privateChannel) {
        this(name, name, privateChannel);
    }

    public String getId() {
        return id;
    }

    public String getName(UUID self) {
        if (self != null && privateChannel) {
            for (UUID recipient : recipients) {
                if (!self.equals(recipient)) {
                    Player chatPartner = Bukkit.getPlayer(recipient);
                    return chatPartner.getName();
                }
            }
        }

        return name;
    }

    public boolean isPrivate() {
        return privateChannel;
    }

    public void addRecipient(UUID player) {
        recipients.add(player);
    }

    public void removeRecipient(UUID player) {
        recipients.remove(player);
    }

    public List<UUID> getRecipients() {
        return recipients;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public void addMessage(String message) {
        //-1 because of the added space after a line break
        String[] linesToAdd = ChatPaginator.wordWrap(message, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2 - 1);
        int oversize = chatHistory.size() + linesToAdd.length - QUEUE_SIZE;
        for (int i = 1; i <= oversize; i++) {
            //remove the oldest element
            chatHistory.remove(0);
        }

        chatHistory.add(linesToAdd[0]);
        for (int i = 1; i < linesToAdd.length; i++) {
            String messagePart = ' ' + linesToAdd[i];
            chatHistory.add(messagePart);
        }
    }

    public BaseComponent[] getContent() {
        StringBuilder emptyLineBuilder = new StringBuilder();
        for (int i = QUEUE_SIZE - chatHistory.size(); i > 0; i--) {
            emptyLineBuilder.append("\n");
        }

        ComponentBuilder builder = new ComponentBuilder(emptyLineBuilder.toString());

        //chat history
        for (String previousMessage : chatHistory) {
            builder.append(previousMessage).append("\n");
        }

        builder.append(StringUtils.repeat("=", 26)).color(ChatColor.GOLD);
        builder.create();
        return builder.create();
    }

    public BaseComponent[] getFormattedHeader(String headerName) {
        String title = ' ' + headerName + ' ';
        String center = StringUtils.center(title, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2, '=');
        return new ComponentBuilder(center).color(ChatColor.GREEN).create();
    }
}
