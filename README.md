< Music store >
=====================

The course project on the discipline of "Databases"


Student: Vasilii Komarov

Number of student's record-book: 0911250

Group: IV-44-ZF


Created on 5/14/2018

Email: komarov.vasiliy884@gmail.com


=====================

PostgreSQL is used as a DBMS.

Before running the program, you must run the SQL script that creates the schema and the user.

You need to execute the following command in the command line from the directory where the file 00_create_schema_and_user.sql is located:


psql -U postgres -f 00_create_schema_and_user.sql > 00_create_schema_and_user.out


Run the program with one or more parameters:


"createTables" - creates tables in the database schema

"insertData" - fills the tables with initial data

"dropTables" - drops tables
