
package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import android.database.Cursor;

public class DatabaseAccountDAO implements AccountDAO {
    private DBHelper dbHelper;

    public DatabaseAccountDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<String>();
        Cursor cursor = dbHelper.getAccountNumbersList();

        if (!cursor.moveToFirst()) {
            return accountNumbers;
        }

        String account_no;

        do {

            account_no = cursor.getString(0);
            accountNumbers.add(account_no);

        } while (cursor.moveToNext());

        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();
        Cursor cursor = dbHelper.getAccountsList();

        if (!cursor.moveToFirst()) {
            return accounts;
        }

        Account account;

        do {

            account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            accounts.add(account);

        } while (cursor.moveToNext());

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = dbHelper.getAccount(accountNo);
        if (cursor.moveToFirst()) {
            Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        dbHelper.addAccount(account.getAccountNo(), account.getBankName(), account.getAccountHolderName(),account.getBalance());
        return;
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor resultSet =dbHelper.getAccount(accountNo);
        if (resultSet.moveToFirst()) {
            dbHelper.removeAccount(accountNo);
            return;

        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Cursor resultSet = dbHelper.getAccount(accountNo);
        if (!resultSet.moveToFirst()) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        double balance = resultSet.getDouble(3);
        double newBalance = balance;

        switch (expenseType) {
            case EXPENSE:
                newBalance = balance - amount;
                break;
            case INCOME:
                newBalance = balance + amount;
                break;
        }

        if (newBalance < 0){
            System.out.println("Not Enough Balance.");
            return;
        }

        dbHelper.updateBalance(accountNo, newBalance);
    }
}
