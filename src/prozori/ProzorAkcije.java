package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;

import print.Ema;
import print.Ma;
import print.Scena;
import print.Stock;


public class ProzorAkcije extends Frame {

	Scena scena;
	
	Panel desniPanel=new Panel(new GridLayout(0, 1));
	public Label labelVrednost=new Label("0        ");
	public Label labelKomp=new Label("Prikaz akcija");
	public Label labelOpen=new Label("              ");
	public Label labelClose=new Label("              ");
	public Label labelHigh=new Label("              ");
	public Label labelLow=new Label("              ");
	public Label labelTime=new Label("                ");
	public ProzorAkcije() {
		super();
		this.setBounds(100, 100, 900, 500);
		this.setResizable(false);
		this.populateWindow();
		this.showSetupDialog();
		this.pack();
		
		this.setVisible(true);
	}
	
	private void showSetupDialog() {
		Dialog setup=new Dialog(this,ModalityType.APPLICATION_MODAL);
		setup.setBackground(Color.CYAN);
		long curent=System.currentTimeMillis();
		Timestamp curTime=new Timestamp(curent);
		long triMeseca=7948800;
		triMeseca*=1000;
		Timestamp startTime=new Timestamp(curent-triMeseca);
		TextField poljePocetak=new TextField(startTime.toString());
		TextField poljeKraj=new TextField(curTime.toString());
		TextField poljeKomp=new TextField();
		Button b=new Button("Potvrdi");		
		Label lKr=new Label("Do: "),lP=new Label("Od: "),lK=new Label("Oznaka akcije: ");
		Label labelGreska=new Label("");
		labelGreska.setFont(new Font("Monospaced", Font.BOLD, 18));
		labelGreska.setAlignment(Label.CENTER);
		lKr.setFont(new Font("Monospaced", Font.ITALIC, 18));
		lP.setFont(new Font("Monospaced", Font.ITALIC, 18));
		lK.setFont(new Font("Monospaced", Font.ITALIC, 18));
		Panel p=new Panel(new GridLayout(0,1));
		p.add(lK);
		p.add(poljeKomp);
		p.add(lP);
		p.add(poljePocetak);
		p.add(lKr);
		p.add(poljeKraj);
		Label labelVreme=new Label("format vremena:   YYYY-MM-DD hh:mm:ss");
		//labelVreme.setAlignment(Label.CENTER);
		labelVreme.setFont(new Font("Monospaced", Font.ITALIC,12));
		p.add(labelVreme);
		p.add(labelGreska);
		Panel q=new Panel(new GridLayout(0,1));
		setup.add(p,BorderLayout.NORTH);
		q.add(b);
		setup.add(q,BorderLayout.SOUTH);
		setup.setTitle("Biranje akcija za posmatranje");
		setup.setBounds(400,200,400,400);
		setup.setResizable(false);
		
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
				try {
				String cmp=poljeKomp.getText(),start=poljePocetak.getText(),end=poljeKraj.getText();
				if(cmp.length()>0 && start.length()>0 && end.length()>0)
				{
					Timestamp t1=Timestamp.valueOf(start);
					Timestamp t2=Timestamp.valueOf(end);
					ProzorAkcije.this.scena.setStock(new Stock(cmp, t1.getTime()/1000, t2.getTime()/1000));
					if(ProzorAkcije.this.scena.stock.brSveca() > 0)
						setup.dispose();
					else
						labelGreska.setText("Nema akcija za dati unos!");
				}else {
					labelGreska.setText("Sva polja su obavezna!");
				}
				}catch(IllegalArgumentException aE) {
					labelGreska.setText("Neispravan format datuma!");
				}
				
			}
		});
		
		
		setup.setVisible(true);
		
	}

	private void populateWindow() {
		scena=new Scena(this);
		desniPanel.setPreferredSize(new Dimension(350, 500));
		
		labelVrednost.setFont(new Font("Monospaced", Font.ITALIC, 18));
		labelKomp.setFont(new Font("Monospaced", Font.BOLD, 26));
		labelKomp.setAlignment(Label.CENTER);
		this.add(scena,BorderLayout.CENTER);
		
		labelOpen.setFont(new Font("Monospaced", Font.ITALIC, 15));
		labelClose.setFont(new Font("Monospaced", Font.ITALIC, 15));
		labelHigh.setFont(new Font("Monospaced", Font.ITALIC, 15));
		labelLow.setFont(new Font("Monospaced", Font.ITALIC, 15));
		
		Panel p=new Panel();
		Label l=new Label("Vrednost[$] :");
		l.setFont(new Font("Monospaced", Font.ITALIC, 18));
		desniPanel.add(labelKomp);
		l.setAlignment(Label.LEFT);
		p.add(l);
		p.add(labelVrednost);
		desniPanel.add(p);
		Panel pp=new Panel(new GridLayout(0, 1));
		labelTime.setAlignment(Label.CENTER);
		
		Label info=new Label("Za vise info");
		Label info2=new Label("kliknite na svecu");
		Label info3=new Label("kretanje po istoriji: A - D");
		info.setAlignment(Label.CENTER);
		info.setFont(new Font("Monospaced", Font.BOLD, 22));
		info2.setAlignment(Label.CENTER);
		info2.setFont(new Font("Monospaced", Font.BOLD, 22));
		info3.setAlignment(Label.CENTER);
		info3.setFont(new Font("Monospaced", Font.PLAIN, 18));
		pp.add(info3);
		pp.add(info);
		pp.add(info2);
		labelTime.setFont(new Font("Monospaced", Font.PLAIN, 16));
		pp.add(labelTime);
		labelTime.setAlignment(Label.CENTER);
		pp.add(labelOpen);
		pp.add(labelClose);
		pp.add(labelHigh);
		pp.add(labelLow);
		desniPanel.add(pp);
		desniPanel.setBackground(Color.CYAN);
		this.add(desniPanel,BorderLayout.EAST);
		
		Menu meniSvece=new Menu("Svece");
		MenuItem prikazi=new MenuItem("Prikazi");
		MenuItem sakriji=new MenuItem("Sakriji");
		meniSvece.add(prikazi);
		meniSvece.add(sakriji);
		
		prikazi.addActionListener(a->{
			ProzorAkcije.this.scena.prikaziSvece=true;
			ProzorAkcije.this.scena.repaint();
		});
		
		sakriji.addActionListener(a->{
			ProzorAkcije.this.scena.prikaziSvece=false;
			ProzorAkcije.this.scena.repaint();
		});
		
		Menu meni=new Menu("Opcije");
		meni.add(meniSvece);
		MenuItem nazad=new MenuItem("Nazad", new MenuShortcut(KeyEvent.VK_Q));
		MenuItem promeni=new MenuItem("Promeni pregled");
		meni.add(promeni);
		Menu param=new Menu("Dodatni indikatori");
		MenuItem p1=new MenuItem("MA indikator");
		MenuItem p2=new MenuItem("EMA indikator");
		MenuItem p3=new MenuItem("Obrisi indikatore");
		
		
		param.add(p1);
		param.add(p2);
		param.add(p3);
		p1.addActionListener(ae->{
			Dialog d=new Dialog(ProzorAkcije.this,ModalityType.APPLICATION_MODAL);
			TextField t=new TextField();
			Button bb=new Button("Potvrdi");
			bb.addActionListener(aee->{
				try {
					int n=Integer.valueOf(t.getText());
					d.dispose();
					scena.maNiz=new Ma().dohvati(ProzorAkcije.this.scena.stock, n);
					scena.ma=true;
					scena.repaint();
				}catch (NumberFormatException nExc) {
					d.dispose();
				}
			});
			d.setBounds(200,200,300,120);
			d.setTitle("Ma indikator");
			d.add(new Label("Unesite n:"),BorderLayout.NORTH);
			d.add(t,BorderLayout.CENTER);
			d.add(bb,BorderLayout.SOUTH);
			d.setVisible(true);
			
			d.pack();
			
		});
		
		p2.addActionListener(ae->{
			Dialog d=new Dialog(ProzorAkcije.this,ModalityType.APPLICATION_MODAL);
			TextField t=new TextField();
			Button bb=new Button("Potvrdi");
			bb.addActionListener(aee->{
				try {
					int n=Integer.valueOf(t.getText());
					d.dispose();
					scena.emaNiz=new Ema().dohvati(ProzorAkcije.this.scena.stock, n);
					scena.ema=true;
					scena.repaint();
				}catch (NumberFormatException e) {
					d.dispose();
				}
			});
			d.setBounds(200,200,300,120);
			d.setTitle("EMa indikator");
			d.add(new Label("Unesite n:"),BorderLayout.NORTH);
			d.add(t,BorderLayout.CENTER);
			d.add(bb,BorderLayout.SOUTH);
			d.setVisible(true);
			
			d.pack();
			
		});
		
		
		p3.addActionListener((aAa->{
			scena.ema=false;
			scena.ma=false;
			scena.emaNiz=null;
			scena.maNiz=null;
			scena.repaint();
		}));
		
		
		nazad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProzorAkcije.this.dispose();
				new ProzorPocetni();
			}
		});
		
		promeni.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProzorAkcije.this.dispose();
				new ProzorAkcije();
			}
		});
		MenuBar bar=new MenuBar();
		meni.add(param);
		meni.addSeparator();
		meni.add(nazad);
		bar.add(meni);
		this.setMenuBar(bar);
	}

	public static void main(String[] args) {
		new ProzorAkcije();
	}
}
