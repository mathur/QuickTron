package com.on.puz.quicktron;

public class Test {
    private int id;
    private String testName;
    private int answerKey;
    private String[] scores;

    /*
     * Constructor that sets up a test of name null
     */
    public Test()
    {
        this.testName=null;
        this.answerKey=0;
    }

    public Test(String newTestName, int isAnswerKey, String[] scoreList) {
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
     * @param itemName is the desired name of the Item object currently being processed
     */
    public void setTestName(String newTestName) {
        this.testName = newTestName;
    }

    /*
     * Method to get the name of a specific Test object
     *
     * @return the name of the object as a string
     */
    public int isAnswerKey(){
        return answerKey;
    }

    /*
     * Method that sets the name of an Item object
     *
     * @param itemName is the desired name of the Item object currently being processed
     */
    public void setIsAnswerKey(int ifAnswerKey) {
        this.answerKey = ifAnswerKey;
    }


    /*
     * Method to get the name of a specific Test object
     *
     * @return the name of the object as a string
     */
    public String getScoresString() {
        String str = "";
        for (int i = 0;i<scores.length; i++) {
            str = str+scores[i];
            // Do not append comma at the end of last element
            if(i<scores.length-1){
                str = str+",";
            }
        }
        return str;
    }

    /*
     * Method that sets the name of an Item object
     *
     * @param itemName is the desired name of the Item object currently being processed
     */
    public void setScores(String newScores) {
        String[] arr = newScores.split(",");
        this.scores = arr;
    }
}
