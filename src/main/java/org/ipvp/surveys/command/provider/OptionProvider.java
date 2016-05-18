package org.ipvp.surveys.command.provider;

import com.google.common.base.Joiner;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.ipvp.surveys.Option;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionProvider implements Provider<Option> {

    private Joiner spaceJoiner = Joiner.on(' ');
    
    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public Option get(CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
        List<String> arguments = new ArrayList<>();
        while (commandArgs.hasNext()) {
            arguments.add(commandArgs.next());
        }
        return new Option(spaceJoiner.join(arguments));
    }

    @Override
    public List<String> getSuggestions(String s) {
        return Collections.emptyList();
    }
}
