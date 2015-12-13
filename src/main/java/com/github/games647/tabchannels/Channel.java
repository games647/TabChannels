package com.github.games647.tabchannels;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

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
        this.name = name;
        this.privateChannel = privateChannel;
    }

    public Channel(String name, boolean privateChannel) {
        this(name, name, privateChannel);
    }

    public String getId() {
        return id;
    }

    public String getName() {
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
        if (chatHistory.size() >= QUEUE_SIZE) {
            //remove the oldest element
            chatHistory.remove(0);
        }

        chatHistory.add(message);
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

        builder.append(addSeperator(26).toString()).color(ChatColor.GOLD);
        builder.create();
        return builder.create();
    }

    public BaseComponent[] getFormattedHeader(String headerName) {
        return new ComponentBuilder(buildHeader(headerName)).color(ChatColor.GREEN).create();
    }

    private String buildHeader(String headerName) {
        //-2 because of a single space before and after the channel name
        int remainingWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - headerName.length() - 2;

        StringBuilder header = new StringBuilder(ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
        //set a prefix
        header.append(addSeperator(remainingWidth / 2));

        header.append(' ').append(headerName).append(' ');

        //set the suffix
        header.append(addSeperator(remainingWidth / 2));

        return header.toString();
    }

    private StringBuilder addSeperator(int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 1; i <= size; i++) {
            builder.append('=');
        }

        return builder;
    }
}
