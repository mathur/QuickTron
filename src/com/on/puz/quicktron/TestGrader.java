package com.on.puz.quicktron;

import org.opencv.core.Point;

public class TestGrader {
	
	public String parseFillIn(boolean[][] answerFill) {
		String studentResponse = "";
		for(int i = 0; i < answerFill.length; i++) {
			int fillCount = 0;
			int index = 0;
			for(int j = 0; j < answerFill[i].length; j++) {
				if(answerFill[i][j] == true) {
					index = j;
					fillCount++;
				}
			}
			if(fillCount == 0) {
				studentResponse += 'O';
			} else if (fillCount > 1) {
				studentResponse += 'M';
			} else {
				studentResponse += (char) (index+65);
			}
		}
		return studentResponse;
	}
	
	public double gradeStudentResponse(String studentResponse, String answerKey) {
		int correct = 0;
		int totalScore = answerKey.length();
		for(int i = 0; i < answerKey.length(); i++) {
			if(studentResponse.charAt(i) == answerKey.charAt(i)) {
				correct++;
			} 
		}
		double score = (double) correct/totalScore;
		System.out.println(studentResponse+ " \n" + answerKey);
		return score;
	}
}
