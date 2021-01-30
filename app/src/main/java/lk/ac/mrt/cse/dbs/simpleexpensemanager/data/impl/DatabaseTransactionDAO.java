
package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import android.database.Cursor;


/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class DatabaseTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;

    public DatabaseTransactionDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Cursor resultSet = dbHelper.getAccount(accountNo);
        if (!resultSet.moveToFirst()) {
            String msg = "Account " + accountNo + " is invalid.";
            System.out.println(msg);
        }

        double balance = resultSet.getDouble(3);

        if (expenseType == ExpenseType.EXPENSE && balance - amount < 0){
            System.out.println("Not Enough Balance.");
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);
        dbHelper.logTransaction(strDate, accountNo, expenseType.name(), amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        Cursor cursor = dbHelper.getAllTransactionLogs();

        if (!cursor.moveToFirst()) {
            return transactions;
        }

        Transaction transaction;

        do {
            String strDate = cursor.getString(1);
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            transaction = new Transaction(date, cursor.getString(2), ExpenseType.valueOf(cursor.getString(3)), cursor.getDouble(4));
            transactions.add(transaction);

        } while (cursor.moveToNext());

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

}
