package com.on.puz.quicktron;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ViewAllTests extends Activity {
	
	ArrayAdapter<String> testItems;
	EditText testNameInput;
	Button addTestButton;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_view_all_tests);
		//testNameInput = (EditText)findViewById(R.id.input_test_name);
		addTestButton = (Button)findViewById(R.id.add_test_button);
        listView = (ListView)findViewById(R.id.all_tests);
        ArrayList<String> sample = new ArrayList<String>();
        sample.add("blah");
        sample.add("blah2");
        sample.add("blah3");
        testItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        addItemsFromDb();
        listView.setAdapter(testItems);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	//call database stuff
            	Toast.makeText(getApplicationContext(), "button " + position + " clicked", Toast.LENGTH_SHORT).show();
            }

			

        });
	}
	public void addItemsFromDb(){
		//add test names to the list view
		//dummy data:
		for(int i = 0; i < 20; i++)
		{
			testItems.add("test " + (i + 1));
		}
		testItems.notifyDataSetChanged();
	}
	public void createNewTest(String name){
		if (name.length()>0){
			if(isDuplicateTestName(name)){
				Toast.makeText(getApplicationContext(), R.string.duplicate_test_name, Toast.LENGTH_SHORT).show();
			}
				
            //add it to database
			//
        }
		else
			 Toast.makeText(getApplicationContext(), R.string.no_new_test_name, Toast.LENGTH_SHORT).show();
		

    }
	public boolean isDuplicateTestName(String testName){
		//use database
		return false;
	}
	
	public void addNewTest(View v){
		final EditText input = new EditText(this);
		final AlertDialog.Builder newName = new AlertDialog.Builder(this);
		newName.setTitle("New Test Name");
		newName.setView(input);
		AlertDialog alert = newName.create();
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
	        public void onClick(final DialogInterface dialog, final int whichButton) {
	            final String value = input.getText().toString().trim();
	            if (value.length()<=0)
	            	dialog.cancel();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_all_tests, menu);
		return true;
	}
	

}
