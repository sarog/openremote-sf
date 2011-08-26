package org.openremote.android.console;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
public class DataHelper {
   private static final String DATABASE_NAME = "example.db";
   private static final int DATABASE_VERSION = 1;
   private static final String TABLE_NAME = "table1";
   private Context context;
   private SQLiteDatabase db;
   private SQLiteStatement insertStmt;
   private SQLiteStatement deleteStmt;
   private SQLiteStatement selectStmt;
   private static final String INSERT = "insert into "
      + TABLE_NAME + "(name,info, auto, up) values (?,?,?,?)";
   private static final String DELETE = "delete from "
		      + TABLE_NAME + " where name = ?";
   private static final String SELECT = "select * from "
		      + TABLE_NAME ;
   private static final String FIND = "select * from "
		      + TABLE_NAME+" where name = ?" ;
   
   public DataHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
      this.deleteStmt = this.db.compileStatement(DELETE);
      this.selectStmt = this.db.compileStatement(SELECT);
   }
   public long insert(String name, String info) {
	   //if(db.h)
	   
      this.insertStmt.bindString(1, name);
      this.insertStmt.bindString(2, info);
      return this.insertStmt.executeInsert();
   }
   
   public long insert(String name, String info, int auto, int up, int selected) {
	   //if(db.h)
	   
      this.insertStmt.bindString(1, name);
      this.insertStmt.bindString(2, info);
      return this.insertStmt.executeInsert();
   }
   
   public long delete(String name) {
	      this.deleteStmt.bindString(1, name);
	      return this.deleteStmt.executeUpdateDelete();
	   }
   
   public void deleteAll() {
      this.db.delete(TABLE_NAME, null, null);
   }
   
   public ArrayList<ControllerObject> getControllerData(){
	   ArrayList<ControllerObject> list = new ArrayList<ControllerObject>();
			   
	     Cursor cursor = this.db.query(TABLE_NAME, null,
	    	        null, null, null, null, "name desc");
	    	      if (cursor.moveToFirst()) {
	    	         do {
	    	            list.add(new ControllerObject(cursor.getString(0), cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4)));//0 is id, 1 is name
	    	         } while (cursor.moveToNext());
	    	      }
	    	      if (cursor != null && !cursor.isClosed()) {
	    	         cursor.close();
	    	      }
	   return list;
	   
   }
   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" },
        null, null, null, null, "name desc");
      if (cursor.moveToFirst()) {
         do {
            list.add(cursor.getString(0));
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
   private static class OpenHelper extends SQLiteOpenHelper {
      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }
      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + TABLE_NAME + "(name TEXT PRIMARY KEY, info TEXT, auto INTEGER, up INTEGER, selected INTEGER)");
      }
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
      }
   }
}