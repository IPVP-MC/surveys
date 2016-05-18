package org.ipvp.questionnaire.conversation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.ipvp.questionnaire.Option;
import org.ipvp.questionnaire.Question;
import org.ipvp.questionnaire.QuestionnairePlugin;
import org.ipvp.questionnaire.Survey;

import java.util.HashSet;
import java.util.Set;

public class SurveyPrompt extends NumericPrompt {

    private QuestionnairePlugin plugin;
    private final Survey survey;
    private int currentQuestion = 0;
    private Set<Option> answers = new HashSet<>();
    
    SurveyPrompt(QuestionnairePlugin plugin, Survey survey) {
        this.plugin = plugin;
        this.survey = survey;
    }
    
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
        int answer = input.intValue() - 1; // Change to index based number with -1 
        Question current = survey.getQuestions().get(currentQuestion);
        if (current.getOptions() == null) {
            return Prompt.END_OF_CONVERSATION;
        } else if (answer < 0 || current.getOptions().size() <= answer) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Answer must be between 1 and " + current.getOptions().size());
            return this;
        } 
        
        // Temporarily cache the answer (just in case they decide to exit the survey)
        Option option = current.getOptions().get(answer);
        answers.add(option);
        
        // Check if there is a question after the answered one
        if (++currentQuestion < survey.getQuestions().size()) {
            return this;
        }
        
        // Increment all option votes
        answers.forEach(Option::incrementVotes);
        
        // Finish the survey
        Player player = (Player) context.getForWhom();
        survey.addToFilled(player.getUniqueId());
        player.sendRawMessage(ChatColor.GOLD + "Thank you for filling out the survey!");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + player.getName() + " 1"); // Ghetto: Give a life for now
        plugin.removeConversation(player.getUniqueId());
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Question question = survey.getQuestions().get(currentQuestion);
        Conversable who = context.getForWhom();
        who.sendRawMessage(ChatColor.GREEN + "Question #" + (currentQuestion + 1) + "/" + survey.getQuestions().size() 
                + ": " + ChatColor.WHITE + question.getDescription());
        int option = 1;
        for (Option o : question.getOptions()) {
            who.sendRawMessage("  " + option + ". " + o.getDescription());
            option++;
        }
        return ChatColor.GREEN + "Type the number corresponding to your answer in chat.";
    }
}
