#include"nativeMetode_BazaPodataka.h"
#include "sqlite3.h"
#include <string>
#include<vector>

JNIEXPORT jboolean JNICALL Java_nativeMetode_BazaPodataka_addUser
(JNIEnv* env, jclass, jstring imee, jstring sifraa, jdouble balans, jstring baza) {
	const char* ime = env->GetStringUTFChars(imee, nullptr);
	const char* sifra = env->GetStringUTFChars(sifraa, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	double stanje = balans;

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return false;
	}

	sqlite3_stmt* stmt;
	const char* sql = "insert into Users (Username, Password, Balance) values (?, ?, ?)";
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return false;
	}

	sqlite3_bind_text(stmt, 1, ime, -1, nullptr);
	sqlite3_bind_text(stmt, 2, sifra, -1, nullptr);
	sqlite3_bind_double(stmt, 3, stanje);


	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE) {
		return false;
	}

	//creating table for record of purchased stocks
	sqlite3_stmt* stmt1;
	std::string s = "CREATE TABLE \"" + std::string(ime) + "\" ( \"id\"	INTEGER NOT NULL, \"Company\"	TEXT, \"brojAkcija\"	INTEGER NOT NULL, \"nabavnaCena\"	REAL NOT NULL, PRIMARY KEY(\"id\" AUTOINCREMENT))";
	sql = s.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt1, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt1);
		return false;
	}

	sqlite3_bind_text(stmt1, 1, ime, -1, nullptr);


	rs = sqlite3_step(stmt1);
	if (rs != SQLITE_DONE) {
		return false;
	}

	sqlite3_finalize(stmt);

	sqlite3_finalize(stmt1);

	sqlite3_close(db);
	return true;
}



JNIEXPORT jboolean JNICALL Java_nativeMetode_BazaPodataka_isThisTruePassword
(JNIEnv* env, jclass, jstring ime, jstring sifra, jstring baza) {
	const char* username = env->GetStringUTFChars(ime, nullptr);
	const char* password = env->GetStringUTFChars(sifra, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return false;
	}


	sqlite3_stmt* stmt;
	const char* sql = "select Password from Users where Username=?";
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return 0;
	}

	sqlite3_bind_text(stmt, 1, username, -1, nullptr);

	rs = sqlite3_step(stmt);


	std::string s((const char*)(sqlite3_column_text)(stmt, 0));
	std::string p(password);
	sqlite3_finalize(stmt);
	sqlite3_close(db);
	return s == p;
}



JNIEXPORT jboolean JNICALL Java_nativeMetode_BazaPodataka_isThisUserNameTaken
(JNIEnv* env, jclass, jstring ime, jstring baza) {
	const char* username = env->GetStringUTFChars(ime, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return true;
	}

	sqlite3_stmt* stmt;
	const char* sql = "select Username from Users where Username=?";
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return true;
	}

	sqlite3_bind_text(stmt, 1, username, -1, nullptr);

	if (rs = sqlite3_step(stmt) != SQLITE_ROW)
	{
		sqlite3_finalize(stmt);
		sqlite3_close(db);
		return false;
	}
	else
	{
		sqlite3_finalize(stmt);
		sqlite3_close(db);
		return true;
	}
}

//getBalance for user(ime)
JNIEXPORT jdouble JNICALL Java_nativeMetode_BazaPodataka_getBalance
(JNIEnv* env, jclass, jstring ime, jstring baza) {
	const char* username = env->GetStringUTFChars(ime, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return -1;
	}

	sqlite3_stmt* stmt;
	const char* sql = "select Balance from Users where Username=?";
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return -1;
	}

	sqlite3_bind_text(stmt, 1, username, -1, nullptr);

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE && rs != SQLITE_ROW) {
		return -1;
	}

	double b = sqlite3_column_double(stmt, 0);
	sqlite3_finalize(stmt);
	sqlite3_close(db);
	return (jdouble)b;
}


