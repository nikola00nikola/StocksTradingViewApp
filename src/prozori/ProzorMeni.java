package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.ArrayList;

import nativeMetode.BazaPodataka;
import print.Stock;

public class ProzorMeni extends Frame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4970470415410556463L;
	String company;
	double cena,balans;
	CardLayout cardLayout=new CardLayout();
	Label labelNaziv;
	Label labelStanje;
	Button kupi,prodaj,portfolio=new Button("Korisnicki portfolio");
	Panel panPocetni=new Panel(new GridLayout(0, 1));
	Panel panKupovinaA=new Panel(new GridLayout(0, 1));
	Panel panKupovinaB=new Panel(new GridLayout(0, 1));
	Panel panProdaja=new Panel(new GridLayout(0, 1));
	Panel panCeo=new Panel(cardLayout);
	
	List list;
	Label labUputstvo1A=new Label("Unesite oznaku kompanije");
	TextField textKompanijaA=new TextField();
	Label greskaA=new Label("");
	Button daljeK=new Button("Dalje");
	Label labUputstvo1B=new Label("Unesite oznaku kompanije");
	Label labKupFirma=new Label();
	Label greskaB=new Label("");
	Label labUputstvo2=new Label("Unesite broj akcija za kupovinu");
	Label labProdaja=new Label();
	TextField textBrojAk=new TextField();
	TextField textProdajaBroj=new TextField(5);
	Button potvrdiProdaju=new Button("Prodaj");
	Button nazadProdaja=new Button("Nazad");
	Label obavestenje=new Label();
	Button potvrdiK=new Button("Kupi"),nazadKB=new Button("Nazad"),nazadKA=new Button("Nazad");
	
	private String user;
	
	
	public static double dohvTrenutnuCenu(String company) {
		Timestamp t=new Timestamp(System.currentTimeMillis());
		long kraj=t.getTime()/1000;
		long d=345600;//4 dana
		long pocetak=kraj-d;
		
		double izlaz=-1;
		
		Stock s=new Stock(company, pocetak, kraj);
		int duzina=s.brSveca();
		if(duzina > 0)
			izlaz=s.candles.get(duzina-1).close;
		else {
			pocetak-=d;
			s=new Stock(company, pocetak, kraj);
			duzina=s.brSveca();
			if(duzina > 0)
				izlaz=s.candles.get(duzina-1).close;
		}
		
		return izlaz;
	}
	
	public ProzorMeni(String korisnik) {
		super();
		user=korisnik;
		balans=BazaPodataka.getBalance(korisnik, BazaPodataka.pathBaza);
		this.setBounds(400,200,400,400);
		this.setPreferredSize(new Dimension(400, 400));
		this.setResizable(false);
		//this.setResizable(false);
		this.setBackground(Color.CYAN);
		this.setTitle("Trgovanje akcijama");
		this.populateLog();
		this.pack();
		
		portfolio.addActionListener(aaaA->{
			ProzorMeni.this.showPortfolio();
		});
		
		kupi.addActionListener((ae)->{
			cardLayout.next(panCeo);
		});
		
		potvrdiK.addActionListener((ae)->{
			if(textBrojAk.getText().length() == 0)
				return;
			try {

				int brojAkc=Integer.valueOf(textBrojAk.getText());
				if(brojAkc==0)
					return;
				company=labKupFirma.getText();
				cena=ProzorMeni.dohvTrenutnuCenu(company);
				
				greskaB.setText("Cena akcije "+company+": "+cena);
				double noviBalans=balans-cena*brojAkc;
				if(noviBalans>=0) {
					balans=noviBalans;
					labelStanje.setText("Stanje: "+balans+" $");
					System.out.println(BazaPodataka.setBalance(user, balans, BazaPodataka.pathBaza));
					BazaPodataka.dodajKupljene(user,company , brojAkc,cena, BazaPodataka.pathBaza);
					obavestenje.setText("Uspesno ste kupili "+brojAkc+" akcija ["+company+"]");
				}else {
					obavestenje.setText("Nedovoljno sredstava za kupovinu");
				}
			}catch (NumberFormatException e) {
				labUputstvo2.setText("Neispravan unos!");
			}
		});
		
		nazadProdaja.addActionListener((ae)->{
			ProzorMeni.this.setBounds(400,200,400,400);
			cardLayout.next(panCeo);
		});
		
		prodaj.addActionListener((ae)->{
			ProzorMeni.this.popuniProdPanel();
			ProzorMeni.this.setBounds(400,200,620,400);
		});
		
		potvrdiProdaju.addActionListener((ae)->{
			if(textProdajaBroj.getText().length() == 0) {
				labProdaja.setText("Unesite broj akcija za prodaju");
				return;
			}
			try {
				String izabranaLinija=list.getSelectedItem();
				int id=this.getIdFromSelection(izabranaLinija);		
				
				int brAkc=Integer.parseInt(textProdajaBroj.getText());
				int br=BazaPodataka.dohvBrAkcOdId(user, id, BazaPodataka.pathBaza);
				if(br == -1) {
					labProdaja.setText("Neispravan id");
					return;
				}
				if(br >= brAkc) {
					labProdaja.setText("");
					String cmp=BazaPodataka.dohVKompOdId(user, id, BazaPodataka.pathBaza);
					double cena=ProzorMeni.dohvTrenutnuCenu(cmp);
					balans=balans+cena*brAkc;
					labelStanje.setText("Stanje: "+balans+" $");
					BazaPodataka.setBalance(user, balans, BazaPodataka.pathBaza);
					BazaPodataka.prodajAkcije(user, brAkc, id, BazaPodataka.pathBaza);
					BazaPodataka.obrisiBezAkcija(user, BazaPodataka.pathBaza);
					labProdaja.setText("Uspesno ste prodali "+brAkc+"akcija -"+cmp+" za "+cena*brAkc+"$");
					ProzorMeni.this.popuniProdPanel();
					ProzorMeni.this.setBounds(400,200,620,400);
				}else {
					labProdaja.setText("Prevelik broj akcija");
				}
			}catch (NumberFormatException e) {
				labProdaja.setText("Neispravan unos!");
			}
			ProzorMeni.this.setBounds(400,200,620,400);
		});
		
		nazadKA.addActionListener((ae)->{
			cardLayout.previous(panCeo);
		});
		
		nazadKB.addActionListener((ae)->{
			obavestenje.setText("");
			greskaA.setText("");
			cardLayout.previous(panCeo);
		});
		
		daljeK.addActionListener((ae)->{
			company=textKompanijaA.getText();
			cena=ProzorMeni.dohvTrenutnuCenu(company);
			if(cena != -1) {
				cardLayout.next(panCeo);
				labKupFirma.setText(company);
				textBrojAk.setText("");
				greskaB.setText("Cena akcije "+company+": "+cena+"$");
			}else {
				greskaA.setText("Ne postoji akcija sa imenom "+company);
			}
		});
		
		this.setVisible(true);
	}
	
	public void populateLog() {
		labKupFirma.setFont(new Font("Monospaced", Font.BOLD, 14));
		labelNaziv=new Label("Korisnik: "+user);
		labelNaziv.setAlignment(Label.CENTER);
		labelNaziv.setFont(new Font("Monospaced", Font.BOLD, 15));
		labelStanje=new Label("Stanje: "+balans+" $");
		labelStanje.setAlignment(Label.CENTER);
		labelStanje.setFont(new Font("Monospaced", Font.BOLD, 15));
		kupi=new Button("Kupovina akcija");
		prodaj=new Button("Prodaja akcija");
		
		kupi.setPreferredSize(new Dimension(this.getSize().width/2, this.getSize().height-50));
		prodaj.setPreferredSize(new Dimension(this.getSize().width/2, this.getSize().height-50));
		Panel p=new Panel(new GridLayout(0, 1));
		p.add(labelNaziv);
		p.add(labelStanje);
		this.add(p,BorderLayout.NORTH);
		panPocetni.add(kupi);
		panPocetni.add(prodaj);
		panPocetni.add(portfolio);
		
		//druga i treca Karta
		Panel q=new Panel();
		panKupovinaA.add(labUputstvo1A);
		panKupovinaA.add(textKompanijaA);
		panKupovinaA.add(greskaA);
		Panel qq=new Panel();
		daljeK.setPreferredSize(new Dimension(this.getSize().width/2-15, 55));
		nazadKA.setPreferredSize(new Dimension(this.getSize().width/2-15, 55));
		qq.add(nazadKA);
		qq.add(daljeK);
		panKupovinaA.add(qq);
		
		panKupovinaB.add(q);
		panKupovinaB.add(labUputstvo1B);
		panKupovinaB.add(labKupFirma);
		panKupovinaB.add(greskaB);
		panKupovinaB.add(labUputstvo2);
		panKupovinaB.add(textBrojAk);
		panKupovinaB.add(obavestenje);
		potvrdiK.setPreferredSize(new Dimension(this.getSize().width/2-15, 55));
		nazadKB.setPreferredSize(new Dimension(this.getSize().width/2-15, 55));
		q.add(nazadKB);
		q.add(potvrdiK);
		panKupovinaB.add(q);
		panCeo.add(panPocetni);
		panKupovinaA.setBackground(Color.ORANGE);
		panKupovinaB.setBackground(Color.ORANGE);
		panCeo.add(panKupovinaA);
		panCeo.add(panKupovinaB);
		panCeo.add(panProdaja);
		
		/*this.add(kupi,BorderLayout.WEST);
		this.add(prodaj,BorderLayout.EAST);*/
		this.add(panCeo,BorderLayout.CENTER);
		
		Menu menu=new Menu("Opcije");
		MenuItem logOut=new MenuItem("Log out");
		menu.add(logOut);
		MenuBar bar=new MenuBar();
		bar.add(menu);
		setMenuBar(bar);
		
		logOut.addActionListener((ae)->{
			ProzorMeni.this.dispose();
			new ProzorPrijava();
		});
		
	}
	
	
	public void popuniProdPanel() {
		//this.setBounds(400,200,620,400);
		list=new List(10);
		ArrayList<ArrayList<String>> kupljene=BazaPodataka.dohvKupljene(user, BazaPodataka.pathBaza);
		if(kupljene.size() > 0) {
			int velicina=kupljene.get(0).size();
			Panel pan=new Panel();
			for(int i=0;i<velicina;i++) 
			{
				String s=kupljene.get(0).get(i)+"   "+kupljene.get(1).get(i)+
						"   "+kupljene.get(2).get(i)+"   "+kupljene.get(3).get(i);
						//"   "+ProzorMeni.dohvTrenutnuCenu(kupljene.get(1).get(i));
				list.add(s);
			}
			pan.add(list);
			list.select(0);
			panCeo.remove(panProdaja);
			panProdaja=new Panel();
			panProdaja.add(pan);
			panProdaja.setBackground(Color.ORANGE);
			Panel o=new Panel(new GridLayout(0,1));
			o.add(labProdaja);
			Panel oo=new Panel();
			o.add(oo);
			Panel ooo=new Panel();
			ooo.add(new Label("Broj akcija za prodaju"));
			ooo.add(textProdajaBroj);
			o.add(ooo);
			Panel qqq=new Panel();
			nazadProdaja.setPreferredSize(new Dimension(150, 55));
			potvrdiProdaju.setPreferredSize(new Dimension(150, 55));
			qqq.add(nazadProdaja);
			qqq.add(potvrdiProdaju);
			o.add(qqq);
			panProdaja.add(o);
			panCeo.add(panProdaja);
			cardLayout.previous(panCeo);
			//this.pack();
			
		}
	}
	
	public int getIdFromSelection(String linija) {
		int izlaz=0;
		int brr=0;
		char c=linija.charAt(0);
		
		while(c >= '0' && c <= '9') {
			izlaz*=10;
			izlaz+=c-'0';
			c=linija.charAt(++brr);
		}
		return izlaz;
	}
	
	
	public void showPortfolio() {
		Dialog d=new Dialog(this,ModalityType.APPLICATION_MODAL);
		d.setTitle("Portfolio - "+user);
		d.setBounds(400,200,600,400);
		Panel p=new Panel(new GridLayout(0, 6));
		p.add(new Label("id"));
		p.add(new Label("Simbol akcije"));
		p.add(new Label("Broj akcija"));
		p.add(new Label("Nabavna cena"));
		p.add(new Label("Trenutna cena"));
		p.add(new Label("Relativna cena"));
		ArrayList<ArrayList<String>> lista=BazaPodataka.dohvKupljene(user, BazaPodataka.pathBaza);
		int brojac=0;
		int ogr=lista.get(0).size();
		d.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				d.dispose();
			}
		});
		
		
		for(int i=0;i<ogr;i++) {
			p.add(new Label(lista.get(0).get(i)));
			p.add(new Label(lista.get(1).get(i)));
			p.add(new Label(lista.get(2).get(i)));
			double nabavna=Double.valueOf(lista.get(3).get(i));
			p.add(new Label(String.format("%4.4f", nabavna)));
			double trenutna=ProzorMeni.dohvTrenutnuCenu(lista.get(1).get(i));
			p.add(new Label(String.format("%4.4f",trenutna)));
			double relativna=(trenutna-nabavna)/nabavna * 100;
			Label labRel=new Label();
			if(relativna >= 0)
				labRel.setForeground(Color.GREEN);
			else {
				relativna*=-1;
				labRel.setForeground(Color.RED);
			}
			labRel.setText(String.format("%4.4f", relativna)+" %");
			labRel.setFont(new Font("Arial", Font.BOLD, 0));
			p.add(labRel);
		}
		
		d.add(p);
		d.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new ProzorMeni("nikola");
	}
}
