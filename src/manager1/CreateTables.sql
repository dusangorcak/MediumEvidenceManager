CREATE TABLE "STORAGE" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(50) NOT NULL,
    "CAPACITY" INTEGER NOT NULL,
    "ADDRESS" VARCHAR(50) NOT NULL
 );

CREATE TABLE "MEDIUM"(
	"ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        "STORAGEID" BIGINT REFERENCES STORAGE (ID),
	"NAME" VARCHAR(50) NOT NULL,
	"AUTHOR" VARCHAR(50) NOT NULL,
	"GENRE" VARCHAR(50) NOT NULL,
	"PRICE" DECIMAL(5,2),
	"TYPE" VARCHAR(5)
);


