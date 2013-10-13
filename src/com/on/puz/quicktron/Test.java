package com.on.puz.quicktron;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    private int id;
    private String testName;
    private int answerKey;
    private ArrayList<String> scores;

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
    public Test(String newTestName, int isAnswerKey, ArrayList<String> scoreList) {
        super();
        this.testName = newTestName;
        this.answerKey = isAnswerKey;
        this.scores = scoreList;
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
     * Method to get the name of a specific Test object
     *
     * @return the scores for each question, with each question being part of the string
     */
    public String getScoresString() {
        String str = "";
        for (int i = 0;i<scores.size(); i++) {
            str = str+scores.get(i);
            // Do not append comma at the end of last element
            if(i<scores.size()-1){
                str = str+",";
            }
        }
        return str;
    }

    /*
     * Method that sets the name of an Test object
     *
     * @param newScores is the new set of scores to add to this item object, as a string
     */
    public void setScores(String newScores) {
        ArrayList<String> arr = new ArrayList<String>(Arrays.asList(newScores.split(",")));
        this.scores = arr;
    }
    public String toString(){
    	return testName;
    }
}
