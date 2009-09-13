package fsu.android.ProviderExample;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.Phones;
import android.webkit.WebView;


public class ProviderExample extends Activity {
	final String charHeader = "<html><Title>Characters and Players</Title>The character names " +
							  "below were taken from the custom ContentProvider. The phone numbers " +
							  "and names of people were taken out of the contacts list.<br><br>" +
							  "<TABLE><tr><td>Name</td><td>Player</td><td>Phone #</td></tr><tr></tr>";
	
	final String contactHeader = "This is the entire contact list:<br>" +
								 "<table><tr><td>Name</td><td>Phone Number</td><td>ID</td></tr>";
	
	final Uri allNames = Uri.parse("content://fsu.android.ProviderExample.CharacterProvider/characters");
	 
@Override
public void onCreate(Bundle savedInstanceState) {
	String dHTML = charHeader;	//starting point for appended content
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
    generateData();		//for this example, generate and insert some test data
	final WebView myweb = (WebView) findViewById(R.id.Output);
	
	
	//Get character data
	Cursor c = managedQuery(allNames, null, null, null, null);
	if (c.moveToFirst()) {
		do {
			String characterName = c.getString(CharacterProvider.cName);
			String playerID = c.getString(CharacterProvider.cPlayerid);
			Cursor contact = findContactsByID(playerID);

			if (contact.moveToFirst()) {
				String playerName = contact.getString(contact.getColumnIndex(Phones.NAME));
				String playerNumber = contact.getString(contact.getColumnIndex(Phones.NUMBER));
//playerNumber = 	make this a formated phone number
				dHTML += "<tr><td>" + characterName +
						 "</td><td>" + playerName +
						 "</td><td>" + playerNumber + "</tr>";
			}
			
		} while (c.moveToNext());
	}
	dHTML += "</TABLE>";		//open tags need to be closed now
		
	
	//get full contact list
	String[] projection = new String[] {
		Phones._ID,
		Phones.NAME,
		Phones.NUMBER
	};
	
	Cursor managedCursor = managedQuery(
		Phones.CONTENT_URI,
        projection,				// Which columns to return 
        null,      				// Which rows to return (all rows)
        null,				    // Selection arguments (none)
        Phones.NAME + " ASC"	// Put the results in ascending order by name
	);
	dHTML += contactHeader;
		
	if (managedCursor.moveToFirst()) {
		String contactname; 
		String contactphoneNumber; 
		String contactID;
		int nameColumn = managedCursor.getColumnIndex(Phones.NAME); 
		int phoneColumn = managedCursor.getColumnIndex(Phones.NUMBER);
		int contactIdColumn = managedCursor.getColumnIndex(Phones._ID);
		
		do {
    	  // Get the field values
    	  contactname = managedCursor.getString(nameColumn);
          contactphoneNumber = managedCursor.getString(phoneColumn);
          contactID = managedCursor.getString(contactIdColumn);
          
          if (contactname != null) {
        	  dHTML += "<TR><td>" + contactname+ "</td><td>" + contactphoneNumber +  "</td><td>" +
        	  contactID + "</td></tr>";
          }

		} while (managedCursor.moveToNext());

		dHTML += "</table>";
	}
	
	dHTML +="</html>";
	myweb.loadData(dHTML, "text/html", "UTF-8");
} //end of OnCreate()

/**
 * Finds contacts by searching with a phone number. The number may be
 * partial or full. If no contacts are found, the returned cursor will
 * be empty.
 * 
 * @param number	the number to look up
 * @return			cursor of results
 */
public Cursor findContactsByNumber(String number) {
	String[] projection = new String[] {Phones.NUMBER};
	Uri result = Uri.withAppendedPath(Phones.CONTENT_FILTER_URL, Uri.encode(number));

	return getContentResolver().query(
			result,								// the query
			projection,							// which columns to return
			null,								// which rows to return (null = all)
			null,								// selection arguments (already inside query URI)
			Phones.NUMBER_KEY + " ASC"	// sort order for results (ascending by normalized number)
	);
}

/**
 * Finds contacts by searching with a full or partial contact
 * ID. This is the unique ID stored in the contacts table.
 * The returned cursor contains the following fields:
 * 		_ID
 * 		NAME
 * 		DISPLAY_NAME
 * 		NUMBER
 * 		NUMBER_KEY
 * 		ISPRIMARY
 * 
 * @param id	the number to lookup
 * @return		cursor of results
 */
public Cursor findContactsByID(String id) {
	String[] projection = new String[] {
		Phones._ID,
		Phones.NAME,
		Phones.DISPLAY_NAME,
		Phones.NUMBER,
		Phones.NUMBER_KEY,
		Phones.ISPRIMARY,
	};
	
	return getContentResolver().query(
			Phones.CONTENT_URI,				// the query
			projection,						// which columns to return
			"phones._id="+id,				// which rows to return (WHERE phones._id = xxx)
			null,							// selection arguments (already inside query URI)
			Phones._ID + " ASC"				// sort order for results (ascending by ID)
	);
}


/**
 * Generates test data for this application. If the contacts are found (by number)
 * already existing in the phone book, then the data is not added.
 */
private void generateData() {
	final String NUM1 = "18232145674";
	final String NUM2 = "1900411574";
	ContentValues values = null;
	ContentValues avalues = null;
	Uri personURI, infoURI;
	String pathLeaf;
	
	
	//look for first contact
	Cursor cur = findContactsByNumber(NUM1);
	
	if (!cur.moveToFirst()) {	//the contact number doesn't exist (and so shouldn't the contact)
		values = new ContentValues();
	    values.put(Phones.NAME, "Joe Clinton");
	    personURI = Contacts.People.createPersonInMyContactsGroup(getContentResolver(), values);	//create contact
	    infoURI = Uri.withAppendedPath(personURI, Contacts.People.Phones.CONTENT_DIRECTORY);
	    pathLeaf = personURI.getLastPathSegment();				//the last value is the ID number here
	    values.clear();
	    values.put(Phones.TYPE, Phones.TYPE_MOBILE);		//a number of type Mobile
	    values.put(Phones.NUMBER, NUM1);							//the value of that number
	    getContentResolver().insert(infoURI, values);					//insert this info into the contacts list

	    avalues = new ContentValues();
	    avalues.put("name", "Gothar");
	    avalues.put("agility", 3);
	    avalues.put("intelligence", 3);
	    avalues.put("reflexes", 2);
	    avalues.put("awareness", 2);
	    avalues.put("stamina", 3);
	    avalues.put("willpower", 3);
	    avalues.put("strength", 4);
	    avalues.put("perception", 1);
	    avalues.put("vvoid", 3);
	    avalues.put("playerid", pathLeaf);	//where pathLeaf is the contact ID
	
	    getContentResolver().insert( Uri.parse(
	    	"content://fsu.android.ProviderExample.CharacterProvider/characters"), avalues );
	}
	
    //-----//
  
	//look for second contact
	cur = findContactsByNumber(NUM2);
	
	if (!cur.moveToFirst()) {
		values = new ContentValues();
	    values.put(Phones.NAME, "Cindy Vitales");
	    personURI = Contacts.People.createPersonInMyContactsGroup(getContentResolver(), values);
	    infoURI = Uri.withAppendedPath(personURI, Contacts.People.Phones.CONTENT_DIRECTORY);
	    pathLeaf = personURI.getLastPathSegment();
	    values.clear();
	    values.put(Phones.TYPE, Phones.TYPE_MOBILE);
	    values.put(Phones.NUMBER, NUM2);
	    getContentResolver().insert(infoURI, values);

	    avalues = new ContentValues();
	    avalues.put("name", "Conan");
	    avalues.put("agility", 3);
	    avalues.put("intelligence", 3);
	    avalues.put("reflexes", 4);
	    avalues.put("awareness", 1);
	    avalues.put("stamina", 2);
	    avalues.put("willpower", 3);
	    avalues.put("strength", 3);
	    avalues.put("perception", 4);
	    avalues.put("vvoid", 2);  
	    avalues.put("playerid", pathLeaf);  
	
	    getContentResolver().insert( Uri.parse(
	 		"content://fsu.android.ProviderExample.CharacterProvider/characters"), avalues );
	}
} //end of generateData

private void deleteData() {
	//could remove test data if desired
}

} //end of Activity
