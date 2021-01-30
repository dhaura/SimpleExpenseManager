package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "180449H.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS account(" +
                        "account_no TEXT PRIMARY KEY," +
                        "bank_name TEXT NOT NULL," +
                        "account_holder_name TEXT NOT NULL," +
                        "balance REAL NOT NULL" +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS transaction_account (" +
                        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date TEXT NOT NULL," +
                        "account_no TEXT NOT NULL," +
                        "expense_type TEXT NOT NULL CHECK (expense_type == \"EXPENSE\" OR expense_type == \"INCOME\")," +
                        "amount REAL NOT NULL," +
                        "FOREIGN KEY(account_no) REFERENCES account(account_no) ON DELETE CASCADE" +
                        ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS account");
        db.execSQL("DROP TABLE IF EXISTS transaction_account");
        onCreate(db);
    }

    public boolean addAccount(String accountNo, String bankName, String accountHolderName, double balance){
        Cursor res = getAccount(accountNo);
        if (res.moveToFirst()){
            System.out.println("Account already exists.");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", accountNo);
        contentValues.put("bank_name", bankName);
        contentValues.put("account_holder_name", accountHolderName);
        contentValues.put("balance", balance);
        db.insert("account", null, contentValues);
        return true;
    }

    public Cursor getAccount(String accountNo){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM account WHERE account_no='"+accountNo+"';", null );
        return res;
    }

    public Cursor getAccountsList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM account;", null );
        return res;
    }

    public Cursor getAccountNumbersList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT account_no FROM account", null );
        return res;
    }

    public boolean removeAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account", "account_no = ?", new String[] { accountNo });
        return true;
    }

    public boolean updateBalance(String accountNo, double newBalance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", newBalance);
        db.update("account", contentValues, "account_no = ? ", new String[] { accountNo} );
        return true;
    }

    public Cursor getAllTransactionLogs(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM transaction_account;", null );
        return res;
    }

    public boolean logTransaction(String date, String accountNo, String expenseType, double amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("account_no", accountNo);
        contentValues.put("expense_type", expenseType);
        contentValues.put("amount", amount);
        db.insert("transaction_account", null, contentValues);
        return true;
    }
}