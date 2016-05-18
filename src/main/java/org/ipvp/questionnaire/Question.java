package org.ipvp.questionnaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Question {

    private String name;
    private final String description;
    private List<Option> options;
    
    public Question(String name, String description) {
        Objects.requireNonNull(name, "Question name cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Option> getOptions() {
        return options;
    }
    
    public void addOption(Option option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
    }
}
