package org.ipvp.surveys.command.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.ipvp.surveys.Question;
import org.ipvp.surveys.SurveyPlugin;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class QuestionProvider implements Provider<Question> {

    private SurveyPlugin plugin;
    
    public QuestionProvider(SurveyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public Question get(CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
        String name = commandArgs.next();
        Question question = plugin.getQuestion(name);
        if (question == null) {
            throw new ProvisionException("No question with that name exists");
        }
        return question;
    }

    @Override
    public List<String> getSuggestions(String s) {
        return Collections.emptyList();
    }
}
