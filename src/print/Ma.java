package print;

import java.util.ArrayList;

public class Ma implements Indicator{

	@Override
	public ArrayList<Double> dohvati(Stock s,int n) {
		Stock expandedStock=s.makeStockWithNInserted(n);
		ArrayList<Double> izlaz=new ArrayList<>();
		double zbir=0;
		for(int i=0;i<n-1;i++)
			zbir+=expandedStock.candles.get(i).close;
		
		int size=s.candles.size();
		
		for(int i=0;i<size;i++) {
			zbir+=s.candles.get(i).close;
			izlaz.add(zbir/n);
			zbir-=expandedStock.candles.get(i).close;
		}
		
		return izlaz;
	}

}
