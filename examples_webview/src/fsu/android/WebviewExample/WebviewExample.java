 package fsu.android.WebviewExample;
 
/**
 * Webview Example
 * 
 * This beginner application demonstrates using a text field to query the web. The layout is
 * also inflated, and a text box added. The button starts a new google search with the search
 * string from the text box.
 * 
 * Possible improvements:
 * + Use a thread to track the loading progress of the webview with getProgress(), and report to a
 * 	 handler when finished. Could change the button from "Searching..." back to "Lookup".
 * + Add browser controls, such as back, forward, and home.
 * + Use a custom HTML layout that pulls data from a website and displays it in a custom way.
 * 
 * 
 * @author	Randy Flood
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;


public class WebviewExample extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Set up layout
        RelativeLayout myLayout = new RelativeLayout(this); 
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
        									(100, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        myLayout.setLayoutParams(params); 
         
        //Inflate layout to add component
        LayoutInflater mainLayoutinfater = this.getLayoutInflater();  
        View mainView= mainLayoutinfater.inflate(R.layout.main, null);
        TextView Credits=new TextView(this);
        Credits.setText("\nRandy Flood");
        Credits.setLayoutParams(params); 
        Credits.bringToFront();
        myLayout.addView(mainView);
        myLayout.addView(Credits);
        setContentView(myLayout);	//update layout for display
 
		final WebView myweb= (WebView) findViewById(R.id.webview);
		myweb.setMinimumHeight(300);	
		myweb.setMinimumWidth(300);
            
        final Button LookupButton = (Button) findViewById(R.id.Lookup);
        final String mimetype =  "text/html";
        final String encoding =  "UTF-8";
		String htmldata = "<html><body>Search for something!</body></html>";
         
        myweb.loadData(htmldata, mimetype, encoding);
        myweb.getSettings().setJavaScriptEnabled(true);
       
        //Makes the textbox select all text when clicked
		final EditText searchBox = (EditText) findViewById(R.id.searchBox);
		searchBox.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				searchBox.selectAll();
			}
		});
		
		//Fires when the button is clicked
        LookupButton.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v)
        	{	
        		String myurl="http://www.google.com/search?&q=";
        		String theurl=myurl.concat(searchBox.getText().toString());
        		searchBox.clearFocus();
        		
        		//hide the virtual keyboard
        		InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        		imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        		
        		myweb.loadUrl(theurl); 		
        	}
        });
    } // end of Main
	
} // end of class