package gis;

import gis.pip.PolygonOperator;

import java.util.Arrays;

//import static org.apache.commons.math3.util.FastMath.*;
import static java.lang.Math.*;

/**
 * 
 * Reference System Transformer
 * 		WGS-84、GCJ-02、BD-09三种参考系统的坐标变换
 * 
 * 
 * @author kaichao
 *
 */
public class RefSysTransformer {
	/**
	 * 	坐标系 取值为 "WGS-84"、"GCJ-02"、"BD-09"
	 * 
	 * 			WGS-84	GCJ-02	BD-09
	 * 	WGS-84		X		0		1
	 * 	GCJ-02		2		X		3
	 * 	BD-09		4		5		X
	 * 
	 * @param source	原始坐标系
	 * @param target	目标坐标系
	 * 
	 */
	public RefSysTransformer(String source, String target) {
		String [] coords = {"WGS-84", "GCJ-02", "BD-09"};
		int i1 = Arrays.asList(coords).indexOf(source);
		int i2 = Arrays.asList(coords).indexOf(target);
		if(i1 == -1 || i2 == -1 || i1==i2){
			System.out.println("wrong coordinate system transform parameter!");
		}
		op = ops[i1][i2];
		po = new PolygonOperator("data/chinamap-128.pip");
	}
	private static byte[][] ops = {{-1,0,1},{2,-1,3},{4,5,-1}};
	private int op=0;
	PolygonOperator po;
	/**
	 * 计算转换后的坐标点
	 * @param p
	 * @return
	 */
	public LonLat getTransformed(LonLat p){
		switch(op){
		case 0:
			return wgs2gcj(p);
		case 1:
			return wgs2bd(p);
		case 2:
			return gcj2wgs(p);
		case 3:
			return gcj2bd(p);
		case 4:
			return bd2wgs(p);
		case 5:
			return bd2gcj(p);
		default:
			return p;
		}
	}

	private LonLat wgs2gcj(LonLat p) {
		if (!po.containsPoint(p)) {
			return p;
		}
		return p.add(offset(p));
	}
    //
    // Krasovsky 1940
    // a = 6378245.0, 1/f = 298.3
    // b = a * (1 - f)
    // ee = (a^2 - b^2) / a^2;
	private static double A = 6378245.0;
	private static double EE = 0.00669342162296594323;

	/*
	 * 求变换前后的偏差值
	 */
	private LonLat offset(LonLat p) {
		double x = p.lon - 105;
		double y = p.lat - 35;
		double lon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* sqrt(abs(x));
		lon += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0;
		lon += (20.0 * sin(x * PI) + 40.0 * sin(x / 3.0 * PI)) * 2.0 / 3.0;
		lon += (150.0 * sin(x / 12.0 * PI) + 300.0 * sin(x * PI / 30.0)) * 2.0 / 3.0;

		double lat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
				+ 0.2 * sqrt(abs(x));
		lat += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0;
		lat += (20.0 * sin(y * PI) + 40.0 * sin(y / 3.0 * PI)) * 2.0 / 3.0;
		lat += (160.0 * sin(y / 12.0 * PI) + 320.0 * sin(y * PI / 30.0)) * 2.0 / 3.0;

		double radLat = p.lat / 180.0 * PI;
		double magic = sin(radLat);
		magic = 1 - EE * magic * magic;
		double sqrtMagic = sqrt(magic);
		lat = (lat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
		lon = (lon * 180.0) / (A / sqrtMagic * cos(radLat) * PI);
		return new LonLat(lon, lat);
	}

	/**
	 * 2轮计算，测试表明，精度高于1E-8，折合长度在1毫米内
	 * @param p
	 * @return
	 */
	private LonLat gcj2wgs(LonLat p){
		if (!po.containsPoint(p)) {
			return p;
		}

		LonLat p1, p2, p3;
		// 正向求解
		p1 = p.minus(offset(p));
		// 反向求解
		p2 = p1.add(offset(p1));
		// 修正点
		p3 = p.add(p).minus(p2);
		return p3.minus(offset(p3));
	}

	/**
	 * 通过二次转换实现，WGS --> GCJ --> BD
	 * @param p
	 * @return
	 */
	private LonLat wgs2bd(LonLat p){
		return gcj2bd(wgs2gcj(p));
	}

	/**
	 * 通过二次转换实现，BD --> GCJ --> WGS
	 * @param p
	 * @return
	 */
	private LonLat bd2wgs(LonLat p){
		return gcj2wgs(bd2gcj(p));
	}

	/**
	 * GCJ <--> BD，二者之间的变换为可逆过程
	 * @param p
	 * @return
	 */
	private LonLat gcj2bd(LonLat p){
		double x = p.lon;
		double y = p.lat;
		double z = sqrt(x * x + y * y) + 0.00002
				* sin(y * PI);
		double theta = atan2(y, x) + 0.000003
				* cos(x * PI);
		return new LonLat(z * cos(theta) + 0.0065, z
				* sin(theta) + 0.006);
	}

	private LonLat bd2gcj(LonLat p){
		double x = p.lon - 0.0065;
		double y = p.lat - 0.006;
		double z = sqrt(x * x + y * y) - 0.00002
				* sin(y * PI);
		double theta = atan2(y, x) - 0.000003
				* cos(x * PI);
		return new LonLat(z * cos(theta), z * sin(theta));
	}
}
