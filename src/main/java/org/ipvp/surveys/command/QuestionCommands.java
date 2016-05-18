package org.ipvp.surveys.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.ipvp.surveys.Option;
import org.ipvp.surveys.Question;
import org.ipvp.surveys.SurveyPlugin;
import org.ipvp.surveys.Survey;
import org.ipvp.surveys.conversation.PromptFactory;

import java.util.List;

public class QuestionCommands {
    
    private SurveyPlugin plugin;
    
    public QuestionCommands(SurveyPlugin plugin) {
        this.plugin = plugin;
    }
    
    // Admin commands
    // Commands to create new questions/surveys/options
    
    @Command(
            aliases = { "createquestion", "createq", "cquestion", "newquestion", "addquestion" },
            desc = "Create a new question",
            usage = "", // TODO
            min = 3
    )
    @Require("questionnaire.createquestion")
    public void createQuestion(CommandSender sender, Survey survey, String name, @Text String description) {
        if (plugin.getQuestion(name) != null) {
            sender.sendMessage(ChatColor.RED + "A question with that name already exists");
        } else {
            Question question = new Question(name, description);
            plugin.registerQuestion(question);
            survey.addQuestion(question);
            sender.sendMessage(ChatColor.GREEN + "Created a new question with name '" + name + "'");
        }
    }

    @Command(
            aliases = { "createsurvey", "creates", "newsurvey", "addsurvey" } ,
            desc = "Create an empty survey",
            usage = "", // TODO
            min = 1
    )
    @Require("questionnaire.createsurvey")
    public void createSurvey(CommandSender sender, String name) {
        if (plugin.getSurvey(name) != null) {
            sender.sendMessage(ChatColor.RED + "A survey with that name already exists");
        } else {
            Survey survey = new Survey(name);
            plugin.registerSurvey(survey);
            sender.sendMessage(ChatColor.GREEN + "Created a new survey with name '" + name + "'");
        }
    }

    @Command(
            aliases = { "addoption", "newoption", "createoption" } ,
            desc = "Create and add a new option to an existing question",
            usage = "", // TODO
            min = 2
    )
    @Require("questionnaire.addoption")
    public void addNewOption(CommandSender sender, Question question, Option option) { 
        question.addOption(option);
        sender.sendMessage(ChatColor.GREEN + "Added option '" + option.getDescription() + "' to question '" + question.getName() + "'");
    }
    
    @Command(
            aliases = { "results", "viewresults", "showresults" } ,
            desc = "View the results of a questionnaire",
            usage = "", // TODO
            min = 1
    )
    @Require("questionnaire.viewresults")
    public void addNewOption(CommandSender sender, Survey survey) {
        if (survey.getQuestions() == null) {
            sender.sendMessage(ChatColor.RED + "That survey has no questions!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Displaying results for survey '" + survey.getName() + "':");
             survey.getQuestions().forEach(q -> {
                 sender.sendMessage("Question: '" + q.getDescription() + "'");
                 if (q.getOptions() == null) {
                     sender.sendMessage(ChatColor.RED + "  - Question has no options");
                 } else {
                     List<Option> options = q.getOptions();
                     for (int i = 0 ; i < options.size() ; i++) {
                         Option o = options.get(i);
                         sender.sendMessage("  " + (i + 1) + ". " + o.getDescription() + " - " + ChatColor.GREEN + o.getVotes() + " vote(s)");
                     }
                 }
             });
        }
    }
    
    // User commands
    
    @Command(
            aliases = { "start", "begin", "s", "strt" },
            desc = "Start a questionnaire",
            min = 1
    )
    @Require("questionnaire.start")
    public void startQuestionnaire(CommandSender sender, Survey survey) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can answer surveys");
        } else {
            Player player = (Player) sender;
            if (survey.getQuestions() == null) {
                sender.sendMessage(ChatColor.RED + "That survey has no questions added it");
            } else if (survey.getWhoFilled().contains(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You have already filled out this survey!");
            } else if (plugin.isConversing(player.getUniqueId())) {
                player.sendRawMessage(ChatColor.RED + "You are already filling out a survey!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting survey '" + survey.getName() + "'...");
                sender.sendMessage(ChatColor.GRAY + "Chat will be muted for the duration of the survey");
                sender.sendMessage(ChatColor.GRAY + "You may type '" + ChatColor.RED + "stop" + ChatColor.GRAY + "' at any point to exit");
                Conversation conversation = PromptFactory.startQuestionnaire(plugin, player, survey);
                plugin.registerConversation(player.getUniqueId(), conversation);
            }
        }
    }


}