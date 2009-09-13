package fsu.android.IntentsExample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IntentsExample extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText editText = (EditText) findViewById(R.id.outgoingText);
		final Button callButton = (Button) findViewById(R.id.callButton);
		
		//Select all text when clicked, for convenience
		editText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editText.selectAll();
			}
		});
		
		//what to do when the button is clicked
		callButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				makeCall(editText.getText().toString());
			}
		});
	}
	
	
	/**
	 * Fires the actual intent to bring up the dialer
	 * 
	 * @param number	the phone number as a string
	 */
	private void makeCall(String number) {
		Uri num = Uri.parse("tel:" + number);
		Intent intent = new Intent(Intent.ACTION_CALL, num);
		this.startActivity(intent);
    }
}