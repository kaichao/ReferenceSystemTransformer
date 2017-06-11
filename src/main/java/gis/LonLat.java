package gis;

//import static org.apache.commons.math3.util.FastMath.*;
import static java.lang.Math.*;

/**
 * 经纬度坐标类
 * 
 * @author kaichao
 *
 */
public class LonLat {
	public double lon, lat;

	/**
	 * @param lon	经度，longitude
	 * @param lat	纬度，latitude
	 */
	public LonLat(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}

	/**
	 * 求两个坐标点之间经纬度偏差最大值
	 * 
	 * @param p
	 * @return
	 */
	public double distance(LonLat p){
		return sqrt((lon - p.lon) * (lon - p.lon)
				+ (lat - p.lat) * (lat - p.lat));
	}

	public LonLat add(LonLat p) {
		return new LonLat(this.lon + p.lon, this.lat + p.lat);
	}

	public LonLat minus(LonLat p) {
		return new LonLat(this.lon - p.lon, this.lat - p.lat);
	}

	public String toString() {
		return String.format("[%s,\t%s]", String.valueOf(lon),
				String.valueOf(lat));
	}

	/**
	 * 坐标点间距离小于epsilon，则认定相等
	 */
	public boolean equals(Object p) {
		LonLat that = (LonLat) p;
		return distance(that) < epsilon;
	}

	// 可通过全局属性来设定坐标点距离偏差的最小值
	private static double epsilon;
	static {
		try{
			epsilon = Double.parseDouble(System.getProperty("epsilon"));
		} catch(Exception ex){
			epsilon = 1E-7;
		}
	}
}
