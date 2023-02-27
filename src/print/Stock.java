package print;

import java.util.ArrayList;

import nativeMetode.Parser;

public class Stock {

	String company;
	long start,end;
	public ArrayList<Candle> candles;
	
	
	double minPrice() {
		if(candles.size()==0)
			return -1;
		int end=candles.size();
		int poc=end-Scena.LIMIT-1;
		final int startt= poc < 0 ? 0 : poc;
		return candles.stream().filter(t->candles.indexOf(t) >= startt && candles.indexOf(t)<end)
				.min((a,b)->a.low > b.low ? 1: -1).get().low;
	}
	
	double maxPrice() {
		if(candles.size()==0)
			return -1;
		
		int end=candles.size();
		int poc=end-Scena.LIMIT-2;
		final int startt= poc < 0 ? 0 : poc;
		return candles.stream().filter(t->candles.indexOf(t) >= startt && candles.indexOf(t) < end)
				.max((a,b)->a.high > b.high ? 1 : -1).get().high;
	}
	
	
	public Stock(String cmp,long s,long e) {
		super();
		company=cmp;
		start=(long) s;
		end=e;
		candles=new ArrayList<Candle>();
		ArrayList<ArrayList<Double>> aaa = Parser.parse(cmp, s, e);
		this.popuni(aaa);
		this.obrisiPrazne();
	}
	
	
	public Stock(Stock s,int first,int end) {
		super();
		candles=new ArrayList<Candle>();
		for(int i=first;i<end;i++)
			this.candles.add(s.candles.get(i));
		start=s.start;
		company=s.company;
		this.end=s.end;
	}
	
	void obrisiPrazne() {
		int duzina=this.brSveca();
		boolean jeNula;
		for(int i=0;i<duzina;i+= jeNula?0:1) 
			if(candles.get(i).high == 0) {
				candles.remove(i);
				duzina--;
				jeNula=true;
			}else jeNula=false;
	}
	
	public int brSveca() {
		return candles.size();
	}
	
	private void popuni(ArrayList<ArrayList<Double>> arrs) {
		for(int i=0;i<arrs.get(0).size();i++) {
			candles.add(new Candle(arrs.get(0).get(i),arrs.get(1).get(i),arrs.get(2).get(i),arrs.get(3).get(i),arrs.get(4).get(i)));
		}
	}
	
	@Override
	public String toString() {
		String s="";
		for(Candle c:candles) {
			s=s+c.toString();
			s=s+"\n";
		}
		return s;
	}
	
	
	public Stock makeStockWithNInserted(int n) {
		n--;
		int d=2592000;
		long pocetak=this.start-d;
		Stock expandedStock=new Stock(this.company, pocetak, this.end);
		int sizeOld=this.brSveca();
		int sizeNew=expandedStock.brSveca();
		
		while(sizeNew < sizeOld + n) {
			pocetak-=d;
			expandedStock=new Stock(company, pocetak, end);
			sizeNew=expandedStock.brSveca();
		}
		
		int razlika=sizeNew-sizeOld-n;
		
		for(int i=0;i<razlika;i++)
			expandedStock.candles.remove(0);

		return expandedStock;
	}
	
	
	
}
