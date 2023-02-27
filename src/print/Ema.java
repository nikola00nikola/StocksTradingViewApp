package print;

import java.util.ArrayList;

public class Ema implements Indicator {

	@Override
	public ArrayList<Double> dohvati(Stock s, int n) {
		Stock expandedStock=s.makeStockWithNInserted(n);
		ArrayList<Double> izlaz=new ArrayList<>();
		double zbir=0;
		
		int size=s.brSveca();

		for(int i=0;i<n-1;i++)
			zbir+=expandedStock.candles.get(i).close;
		for(int i=0;i<n;i++) {
			zbir+=s.candles.get(i).close;
			izlaz.add(zbir/n);
			zbir-=expandedStock.candles.get(i).close;
		}
		
		for(int i=n;i<size;i++) {
			double val=s.candles.get(i).close*2./(n+1)+izlaz.get(i-1)*(1-2./(n+1));
			izlaz.add(val);
		}
		
		
		
		return izlaz;
	}

}
