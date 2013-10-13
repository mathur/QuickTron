package com.on.puz.quicktron;

import com.github.sendgrid.SendGrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LaunchScreen extends Activity {

	public void scanNewTest(View view)
	{
		final EditText input = new EditText(this);
		final AlertDialog.Builder newName = new AlertDialog.Builder(this);
		newName.setTitle("New Test Name");
		newName.setView(input);
		AlertDialog alert = newName.create();
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
	        public void onClick(final DialogInterface dialog, final int whichButton) {
	            final String value = input.getText().toString().trim();
	            if (value.length()<=0){
	            	Toast.makeText(getApplicationContext(), R.string.no_new_test_name, Toast.LENGTH_SHORT).show();
	            	dialog.cancel();
	            }
	            else{
		    		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		    		startActivity(intent);
	            }
	        }
	    });

		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            dialog.cancel();
	        }
	    });
		alert.show();
	}
	public void viewTestsList(View view)
	{
		Intent intent = new Intent(this, ViewAllTests.class);
        startActivity(intent);
	}

    public void sendSendGrid(View view)
    {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    SendGrid sendgrid = new SendGrid("rohan32", "hackru");
                    sendgrid.addTo("rohanmathur34@gmail.com");
                    sendgrid.setFrom("rohan@rmathur.com");
                    sendgrid.setSubject("Subj");
                    sendgrid.setText("Hi");
                    sendgrid.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void showDirections(View view)
    {
        final AlertDialog.Builder newName = new AlertDialog.Builder(this);
        newName.setTitle("Directions");
        newName.setMessage("For best results, place scantron on a table and hold phone a foot above it, parallel to table.\nGrading should not take more than a few seconds.\nConfirm grading to email results to student.");
        AlertDialog alert = newName.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch_screen);
	}
}
