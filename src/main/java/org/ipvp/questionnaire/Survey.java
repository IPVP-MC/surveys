package org.ipvp.questionnaire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Survey {

    private String name;
    private List<Question> questions;
    private Set<UUID> whoFilled;
    
    public Survey(String name) {
        Objects.requireNonNull(name, "Survey name must not be null");
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public Set<UUID> getWhoFilled() {
        return whoFilled;
    }
    
    public void addToFilled(UUID who) {
        if (whoFilled == null) {
            whoFilled = new HashSet<>();
        }
        whoFilled.add(who);
    }
    
    void setFilled(Set<UUID> who) {
        this.whoFilled = who;
    }
    
    public void addQuestion(Question question) {
        if (questions == null){
            questions = new ArrayList<>();
        }
        questions.add(question);
    }
}
