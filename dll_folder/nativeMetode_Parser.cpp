#include "nativeMetode_Parser.h"
#include <string> 
#define CURL_STATICLIB 
#include <curl/curl.h> 
#include<regex>

using namespace std;

static size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
	((std::string*)userp)->append((char*)contents, size * nmemb);
	return size * nmemb;
}

JNIEXPORT jobject JNICALL Java_nativeMetode_Parser_parse
(JNIEnv* env, jclass, jstring kompanija, jlong start, jlong end) {
	vector<double> t;
	vector<double> o;
	vector<double> c;
	vector<double> h;
	vector<double> l;
	string Start = std::to_string(start);
	string End = std::to_string(end);
	string company = env->GetStringUTFChars(kompanija, nullptr);
	string address = "https://query1.finance.yahoo.com/v8/finance/chart/" + company + "?period1=" + Start + "&period2=" + End + "&interval=1d";
	//cout << "ADRESS: " << address << endl;

	CURL* curl;
	CURLcode res;
	std::string json;
	curl = curl_easy_init();
	if (curl) {
		curl_easy_setopt(curl, CURLOPT_URL, address.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &json);
		res = curl_easy_perform(curl); curl_easy_cleanup(curl);
	}
	
	//Stock data object fetched from web in variable json
	//now just grab the data of interest for every candle(timestamp, high, low, open, close)

	char* text = (char*)json.c_str();
	char* tek = text;
	int br = 0;

	while ((*tek) != '\0') {
		if (br > 4)
			break;
		vector<double>* pokVektor = nullptr;
		if (*tek == 't' || *tek == 'o' || *tek == 'c' || *tek == 'h' || *tek == 'l') {
			if (tek[0] == 'o' && tek[1] == 'p' && tek[2] == 'e' && tek[3] == 'n')
				pokVektor = &o;
			else if (tek[0] == 'c' && tek[1] == 'l' && tek[2] == 'o' && tek[3] == 's' && tek[4] == 'e')
				pokVektor = &c;
			else if (tek[0] == 'h' && tek[1] == 'i' && tek[2] == 'g' && tek[3] == 'h')
				pokVektor = &h;
			if (tek[0] == 'l' && tek[1] == 'o' && tek[2] == 'w')
				pokVektor = &l;
			else if (tek[0] == 't' && tek[1] == 'i' && tek[2] == 'm' && tek[3] == 'e' && tek[4] == 's' && tek[5] == 't' && tek[6] == 'a' &&
				tek[7] == 'm' && tek[8] == 'p')
				pokVektor = &t;
			if (pokVektor != nullptr) {
				br++;
				while (*tek != '[')
					tek++;
				tek++;
				const char* pocetak = tek;
				while (*tek != ']')
					tek++;
				tek++;
				tek++;
				*(tek - 1) = '\0';


				string full_line(pocetak);
				regex rxl(",([^,]+)");
				sregex_iterator begin(full_line.begin(), full_line.end(), rxl);
				sregex_iterator end;


				//,3.412421,53.24323,423432.43534
				while (begin != end) {
					smatch sm = *begin;
					double value = atof(sm.str(1).c_str());
					pokVektor->push_back(value);
					begin++;
				}
			}
		}
		tek++;
	}
	//vectors t, o, c, h, l contains all data of interest, now just transform them to JAVA ArrayList and return



	jclass java_util_ArrayList = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
	jclass java_lang_Double = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/lang/Double")));
	jmethodID java_util_ArrayList_ = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
	jmethodID java_lang_Double_ = env->GetMethodID(java_lang_Double, "<init>", "(D)V");
	jmethodID java_util_ArrayList_add = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");


	jobject result = env->NewObject(java_util_ArrayList, java_util_ArrayList_, 5);
	jobject lista1 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, t.size());
	jobject lista2 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, o.size());
	jobject lista3 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, c.size());
	jobject lista4 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, h.size());
	jobject lista5 = env->NewObject(java_util_ArrayList, java_util_ArrayList_, l.size());


	for (double d : t) {
		jdouble izlazz = d;
		jobject izlaz = env->NewObject(java_lang_Double, java_lang_Double_, izlazz);
		env->CallBooleanMethod(lista1, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (double d : o) {
		jdouble izlazz = d;
		jobject izlaz = env->NewObject(java_lang_Double, java_lang_Double_, izlazz);
		env->CallBooleanMethod(lista2, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (double d : c) {
		jdouble izlazz = d;
		jobject izlaz = env->NewObject(java_lang_Double, java_lang_Double_, izlazz);
		env->CallBooleanMethod(lista3, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (double d : h) {
		jdouble izlazz = d;
		jobject izlaz = env->NewObject(java_lang_Double, java_lang_Double_, izlazz);
		env->CallBooleanMethod(lista4, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	for (double d : l) {
		jdouble izlazz = d;
		jobject izlaz = env->NewObject(java_lang_Double, java_lang_Double_, izlazz);
		env->CallBooleanMethod(lista5, java_util_ArrayList_add, izlaz);
		env->DeleteLocalRef(izlaz);
	}
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista1);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista2);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista3);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista4);
	env->CallBooleanMethod(result, java_util_ArrayList_add, lista5);



	return result;
}
