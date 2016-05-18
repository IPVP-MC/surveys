package org.ipvp.questionnaire;

import com.google.common.base.Joiner;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.questionnaire.command.QuestionCommands;
import org.ipvp.questionnaire.command.provider.CommandSenderProvider;
import org.ipvp.questionnaire.command.provider.OptionProvider;
import org.ipvp.questionnaire.command.provider.QuestionProvider;
import org.ipvp.questionnaire.command.provider.SurveyProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestionnairePlugin extends JavaPlugin {
        
    private Dispatcher dispatcher;
    
    // Loaded surveys/questions
    private File surveysFile;
    private Map<String, Survey> surveys;
    private Map<String, Question> questions;
    
    // Active conversations
    private Map<UUID, Conversation> conversations = new HashMap<>();

    @Override
    public void onLoad() {
        surveys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        questions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public void onEnable() {
        loadCommands();
        surveysFile = new File(getDataFolder(), "surveys");
        surveysFile.mkdirs();
        for (File file : surveysFile.listFiles((dir, name) -> { return name.endsWith(".yml"); })) {
            YamlConfiguration loaded = YamlConfiguration.loadConfiguration(file);
            Survey survey = new Survey(file.getName().substring(0, file.getName().lastIndexOf('.')));
            List<Map<String, Object>> questions = (List<Map<String, Object>>) loaded.get("questions");
            if (questions != null) {
                for (Map<String, Object> question : questions) {
                    String name = (String) question.get("name");
                    String description = (String) question.get("description");
                    Question q = new Question(name, description);
                    List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
                    if (options != null) {
                        for (Map<String, Object> option : options) {
                            String desc = (String) option.get("description");
                            int votes = ((Number) option.get("votes")).intValue(); 
                            Option o = new Option(desc, votes);
                            q.addOption(o);
                        }
                    }
                    survey.addQuestion(q);
                    this.questions.put(q.getName(), q);
                }
            }
            List<String> filled = (List<String>) loaded.get("filled");
            if (filled != null) {
                survey.setFilled(filled.stream().map(UUID::fromString).collect(Collectors.toSet()));
            }

            this.surveys.put(survey.getName(), survey);
        }
    }
    
    @Override
    public void onDisable() {
        conversations.keySet().forEach(this::removeConversation);
        for (Survey survey : surveys.values()) {
            File file = new File(surveysFile, survey.getName() + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    getLogger().info("Failed to create file: " + file.getPath());
                    continue;
                }
            }
            YamlConfiguration configuration = new YamlConfiguration();
            Set<UUID> whoFilled = survey.getWhoFilled();
            if (whoFilled != null) {
                configuration.set("filled", whoFilled.stream().map(UUID::toString).collect(Collectors.toList()));
            }
            if (survey.getQuestions() != null) {
                List<Map<String, Object>> questions = new ArrayList<>(); // This is ugly
                for (Question question : survey.getQuestions()) {
                    Map<String, Object> serialized = new HashMap<>();
                    serialized.put("description", question.getDescription());
                    serialized.put("name", question.getName());
                    if (question.getOptions() != null) {
                        List<Map<String, Object>> options = new ArrayList<>(); // This is ugly
                        for (Option option : question.getOptions()) {
                            Map<String, Object> serializedOption = new HashMap<>();
                            serializedOption.put("description", option.getDescription());
                            serializedOption.put("votes", option.getVotes());
                            options.add(serializedOption);
                        }
                        serialized.put("options", options);
                    }
                    questions.add(serialized);
                }
                configuration.set("questions", questions);
            }
            try {
                configuration.save(file);
            } catch (IOException e) {
                getLogger().info("Failed to save file: " + file.getPath());
            }
        }
    }

    private void loadCommands() {
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CommandSender.class).toProvider(new CommandSenderProvider());
                bind(Option.class).toProvider(new OptionProvider());
                bind(Survey.class).toProvider(new SurveyProvider(QuestionnairePlugin.this));
                bind(Question.class).toProvider(new QuestionProvider(QuestionnairePlugin.this));
            }
        });
        ParametricBuilder builder = new ParametricBuilder(injector);

        // The authorizer will test whether the command sender has permission.
        builder.setAuthorizer((locals, permission) -> {
            CommandSender sender = locals.get(CommandSender.class);
            return sender != null && sender.hasPermission(permission);
        });

        dispatcher = new CommandGraph()
                .builder(builder)
                .commands()
                .group("questionnaire")
                .registerMethods(new QuestionCommands(this))
                .parent()
                .graph()
                .getDispatcher();
    }
    
    public boolean isConversing(UUID uuid) {
        return conversations.containsKey(uuid);
    }
    
    public void registerConversation(UUID uuid, Conversation conversation) {
        conversations.put(uuid, conversation);
    }
    
    public void removeConversation(UUID uuid) {
        Conversation conversation = conversations.remove(uuid);
        if (conversation != null) {
            conversation.abandon();
        }
    }
    
    public Survey getSurvey(String name) {
        return surveys.get(name);
    }
    
    public Question getQuestion(String name) {
        return questions.containsKey(name) ? questions.get(name) : null;
    }
    
    public void registerSurvey(Survey survey) {
        surveys.put(survey.getName(), survey);
    }
    
    public void registerQuestion(Question question) {
        questions.put(question.getName(), question);
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        // Reconstruct the full command message.
        final String message = command.getName() + " " + StringUtils.join(args, " ");
        final Namespace namespace = new Namespace();

        // The CommandSender is made always available.
        namespace.put(CommandSender.class, sender);

        try {
            // Execute dispatcher with the fully reconstructed command message.
            dispatcher.call(message, namespace, Collections.emptyList());
        } catch (InvalidUsageException e) {
            // Invalid command usage should not be harmful. Print something friendly.
            if (e.isFullHelpSuggested()) {
                // TODO: Send the player the full help menu
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(e.getAliasStack(), e.getCommand().getDescription().getUsage()));
            }
            sender.sendMessage(ChatColor.RED + e.getMessage());
        } catch (AuthorizationException e) {
            // Print friendly message in case of permission failure.
            sender.sendMessage(ChatColor.RED + "Permission denied.");
        } catch (CommandException | InvocationCommandException e) {
            // Everything else is unexpected and should be considered an error.
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    private String getUsage(List<String> aliasStack, String usage) {
        return "/" + Joiner.on(' ').join(aliasStack) + " " + usage;
    }

}
