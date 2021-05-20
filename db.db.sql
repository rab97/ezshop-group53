BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "user" (
	"username"	TEXT NOT NULL UNIQUE,
	"password"	TEXT NOT NULL,
	"role"	TEXT NOT NULL,
	"id"	INTEGER NOT NULL UNIQUE,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "balance_operation" (
	"id"	INTEGER NOT NULL UNIQUE,
	"date"	DATE NOT NULL,
	"money"	INTEGER NOT NULL,
	"type"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "customer" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT NOT NULL,
	"card"	TEXT(10),
	"points"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "product_type" (
	"id"	INTEGER NOT NULL UNIQUE,
	"quantity"	INTEGER,
	"location"	TEXT,
	"note"	TEXT,
	"description"	TEXT NOT NULL,
	"bar_code"	TEXT(12) NOT NULL UNIQUE,
	"price_per_unit"	DOUBLE NOT NULL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "sale_transaction" (
	"id"	INTEGER NOT NULL UNIQUE,
	"discountRate"	DOUBLE,
	"price"	DOUBLE,
	"payed"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "return_transaction" (
	"id"	INTEGER NOT NULL UNIQUE,
	"amount"	NUMERIC,
	"payed"	INTEGER,
	"discountRate"	REAL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "ticket_entry" (
	"transactionId"	INTEGER NOT NULL,
	"productId"	INTEGER NOT NULL,
	"bar_code"	TEXT NOT NULL,
	"price_per_unit"	DOUBLE NOT NULL,
	"amount"	INTEGER,
	"discount_rate"	DOUBLE,
	"product_description"	TEXT,
	FOREIGN KEY("transactionId") REFERENCES "sale_transaction"("id"),
	FOREIGN KEY("productId") REFERENCES "product_type"("id")
);
CREATE TABLE IF NOT EXISTS "return_ticket_entry" (
	"returnId"	INTEGER NOT NULL,
	"productId"	INTEGER NOT NULL,
	"bar_code"	TEXT NOT NULL,
	"price_per_unit"	REAL NOT NULL,
	"amount"	INTEGER,
	"discount_rate"	REAL,
	"product_description"	INTEGER,
	PRIMARY KEY("returnId","productId"),
	FOREIGN KEY("returnId") REFERENCES "return_transaction"("id") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "order" (
	"id"	INTEGER UNIQUE,
	"balance_id"	INT,
	"product_code"	TEXT NOT NULL,
	"price_per_unit"	REAL NOT NULL,
	"quantity"	INTEGER NOT NULL,
	"status"	TEXT NOT NULL,
	PRIMARY KEY("id"),
	FOREIGN KEY("product_code") REFERENCES "product_type"("bar_code"),
	FOREIGN KEY("balance_id") REFERENCES "balance_operation"("id")
);
COMMIT;
