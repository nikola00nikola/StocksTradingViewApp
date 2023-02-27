
# StocksTradingViewApp

Application implements displaying stocks value through history (Candle format, real-time data) and simulation of trading with stocks (real-time prices).

### Implementation
Database "baza.db" is used for storing data about registered users (Table "Users") and purchased stocks.

Functions for work with database are implemented in c++ (using sqlite3), files: *"dll_folder/nativeMetode_BazaPodataka.h"* and *"dll_folder/nativeMetode_BazaPodataka.cpp"*. These two files are built as *"dll_folder/RadSaBazom.dll"* and used in Java project as Native library.

Function for fetching data about stock from WEB (Yahoo) is implemented in c++ (CURL library), files:*"nativeMetode_Parse.h"* and *"nativeMetode_Parse.cpp"*.
These two files are built as *"dll_folder/Parser.dll"* and used in Java project as Native library.

User api is built and implemented in Java.

For running the app need to add path of *"dll_folder"* to Java project native library location, create database called "baza.db" in project location(parent of src folder) and run scrypt *"createTableUsers.sql"* to init the database.

## Appearance
#### Browsing stock candles (real-world prices)
![Logo](https://github.com/nikola00nikola/StocksTradingViewApp/blob/main/izgled/IzledProzoraAkcija.png?raw=true)

#### Trading
![Logo](https://github.com/nikola00nikola/StocksTradingViewApp/blob/main/izgled/IzledProzoraKorisnika.png?raw=true)

