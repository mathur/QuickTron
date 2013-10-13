package com.on.puz.quicktron;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
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
		 TestDbHelper db = TestDbHelper.getInstance(getApplicationContext());
		//testNameInput = (EditText)findViewById(R.id.input_test_name);
		addTestButton = (Button)findViewById(R.id.add_test_button);
        listView = (ListView)findViewById(R.id.all_tests);
        
        String answerList = "ter,st,e,wfnptas,ds,uy,fwlpu,fnt";
        db.addTest(new Test("test",0, answerList, "liezl.200@gmail.com"));
        
        ArrayList<String> initial = db.getTestNames();
       // ArrayList<Test> initial = new ArrayList<Test>();
        
        //initial.add(new Test("test",0, scores));
        testItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, initial);
        listView.setAdapter(testItems);
        listView.setLongClickable(true);
        
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	//call database stuff
            	String testName = testItems.getItem(position);
            	LinearLayout layout = new LinearLayout(getApplicationContext());
            	ListView testInfo = new ListView(getApplicationContext());
            	LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	
            	ArrayAdapter<String> testInfoEntries = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
            	testInfo.setAdapter(testInfoEntries);
            	layout.addView(testInfo,listParams);
            	
            	LayoutParams scanNewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	Button scanNew = new Button(getApplicationContext());
            	
            	layout.addView(scanNew,scanNewParams);
            	setContentView(layout);
            	Toast.makeText(getApplicationContext(), "button " + position + " clicked", Toast.LENGTH_SHORT).show();
            }			
        });
                listView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		final TextView text = new TextView(getBaseContext());
        		final AlertDialog.Builder alert2 = new AlertDialog.Builder(getBaseContext());
        		alert2.setTitle("Are you sure you want to delete?");
        		alert2.setView(text);
                AlertDialog deleteConfirm = alert2.create();
                
                deleteConfirm.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        	            //delete from database
        				//delete from ArrayAdapter
        				dialog.cancel();
        	        }
        	    });
        		
                deleteConfirm.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int whichButton) {
        	            dialog.cancel();
        	        }
        	    });
                deleteConfirm.show();
				return true;

        	}
        });
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
}