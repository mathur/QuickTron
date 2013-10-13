package com.on.puz.quicktron;

public class Test {
    private int id;
    private String testName;
    private int answerKey;
    private String scores;
    private String email;

    /*
     * Constructor that sets up a test of name null
     */
    public Test()
    {
        this.testName=null;
        this.answerKey=0;
    }

    /*
     * Constructor that sets up a test with specified parameters
     */
    public Test(String newTestName, int isAnswerKey, String answerList, String newEmail) {
        super();
        this.testName = newTestName;
        this.answerKey = isAnswerKey;
        this.scores = answerList;
        this.email = newEmail;
    }

    /*
     * Method to get the Id of a specific Test object
     *
     * @return id of the object
     */
    public int getId() {
        return id;
    }

    /*
     * Method to set the Id of a specific Test object
     *
     * SHOULD NEVER EVER EVER EVER EVER EVER BE USED!!!!!
     *
     * @param id is the desired id for the object currently being processed
     */
    public void setId(int id) {
        this.id = id;
    }

    /*
     * Method to get the name of a specific Test object
     *
     * @return the name of the object as a string
     */
    public String getName() {
        return testName;
    }

    /*
     * Method that sets the name of an Item object
     *
     * @param newTestName is the desired name of the Test object currently being processed
     */
    public void setTestName(String newTestName) {
        this.testName = newTestName;
    }

    /*
     * Method to get whether it is an answer key or not
     *
     * 0 is no, 1 is yes
     *
     * @return 0 if it is not an answer key, 1 if it is
     */
    public int isAnswerKey(){
        return answerKey;
    }

    /*
     * Method that sets if it is an answer key or not
     *
     * @param ifAnswerKey is an answer key (0 = no, 1 = yes)
     */
    public void setIsAnswerKey(int ifAnswerKey) {
        this.answerKey = ifAnswerKey;
    }


    /*
     * Method to return the scores in a string in CSV format
     *
     * @return the scores for each question, with each question being part of the string
     */
    public String getScoresString() {
        return scores;
    }

    /*
     * Method that sets the scores of a test object, in CSV format
     *
     * @param newScores is the new set of scores to add to this item object, as a string
     */
    public void setScores(String newScores) {
        scores = newScores;
    }

    /*
     * Method to get the name of a specific Test object
     *
     * @return the scores for each question, with each question being part of the string
     */
    public String getEmail() {
        return email;
    }

    /*
     * Method that sets the name of an Test object
     *
     * @param newScores is the new set of scores to add to this item object, as a string
     */
    public void setEmail(String newEmail) {
        scores = newEmail;
    }

    public String toString(){
    	return testName;
    }
}
