package print;


public class Candle {

	public long timestamp;
	public double open,close,high,low;
	
	public Candle(double t,double o,double c,double h,double l) {
		timestamp=(long) t;
		open=o;
		close=c;
		high=h;
		low=l;
	}
	
	@Override
	public String toString() {
		return timestamp+" ,"+open+" ,"+close+" ,"+high+" ,"+low;
	}
	
}
