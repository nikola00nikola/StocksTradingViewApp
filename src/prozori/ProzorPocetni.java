package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProzorPocetni extends Frame {

	Button b1,b2;
	
	public ProzorPocetni() {
		super();
		this.setBounds(400,200,400,400);
		this.setPreferredSize(new Dimension(400, 400));
		this.pack();
		this.setResizable(false);
		this.setBackground(Color.CYAN);
		this.setTitle("Akcije");
		this.populateProzor();
		this.pack();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ProzorPocetni.this.dispose();
			}
		});
		
		this.setVisible(true);
	}

	private void populateProzor() {
		b1=new Button("Pracenje cene akcije");
		b2=new Button("Kupovina/prodaja");
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
				ProzorPocetni.this.dispose();
				new ProzorAkcije();
			}
		});
		
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProzorPocetni.this.dispose();
				new ProzorPrijava();
			}
		});
	}
	
	public static void main(String[] args) {
		new ProzorPocetni();
	}
}
