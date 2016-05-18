package org.ipvp.surveys.conversation;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.ipvp.surveys.SurveyPlugin;
import org.ipvp.surveys.Survey;

public class PromptFactory {

    public static Conversation startQuestionnaire(final SurveyPlugin plugin, Player player, Survey survey) {
        Conversation converstion = new ConversationFactory(plugin)
                .withModality(true) // Suppress messages
                .withLocalEcho(false)
                .withTimeout(120)
                .withFirstPrompt(new SurveyPrompt(plugin, survey))
                .withEscapeSequence("exit")
                .withEscapeSequence("quit")
                .withEscapeSequence("stop")
                .withEscapeSequence("cancel")
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit()) {
                        event.getContext().getForWhom().sendRawMessage(ChatColor.LIGHT_PURPLE + "The survey has finished");
                    }
                    plugin.removeConversation(((Player) event.getContext().getForWhom()).getUniqueId());
                })
                .buildConversation(player);
        converstion.begin();
        return converstion;
    }
}
