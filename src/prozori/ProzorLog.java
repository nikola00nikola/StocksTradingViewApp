package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import nativeMetode.BazaPodataka;

public class ProzorLog extends Frame {

	Label labelNatpis;
	Label labelIspis;
	Button button;
	TextField polje;
	int mod=1;
	private String username,password;
	public ProzorLog() {
		super();
		this.setBounds(400,200,400,400);
		this.setPreferredSize(new Dimension(400, 400));
		this.setResizable(false);
		this.setBackground(Color.CYAN);
		this.setTitle("Prijavljivanje");
		this.populateLog();
		this.pack();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ProzorLog.this.dispose();
				new ProzorPrijava();
			}
		});
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mod==1 && polje.getText().length() > 0) {
					username=polje.getText().toLowerCase();
					if(!BazaPodataka.isThisUserNameTaken(username, BazaPodataka.pathBaza))
						labelNatpis.setText("Ne postoji nalog sa datim imenom");
					else {
						labelNatpis.setText("");
						labelIspis.setText("Unesite lozinku");
						polje.setText("");
						button.setLabel("Prijavi se");
						mod++;
					}
				}else if(mod==2 && polje.getText().length() > 0){
					password=polje.getText();
					if(!BazaPodataka.isThisTruePassword(username, password, BazaPodataka.pathBaza))
						labelNatpis.setText("Uneta lozinka je netacna");
					else {
						new ProzorMeni(ProzorLog.this.username);
						ProzorLog.this.dispose();
					}

				}
			}
		});
		
		this.setVisible(true);
	}
	
	public void populateLog() {
		Panel s=new Panel(new GridLayout(0, 1));
		polje=new TextField();
		polje.setFont(new Font("Monospaced", Font.PLAIN, 14));
		labelIspis=new Label("Unesite korisnicko ime:");
		labelNatpis=new Label("                        ");
		labelIspis.setFont(new Font("Monospaced", Font.ITALIC, 18));
		labelNatpis.setFont(new Font("Monospaced", Font.ITALIC, 16));
		button=new Button("Dalje");
		
		s.add(labelIspis);
		s.add(polje);
		s.add(labelNatpis);
		this.add(s,BorderLayout.NORTH);
		
		Panel j=new Panel(new GridLayout(0, 1));
		j.add(button);
		this.add(j,BorderLayout.SOUTH);
		
	}
	
	public static void main(String[] args) {
		new ProzorLog();
	}
}
