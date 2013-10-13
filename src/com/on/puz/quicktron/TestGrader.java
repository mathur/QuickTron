package com.on.puz.quicktron;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class TestGrader {
	public static String parseFillIn(boolean[][] answerFill) {
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
				studentResponse += "O,";
			} else if (fillCount > 1) {
				studentResponse += "M,";
			} else {
				studentResponse += (char) (index+65);
				studentResponse += ",";
			}
			
		}
		return studentResponse;
	}
	
	public static String gradeStudentResponse(String studentResponse, String answerKey) {

		String studentTestResult = "";
		for(int i = 0; i < answerKey.length(); i+=2) {
			if(studentResponse.charAt(i) == answerKey.charAt(i)) {
				studentTestResult += 'C';
			} else if (studentResponse.charAt(i) == 'M'){
				studentTestResult += 'M';
			} else if (studentResponse.charAt(i) == 'O'){
				studentTestResult += 'O';
			} else {
				studentTestResult += 'W';
			}
		}
		return studentTestResult;
	}
	
	public static double scoreStudentResponse(String studentTestResult) {
		int correct = 0;
		int totalScore = studentTestResult.length();
		for(int i = 0; i < totalScore; i++) {
			if (studentTestResult.charAt(i) == 'C') {
				correct++;
			}
		}
		double score = (double) correct/totalScore;
		return score;
	}
}