//set balance for user(ime)
JNIEXPORT jstring JNICALL Java_nativeMetode_BazaPodataka_setBalance
(JNIEnv* env, jclass, jstring ime, jdouble iznos, jstring baza) {
	double balance = iznos;
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	const char* username = env->GetStringUTFChars(ime, nullptr);

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return NULL;
	}

	sqlite3_stmt* stmt;
	const char* sql = "UPDATE Users SET Balance=? where Username=?";
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return NULL;
	}

	sqlite3_bind_double(stmt, 1, balance);
	sqlite3_bind_text(stmt, 2, username, -1, nullptr);

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE) {
		return env->NewStringUTF(sqlite3_errmsg(db));
	}

	sqlite3_finalize(stmt);
	sqlite3_close(db);
	return NULL;
}


//add purchased stock(kompanija) by user(ime) for price(price)
JNIEXPORT void JNICALL Java_nativeMetode_BazaPodataka_dodajKupljene

(JNIEnv* env, jclass, jstring ime, jstring kompanija, jint brAk, jdouble price, jstring baza) {
	const char* username = env->GetStringUTFChars(ime, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	const char* company = env->GetStringUTFChars(kompanija, nullptr);
	int brAkcija = brAk;
	double cena = price;

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return;
	}


	sqlite3_stmt* stmt;
	std::string s = "insert into \"" + std::string(username) + "\" (Company, brojAkcija, nabavnaCena) values (?, ?, ?)";
	const char* sql = s.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return;
	}

	sqlite3_bind_text(stmt, 1, company, -1, nullptr);
	sqlite3_bind_int(stmt, 2, brAkcija);
	sqlite3_bind_double(stmt, 3, cena);


	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE) {
		return;
	}

	sqlite3_finalize(stmt);


	sqlite3_close(db);
}


//get purchased stocks for user(ime)
JNIEXPORT jobject JNICALL Java_nativeMetode_BazaPodataka_dohvKupljene
(JNIEnv* env, jclass, jstring ime, jstring baza) {
	const char* username = env->GetStringUTFChars(ime, nullptr);
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return NULL;
	}

	std::vector<std::string> p, d, t, c;

	sqlite3_stmt* stmt;
	std::string s = "select id, Company, brojAkcija, nabavnaCena from \"" + std::string(username) + "\"";
	const char* sql = s.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return NULL;
	}
	sqlite3_reset(stmt);
	while (rs = sqlite3_step(stmt) == SQLITE_ROW) {
		p.push_back(reinterpret_cast<const char*>(sqlite3_column_text(stmt, 0)));
		d.push_back(reinterpret_cast<const char*>(sqlite3_column_text(stmt, 1)));
		t.push_back(reinterpret_cast<const char*>(sqlite3_column_text(stmt, 2)));
		c.push_back(reinterpret_cast<const char*>(sqlite3_column_text(stmt, 3)));
	}
	sqlite3_finalize(stmt);
	//now just transform c++ vectors to Java ArrayList and return


	jclass java_util_ArrayList = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
	jclass java_lang_String = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/lang/String")));
	jmethodID java_util_ArrayList_ = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
	jmethodID java_lang_String_ = env->GetMethodID(java_lang_String, "<init>", "(Ljava/lang/String;)V");
	jmethodID java_util_ArrayList_add = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");


	jobject result = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 5);
	jobject lista1 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 1);
	jobject lista2 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 1);
	jobject lista3 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 1);
	jobject lista4 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 1);


	for (std::string s : p) {
		jstring izlazz = env->NewStringUTF(s.c_str());
		jobject izlaz = env->NewObject(java_lang_String, java_lang_String_, izlazz);
		env->CallBooleanMethod(lista1, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (std::string s : d) {
		jstring izlazz = env->NewStringUTF(s.c_str());
		jobject izlaz = env->NewObject(java_lang_String, java_lang_String_, izlazz);
		env->CallBooleanMethod(lista2, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (std::string s : t) {
		jstring izlazz = env->NewStringUTF(s.c_str());
		jobject izlaz = env->NewObject(java_lang_String, java_lang_String_, izlazz);
		env->CallBooleanMethod(lista3, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (std::string s : c) {
		jstring izlazz = env->NewStringUTF(s.c_str());
		jobject izlaz = env->NewObject(java_lang_String, java_lang_String_, izlazz);
		env->CallBooleanMethod(lista4, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}


	sqlite3_close(db);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista1);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista2);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista3);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista4);
	return result;
}


//sell pruchased stocks(idnt) for user(ime)
JNIEXPORT void JNICALL Java_nativeMetode_BazaPodataka_prodajAkcije
(JNIEnv* env, jclass, jstring ime, jint br, jint idnt, jstring baza) {
	std::string user = env->GetStringUTFChars(ime, nullptr);
	int brAkcijaZaProdavanje = br;
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	int id = idnt;

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return;
	}


	sqlite3_stmt* stmt;
	std::string ss = "UPDATE \"" + user + "\" SET brojAkcija=brojAkcija-? where id=?";
	const char* sql = ss.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return;
	}

	sqlite3_bind_int(stmt, 1, brAkcijaZaProdavanje);
	sqlite3_bind_int(stmt, 2, id);

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE) {
		sqlite3_finalize(stmt);
		return;
	}

	sqlite3_finalize(stmt);


	sqlite3_close(db);
}


