/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.chat.data;

import lombok.Getter;
import lombok.Setter;
import net.cogzmc.chat.ChatManager;
import net.cogzmc.chat.filter.CensoredWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores chat data including
 * personal conversations
 * and previous messages for players
 *
 * <p>
 * Latest Change: Remove mutes.
 * <p>
 *
 * @author Jake
 * @since 12/28/2013
 */
public final class Chat {

    /**
     * Whether or not chat is globally muted
     */
    @Getter @Setter
    public boolean muted = false;

    /**
     * A {@link java.util.List} of {@link net.cogzmc.chat.filter.CensoredWord}s on the server
     */
    @Getter
    public final List<CensoredWord> censoredWords;

    /**
     * The last messages that a {@link org.bukkit.entity.Player} sent, stored by username.
     */
    @Getter private final Map<String, String> lastMessages = new HashMap<>();

    public Chat() {
        this.censoredWords = new ArrayList<>();
        updateCensor();
    }

    /**
     * Updates the list of censored words from the database
     */
    public void updateCensor() {
        String[] censoredWords1 = ChatManager.getInstance().getCensoredWords();
        censoredWords.clear();
        for (String s : censoredWords1) {
            censoredWords.add(new CensoredWord(s));
        }
    }
}
