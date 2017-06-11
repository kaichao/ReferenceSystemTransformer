package gis.pip;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import gis.LonLat;
/**
 * 
 * @author kaichao<wukaichao@gmail.com>
 *
 */
public class PolygonOperator {
	public PolygonOperator(){
		this("main.pip");
	}
	private EnvelopeExt env;
	private long [][] borders;
	public PolygonOperator(String pipFile){
		DataInputStream in;
		try{
			if (pipFile.endsWith(".gz")) {
				in = new DataInputStream(new GZIPInputStream(
						new FileInputStream(pipFile)));
			} else {
				in = new DataInputStream(new FileInputStream(pipFile));
			}
			double x0,x1,y0,y1;
			// points per degree
			int ppd;
			x0 = in.readDouble();
			x1 = in.readDouble();
			y0 = in.readDouble();
			y1 = in.readDouble();
			ppd = in.readInt();
			env = new EnvelopeExt(x0,x1,y0,y1,ppd);
			int n = in.readInt();
			borders = new long[2][n];
			for(int i=0;i<n;i++){
				borders[0][i] = in.readLong();
			}
			for(int i=0;i<n;i++){
				borders[1][i] = in.readLong();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	public boolean containsPoint(LonLat p){
		return containsPoint(p.lon, p.lat);
	}
	public boolean containsPoint(double lon, double lat){
		boolean found = false;
		try {
			long[] ls = env.getIndexes(lon, lat);
			for (long key : ls) {
//			for (long key : env.getIndexes(lon, lat)) {
				int n = Arrays.binarySearch(borders[0], key);
				if (n >= 0) {
					found = true;
				} else {
					n = -n - 2;
					found = n >= 0 && key <= borders[1][n];
				} 
				if (found)
					break;
			}
		} catch (OutOfRangeException e) {
			System.out.println(e.getMessage());
			found = false;
		}
		return found;
	}
//	China's border :[72.004 0.8293,137.8347 55.8271]
}
