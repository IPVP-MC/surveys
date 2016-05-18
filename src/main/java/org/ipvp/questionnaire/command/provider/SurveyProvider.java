package org.ipvp.questionnaire.command.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.ipvp.questionnaire.Survey;
import org.ipvp.questionnaire.QuestionnairePlugin;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class SurveyProvider implements Provider<Survey> {

    private QuestionnairePlugin plugin;
    
    public SurveyProvider(QuestionnairePlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public Survey get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next();
        Survey survey = plugin.getSurvey(name);
        if (survey == null) {
            throw new ProvisionException("That survey doesn't exist");
        }
        return survey;
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return Collections.emptyList();
    }
}
