package com.on.puz.quicktron;


import com.on.puz.quicktron.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LaunchScreen extends Activity {

	private static final String TEST_NEW = "com.on.puz.quicktron.TEST_NEW";

	public void scanNewTest(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(TEST_NEW, true);
        startActivity(intent);

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
