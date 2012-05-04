CREATE TABLE "MEDIUM"(
	"ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        "STORAGEID" BIGINT REFERENCES STORAGE (ID),
	"NAME" VARCHAR(50) NOT NULL,
	"AUTHOR" VARCHAR(50) NOT NULL,
	"GENGRE" VARCHAR(50) NOT NULL,
	"PRICE" DECIMAL(5,2),
	"TYPE" VARCHAR(5)
);

CREATE TABLE "STORAGE" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "CAPACITY" INTEGER NOT NULL,
    "ADDRESS" VARCHAR(50) NOT NULL
 );