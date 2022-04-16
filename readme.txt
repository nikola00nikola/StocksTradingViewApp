fajl "dll_folder/Parser.dll" je dobijen build-ovanjem sledecih fajlova: 
	- "nativeMetode_Parse.h"
	- "nativeMetode_Parse.cpp"
(c++) klasa "nativeMetode_Parse" koristi biblioteku "curl" za dohvatanje sadrzaja sa weba.


fajl "dll_folder/RadSaBazom.dll" je dobijen build-ovanjem sledecih fajlova:
	- "nativeMetode_BazaPodataka.h"
	- "nativeMetode_BazaPodataka.cpp"
(c++) klasa "nativeMetode_BazaPodataka" koristi sqlite3("sqlite3.h", "sqlite3ext.h", "sqlite3.c", "shell.c") za rad sa bazama podataka


Za pokretanje aplikacije neophodno je dodati path foldera "dll_folder" u Java Build Path-> Libraries-> Native library location.
Pored toga mora se kreirati baza podataka "baza.db" u folderu projekta, i u njoj kreirati jedna tabela, za cije je kreiranje dat sql skript u fajlu "createTableUsers.sql".
Ostale tabele(tabele za pracenje kupljenih/prodatih akcija) kreira sam program i vodi racuna o njima.