package fsu.android.ProviderExample;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public final class CharacterProvider extends ContentProvider {
	public static final String PROVIDER_NAME = 
		"fsu.android.ProviderExample.CharacterProvider";
	public static final Uri CONTENT_URI = 
		Uri.parse("content://"+ PROVIDER_NAME + "/characters");
	
	//column names
	public static final String _ID = "_id";
	public static final String Name = "name";
	public static final String Agility = "agility";
	public static final String Intelligence = "intelligence";
	public static final String Reflexes = "reflexes";
	public static final String Awareness = "awareness";
	public static final String Stamina = "stamina";
	public static final String Willpower = "willpower";
	public static final String Strength = "strength";
	public static final String Perception = "perception";
	public static final String Void = "vvoid";
	public static final String Playerid = "playerid";
	
	//column numbers
	public static final int cName = 1;
	public static final int cAgility = 2;   
	public static final int cIntelligence = 3;
	public static final int cReflexes = 4;
	public static final int cAwareness = 5;
	public static final int cStamina = 6;
	public static final int cWillpower = 7;
	public static final int cStrength = 8;
	public static final int cPerception = 9;
	public static final int cVoid = 10;
	public static final int cPlayerid = 11;
	
	//URI types
	private static final int CharacterName = 1;
	private static final int CharacterId = 2;
	   
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "characters/#", CharacterId);      
		uriMatcher.addURI(PROVIDER_NAME, "characters", CharacterName);
	}   
	
	private SQLiteDatabase characterDB;
	
	// By placing all of the DB information in constants, including the SQL query
	// to create it, changing the internal structure won't break your code.
	// Fascinating!
	private static final String DATABASE_NAME = "CharactersDb";
	private static final String DATABASE_TABLE = "CharactersTable";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_CREATE =
		"create table " + DATABASE_TABLE + 
		" (_id integer primary key autoincrement, " +
		"name string, " +
		"agility integer, " +
		"intelligence integer, " +
		"reflexes integer, " +
		"awareness integer, " +
		"stamina integer, " +
		"willpower integer, " +
		"strength integer, " +
		"perception integer, "  +
		"vvoid integer, " +
		"playerid integer);";
	
	/**
	 * The DB helpers provide a quick and safe way to open the SQL database
	 * from within Android, create the new database, and upgrade it.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("CharacterProvider DB", "Upgrading database from version " + 
				oldVersion + " to " + newVersion + ", destroying previous data");
//db.execSQL("DROP TABLE IF EXISTS titles");	//doesn't make sense for this code.
	        onCreate(db);
		}
	}


//--Provider Methods--//	//reorder these so that they are in a more logical fashion (create -> insert, remove -> upgrade -> delete)
public CharacterProvider() {
	// TODO Auto-generated constructor stub
}

/**
 * Called when the database is first created. Works best with a helper object
 * to do the heavy lifting, as seen here.
 */
@Override
public boolean onCreate() {
	Context context = getContext();
	DatabaseHelper dbHelper = new DatabaseHelper(context);
    characterDB = dbHelper.getWritableDatabase();
    return (characterDB == null) ? false : true;
}

/**
 * Basic insert method. Also notifies users of the provider about the change.
 * 
 * @return row	the Uri pointing to the inserted row.
 */
@Override
public Uri insert(Uri uri, ContentValues values) {
	long rowID = characterDB.insert(DATABASE_TABLE, "", values);
           
	if (rowID > 0) {
		Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
		getContext().getContentResolver().notifyChange(_uri, null);    
		return _uri;                
	} else
		throw new SQLException("Failed to insert row into " + uri +
							   " "+ values.toString());
}

/**
 * Basic delete method. Performs a query, and deletes the results from the
 * database. Notifies users of the provider about the change.
 * 
 * @return count	the number of rows removed
 */
@Override
public int delete(Uri arg0, String arg1, String[] arg2) {
	int count = 0;
	
	switch (uriMatcher.match(arg0)) {
	case CharacterName:
		count = characterDB.delete(
					DATABASE_TABLE,
					arg1, 
					arg2);
    	break;
	case CharacterId: 
		String id = arg0.getPathSegments().get(1);
        count = characterDB.delete(
        			DATABASE_TABLE,                        
        			_ID + " = " + id + 
        			(!TextUtils.isEmpty(arg1) ? " AND (" + 
        			arg1 + ')' : ""), 
        			arg2
        		);
        break;
	default:
		throw new IllegalArgumentException( "Unknown URI " + arg0);
	}
	
	return count;	//used to be return 0. why?
}

/**
 * Provides the basic and essential method by which the content resolver will
 * query the database. The options are passed in as a managed query, in pieces.
 * For specialized access, raw SQL queries can be called from custom methods.
 * 
 * @return results	cursor to the results of the query (empty if none)
 */
@Override
public Cursor query(Uri uri, String[] projection, String selection,
					String[] selectionArgs, String sortOrder) {
	
	SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	sqlBuilder.setTables(DATABASE_TABLE);
	
	if (uriMatcher.match(uri) == CharacterId)
		sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));              
	
	Cursor c = sqlBuilder.query(
		characterDB, 
	    projection, 
	    selection, 
	    selectionArgs, 
	    null, 
	    null, 
	    sortOrder);
	   
	//register to watch a content URI for changes
	c.setNotificationUri(getContext().getContentResolver(), uri);
	
	return c;
}

/**
 * The basic types are single and multiple, represented as .item and .dir Uri's.		//MORE!
 */
@Override
public String getType(Uri uri) {
	switch (uriMatcher.match(uri)) {
	case CharacterName:
		return "vnd.android.cursor.dir/vnd.CharacterProvider";
    case CharacterId:   
      	return "vnd.android.cursor.dir/vnd.CharacterProvider";
    default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);        
    }   
}

/**
 * The update method provides necessary housekeeping in the event that the
 * internal database structure is changed in a later release. At the most basic
 * level, the old database or table is deleted and onCreate (the new one) is
 * called to replace it. Of course, it is also possible to migrate the existing
 * data from the old table to the new table as required.
 */
@Override
public int update(Uri uri, ContentValues values, String selection, 
				  String[] selectionArgs) {
	int count = 0;
    
	switch (uriMatcher.match(uri)) {
	case CharacterName:
		count = characterDB.update(
					DATABASE_TABLE, 
					values,
					selection, 
					selectionArgs);
		break;
	case CharacterId: 
		count = characterDB.update(
					DATABASE_TABLE, 
					values,
					_ID + " = " + uri.getPathSegments().get(1) + 
					(!TextUtils.isEmpty(selection) ? " AND (" + 
					selection + ')' : ""), 
					selectionArgs);
		break;
	default:
		throw new IllegalArgumentException("Unknown URI " + uri);    
	}    
	
	//notify listeners about the change
	getContext().getContentResolver().notifyChange(uri, null);
	return count;
}

} //end of CharacterProvider class