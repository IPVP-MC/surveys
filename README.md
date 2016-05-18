# Surveys

A Bukkit plugin that allows creation of questionnaires for players to answer

### Building

Building Surveys is made simple with maven, simply use the following command:
```
mvn clean install
```

The resulting jar files will be found in the /target folder for each module.

### In-game commands
The subcommands for this plugin are as follows:

* `/surveys start <survey>`
* `/surveys viewresults <survey>`
* `/surveys createsurvey <name>`
* `/surveys createquestion <survey> <name> <description>`
* `/surveys addoption <question> <text>`

### Usage
Creation of a survey follows three simple steps: 
<br>&nbsp;&nbsp; 1. Create a survey with the `createsurvey` sub-command 
<br>&nbsp;&nbsp; 2. Add a question to the survey with the `createquestion` sub-command
<br>&nbsp;&nbsp; 3. Add an option to the question using the `addoption` sub-command 

##### Usage Example
Let's quickly walk through the creation of a survey asking people about their favorite flavor of ice-cream. 

<b>Step 1</b>. Let's create a survey for players to answer.
<br>
![Creating a survey](http://i.imgur.com/7et1kQE.png)

<b>Step 2</b>. Now that we've created the survey, we can create questions. Let's find out what flavor people prefer.
<br>
![Adding a question](http://i.imgur.com/JU4QxUI.png)

<b>Step 3</b>. Asking a question is great, but people need to have something to reply to. Let's add some choices.
<br>
![Adding an option](http://i.imgur.com/jj2sqnf.png)

We can repeat steps 2 and 3 as many times as we want to add questions and options. Now that we're done our questionnaire, players can answer it with the `/surveys start ice-cream` command. The results can be viewed with `/surveys viewresults ice-cream`.