//Delete purchased stocks from table where quantity == 0
JNIEXPORT void JNICALL Java_nativeMetode_BazaPodataka_obrisiBezAkcija
(JNIEnv* env, jclass, jstring ime, jstring baza) {
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	std::string user(env->GetStringUTFChars(ime, nullptr));

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return;
	}


	sqlite3_stmt* stmt;
	std::string ss = "DELETE FROM \"" + user + "\" where brojAkcija=0";
	const char* sql = ss.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return;
	}

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_DONE) {
		sqlite3_finalize(stmt);
		return;
	}

	sqlite3_finalize(stmt);

	sqlite3_close(db);
}

//Get stock company name by user(ime) and id of purchase(ident)
JNIEXPORT jstring JNICALL Java_nativeMetode_BazaPodataka_dohVKompOdId
(JNIEnv* env, jclass, jstring ime, jint ident, jstring baza) {
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	std::string user(env->GetStringUTFChars(ime, nullptr));
	int id = ident;

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return NULL;
	}

	std::string s = std::string();
	sqlite3_stmt* stmt;
	std::string ss = "select Company from \"" + user + "\" where id=?";
	const char* sql = ss.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return NULL;
	}

	sqlite3_bind_int(stmt, 1, id);

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_ROW) {
		return NULL;
	}

	s = reinterpret_cast<const char*>(sqlite3_column_text(stmt, 0));

	sqlite3_finalize(stmt);
	sqlite3_close(db);
	jstring izlaz = env->NewStringUTF(s.c_str());

	return izlaz;
}

//Get quantity of purchased stocks for user(ime) nad id(ident)
JNIEXPORT jint JNICALL Java_nativeMetode_BazaPodataka_dohvBrAkcOdId
(JNIEnv* env, jclass, jstring ime, jint ident, jstring baza) {
	const char* imeBaze = env->GetStringUTFChars(baza, nullptr);
	std::string user(env->GetStringUTFChars(ime, nullptr));
	int id = ident;

	sqlite3* db;
	int rs = sqlite3_open(imeBaze, &db);
	if (rs != SQLITE_OK) {
		return -1;
	}

	sqlite3_stmt* stmt;
	std::string ss = "select brojAkcija from \"" + user + "\" where id=?";
	const char* sql = ss.c_str();
	rs = sqlite3_prepare(db, sql, 256, &stmt, nullptr);
	if (rs != SQLITE_OK) {
		sqlite3_finalize(stmt);
		return -1;
	}

	sqlite3_bind_int(stmt, 1, id);

	rs = sqlite3_step(stmt);
	if (rs != SQLITE_ROW) {
		return -1;
	}

	jint s = sqlite3_column_int(stmt, 0);

	sqlite3_finalize(stmt);
	sqlite3_close(db);

	return s;
}
