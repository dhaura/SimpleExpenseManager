CREATE TABLE IF NOT EXISTS account(
	account_no TEXT PRIMARY KEY,
   	bank_name TEXT NOT NULL,
	account_holder_name TEXT NOT NULL,
	balance REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS transaction_account (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
	date TEXT NOT NULL,
   	account_no TEXT NOT NULL,
	expense_type TEXT NOT NULL CHECK (expense_type == "EXPENSE" OR expense_type == "INCOME"),
	amount REAL NOT NULL,
	FOREIGN KEY(account_no) REFERENCES account(account_no) ON DELETE CASCADE
);
