package com.on.puz.quicktron;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
	    		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	    		startActivity(intent);
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_launch_screen);
	}
}
