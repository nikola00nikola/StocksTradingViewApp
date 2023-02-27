package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ProzorPrijava extends Frame {

	Button b1,b2;
	
	public ProzorPrijava() {
		super();
		this.setBounds(400,200,400,400);
		this.setPreferredSize(new Dimension(400, 400));
		this.pack();
		this.setResizable(false);
		this.setBackground(Color.CYAN);
		this.setTitle("Pridruzivanje");
		this.populateProzor();
		this.pack();
		
		this.setVisible(true);
	}

	private void populateProzor() {
		b1=new Button("Prijava");
		b2=new Button("Registacija");
		b1.setPreferredSize(new Dimension(this.getSize().width/2, this.getSize().height));
		b2.setPreferredSize(new Dimension(this.getSize().width/2, this.getSize().height));
		Panel p=new Panel();
		p.add(b1);
		p.add(b2);
		this.add(b1,BorderLayout.WEST);
		this.add(b2,BorderLayout.EAST);
		
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProzorPrijava.this.dispose();
				new ProzorLog();
			}
		});
		
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProzorPrijava.this.dispose();
				new ProzorReg();
			}
		});
		
		Menu menu=new Menu("Opcije");
		MenuItem m=new MenuItem("Nazad",new MenuShortcut(KeyEvent.VK_Q));
		MenuItem l=new MenuItem("Log in");
		MenuItem r=new MenuItem("Register");
		menu.add(l);
		menu.add(r);
		menu.addSeparator();
		menu.add(m);
		MenuBar bar=new MenuBar();
		bar.add(menu);
		setMenuBar(bar);
		
		m.addActionListener((ae)->{
			ProzorPrijava.this.dispose();
			new ProzorPocetni();
		});
		
		l.addActionListener((ae)->{
			ProzorPrijava.this.dispose();
			new ProzorLog();
		});
		
		r.addActionListener((ae)->{
			ProzorPrijava.this.dispose();
			new ProzorReg();
		});
		
	}
	
	public static void main(String[] args) {
		new ProzorPocetni();
	}
}
