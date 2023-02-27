package nativeMetode;

import java.util.ArrayList;

public class BazaPodataka {

	static {
		System.loadLibrary("RadSaBazom");	//native metode definisane u c++ RadSaBazom.dll
	}
	public static final String pathBaza=".\\baza.db";
	
	private static BazaPodataka instance=null;
	private BazaPodataka() {
		
	}
	
	public static BazaPodataka getInstance() {
		if(instance==null)
			instance=new BazaPodataka();
		return instance;
	}
	
	public synchronized static native boolean addUser(String username,String password,double balance,String baza);
	public synchronized static native boolean isThisTruePassword(String username,String password,String baza);
	public synchronized static native boolean isThisUserNameTaken(String username,String baza);
	
	public synchronized static native double getBalance(String username,String baza);
	public synchronized static native String setBalance(String username,double balans,String baza);
	
	public synchronized static native void dodajKupljene(String username,String company,int brAkc,double nabavna,String baza);
	public synchronized static native ArrayList<ArrayList<String>> dohvKupljene(String username,String baza);
	
	public synchronized static native void prodajAkcije(String user,int brProdatih,int idKupovine,String baza);
	public synchronized static native void obrisiBezAkcija(String user,String baza);
	public synchronized static native String dohVKompOdId(String user,int id,String baza);
	public synchronized static native int dohvBrAkcOdId(String user,int id,String baza);
	
	
	
	
	public static void main(String[] args) {
		
	}
}
