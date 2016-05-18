package org.ipvp.surveys;

import java.util.Objects;

public class Option {

    private final String description;
    private int votes;

    public Option(String description) {
        this(description, 0);
    }

    public Option(String description, int votes) {
        Objects.requireNonNull(description, "Description cannot be null");
        this.description = description;
        this.votes = votes;
    }

    public String getDescription() {
        return description;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
    
    public void incrementVotes() {
        setVotes(votes + 1);
    }
}
