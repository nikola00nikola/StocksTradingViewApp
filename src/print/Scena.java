package print;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import prozori.ProzorAkcije;

public class Scena extends Canvas {

	static final int LIMIT=50;
	static final int STEP=5;
	public Stock stock;
	public boolean ma,ema;
	public ArrayList<Double> emaNiz,maNiz;
	double MIN,MAX;
	ProzorAkcije prozor;
	float faktorX=1,faktorY=1;
	int sirinaProzora,visinaProzora;
	int sirinaPolja,pomerajX,widthP;
	int brojPrik=LIMIT;
	int first=-1;
	public boolean prikaziVrednost,prikaziSvece=true;
	public boolean ispisiVrMaUTacki=false;
	int misY,misX;
	double d;
	public Scena(ProzorAkcije p){
		super();
		prozor=p;
		this.setPreferredSize(new Dimension(prozor.getWidth(), prozor.getHeight()));
		sirinaProzora=prozor.getWidth();
		visinaProzora=prozor.getHeight();
		sirinaPolja=(sirinaProzora-(brojPrik+1)*STEP)/brojPrik;
		
		
		this.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				misY=e.getY();
				misX=e.getX();
				prikaziVrednost=true;
				Scena.this.repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				prikaziVrednost=false;
				Scena.this.repaint();
			}
		});
		
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c=e.getKeyChar();
				if(c == '-')
					Scena.this.zumiraj_umanji();
				else if(c == 'a') 
					Scena.this.pomeriUlevo();
				else if(c == 'd')
					Scena.this.pomeriUdesno();
				else if(c== 'i')
					Scena.this.zumirajVer();
				else if(c== 'k')
					Scena.this.zumiraj_umanjiVer();
				else if(c== '+')
					Scena.this.zumiraj();
				else if(c== 'w')
					Scena.this.pomeriNagore();
				else if(c== 's')
					Scena.this.pomeriNadole();
			}
		
		});
	}
	
	public void setStock(Stock s) {
		if(s==null)
			return;
		stock=s;
		this.repaint();
		MIN=s.minPrice();
		MAX=s.maxPrice();
		d=MAX*0.01;
		this.prozor.labelKomp.setText("Prikaz akcija : "+this.stock.company);
		first=stock.brSveca()-brojPrik-2;
		if(first<0)
			first=0;
	}
	
	void pomeriUlevo() {
		first=first-2;
		if(first < 0)
			first=0;
		//System.out.println("first= "+first);
		this.repaint();
	}
	
	void pomeriUdesno() {
		first=first+2;
		if(first >= this.stock.candles.size())
			first=this.stock.candles.size()-1;
		//System.out.println("first= "+first);
		this.repaint();
	}
	
	void pomeriNadole() {
		MAX-=d;
		MIN-=d;
		this.repaint();
	}
	
	void pomeriNagore() {
		MAX+=d;
		MIN+=d;
		this.repaint();
	}
	
	void zumiraj() {
		faktorX+=0.5;
		this.repaint();
	}
	
	void zumiraj_umanji() {
		if(faktorX > 0.6)
			faktorX-=0.5;
		this.repaint();
	}
	
	void zumirajVer() {
		faktorY-=0.02;
		if(faktorY<1)
			faktorY=1;
		this.repaint();
	}
	
	void zumiraj_umanjiVer() {
		if(faktorY < 1.2)
			faktorY+=0.02;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//System.out.println("W= "+this.getBounds().width+" H= "+this.getBounds().height);
		if(this.stock==null)
			return;
		double min=MIN/faktorY;
		double max=MAX*faktorY;
		
		widthP=(int)(sirinaPolja*faktorX);
		if(!prikaziSvece)
			widthP=5;
		pomerajX=Scena.STEP+widthP;
		//(widthP+10)*brPrik+10=SirinaProzora
		brojPrik=(sirinaProzora-STEP)/(widthP+STEP);
		int end=first+brojPrik;
		if(end > stock.candles.size())
			end=stock.candles.size();
		//System.out.println("first= "+first+" last= "+end);
		AtomicReference<Integer> heightP=new AtomicReference<>()
				,y=new AtomicReference<>()
				,x=new AtomicReference<>(STEP);
		final int END=end;
		System.out.println(first+" , "+END);
		Scena.this.setPreferredSize(new Dimension((END-first)*widthP+(END-first)*STEP,visinaProzora));
		Stock s=new Stock(stock, first, END);
		s.candles.stream().forEach(c->{
			double H,L;
			int y0,y1;
			if(c.open>=c.close) {
				H=c.open;
				L=c.close;
				g.setColor(Color.RED);
			}else {
				H=c.close;
				L=c.open;
				g.setColor(Color.GREEN);
			}
			heightP.set((int)((H-L)*visinaProzora/(max-min)));
			y.set((int)((max-H)*visinaProzora/(max-min)));
			y0=(int)((max-c.high)*visinaProzora/(max-min));
			y1=(int)((max-c.low)*visinaProzora/(max-min));
			if(heightP.get()<1)
				heightP.set(1);
			
			int i=this.stock.candles.indexOf(c);
			if(prikaziSvece) {
				g.fillRect(x.get(), y.get(), widthP, heightP.get());
				g.drawLine(x.get()+widthP/2, y0, x.get()+widthP/2, y1);
			}/*else if(ma) {
				y0=(int)((max-H)*visinaProzora/(max-min));
				y1=(int)((max-L)*visinaProzora/(max-min));
				int yMa=(int)((max-maNiz.get(i))*visinaProzora/(max-min));
				if(yMa > y0 && yMa< y1) {
					g.setColor(Color.BLACK);
					g.drawLine(x.get()+widthP/2, y0, x.get()+widthP/2, yMa);
					g.setColor(Color.YELLOW);
					g.drawLine(x.get()+widthP/2, y1, x.get()+widthP/2, yMa);
				}else {
					g.drawLine(x.get()+widthP/2, y0, x.get()+widthP/2, y1);
				}
			}*/
			
			if(ma && i<END-1) {
				int yMa1,yMa2;
				yMa1=(int)((max-maNiz.get(i))*visinaProzora/(max-min));
				yMa2=(int)((max-maNiz.get(i+1))*visinaProzora/(max-min));
				g.setColor(Color.BLUE);
				g.drawLine(x.get()+widthP/2, yMa1, x.get()+widthP/2+pomerajX, yMa2);
			}
			
			if(ema && i<END-1) {
				int yeMa1,yeMa2;
				yeMa1=(int)((max-emaNiz.get(i))*visinaProzora/(max-min));
				yeMa2=(int)((max-emaNiz.get(i+1))*visinaProzora/(max-min));
				g.setColor(Color.MAGENTA);
				g.drawLine(x.get()+widthP/2, yeMa1, x.get()+widthP/2+pomerajX, yeMa2);
			}
			//System.out.println("o: "+c.open+"   c: "+c.close+" height: "+heightP+" max: "+max+" min: "+min);
			int newValue=x.get()+pomerajX;
			x.set(newValue);
			Scena.this.prozor.pack();
		});
		
		
		if(prikaziVrednost) {
			g.setColor(Color.GRAY);
			g.drawLine(0, misY, sirinaProzora, misY);
			g.drawLine(misX, 0, misX, visinaProzora);
			this.prozor.labelVrednost.setText(String.valueOf(max-misY*(max-min)/visinaProzora)+"$");
			int index=misX/(Scena.STEP+widthP);
			index=index+first;
			
			if(ispisiVrMaUTacki && index!=0 && maNiz!=null) {
				double Y1=maNiz.get(index-1);
				double Y2=maNiz.get(index);
				int X1=widthP/2+Scena.STEP+(index-first-1)*(Scena.STEP+widthP);
				int X2=X1+Scena.STEP+widthP;
				
				double k=(Y1-Y2)/(X1-X2);
				double n=Y2-k*X2;
				System.out.println("x1= "+X1+" x2= "+X2+" mis "+misX);
				System.out.println(k*misX+n);
			}
			
			if(index >= end) {
				this.prozor.labelOpen.setText("          ");
				this.prozor.labelClose.setText("          ");
				this.prozor.labelHigh.setText("          ");
				this.prozor.labelLow.setText("          ");
				this.prozor.labelTime.setText("          ");
			}else {
			//System.out.println("index"+index);
			Candle c=stock.candles.get(index);
			this.prozor.labelOpen.setText("open: "+String.valueOf(c.open));
			this.prozor.labelClose.setText("close: "+String.valueOf(c.close));
			this.prozor.labelHigh.setText("high: "+String.valueOf(c.high));
			this.prozor.labelLow.setText("low: "+String.valueOf(c.low));
			Timestamp t=new Timestamp((c.timestamp-7200)*1000);//7200 -> GMT+2 srb
			this.prozor.labelTime.setText("vreme: "+t.toString());
			}
		}

	}
	
	
}
