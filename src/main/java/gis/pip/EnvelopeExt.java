package gis.pip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import static org.apache.commons.math3.util.FastMath.*;
import static java.lang.Math.*;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.ogc.OGCGeometry;
import com.esri.core.geometry.ogc.OGCLineString;
import com.esri.core.geometry.ogc.OGCMultiLineString;
import com.esri.core.geometry.ogc.OGCPoint;
import com.google.common.base.Objects;

/**
 * 
 * @author kaichao<wukaichao@gmail.com>
 *
 */
public class EnvelopeExt{
	private double x0,x1,y0,y1;
	private long nx, nx0, nx1, ny0, ny1;
	// points per degree
	private int ppd;
	// tolerance precision
	private static double tolerance = 1e-8;
	private static double roundingError = 1e-11;
	/**
	 * 
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @param ppd		resolution, points per degree
	 */
	public EnvelopeExt(double x0, double x1, double y0, double y1, int ppd) {
		setParam(x0,x1,y0,y1,ppd);
	}
	public EnvelopeExt(double x0, double x1, double y0, double y1) {
		this(x0,x1,y0,y1,10);
	}
	public EnvelopeExt() {
		this(-180,180,-90,90);
	}
	public EnvelopeExt(String wkt) {
		this(wkt,10);
	}

	public EnvelopeExt(String wkt, int ppd) {

	}
	private void setParam(double x0, double x1, double y0, double y1, int ppd){
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		this.ppd = ppd;

		nx1 = (int)ceil((x1)*ppd)-1;
		ny1 = (int)ceil((y1)*ppd)-1;
		nx0 = (int)floor((x0)*ppd);
		ny0 = (int)floor((y0)*ppd);
		nx = nx1 - nx0 + 1;
	}
	/**
	 * get indexes of 4 points 
	 * @param x
	 * @param y
	 * @return
	 */
	public long[] getIndexes(double x, double y){
		idx[0] = getIndex(x - tolerance, y - tolerance);
		idx[1] = getIndex(x - tolerance, y + tolerance);
		idx[2] = getIndex(x + tolerance, y - tolerance);
		idx[3] = getIndex(x + tolerance, y + tolerance);
		// remove duplicated elements
//		int n = 4;
//		for(int i=0;i<n-1;i++){
//			for(int j=i+1;j<n;j++){
//				if(idx[i] == idx[j]){
//					for(int k=i+1;k<n;k++){
//						idx[k-1] = idx[k];
//					}
//					n--,i--;
//					break;
//				}
//			}
//		}
//		return Arrays.copyOf(idx, n);
		if(idx[0] == idx[3] && idx[1] == idx[3] && idx[2] == idx[3] ){
			return new long[]{idx[0]};
		}
		return idx;
	}
	private long[] idx = new long[4];
	private long getIndex(double x, double y){
		long ix = round(x*ppd-0.5);
		long iy = round(y*ppd-0.5);
//		long ix = mathFloor(x*ppd);
//		long iy = mathFloor(y*ppd);
		if(ix<nx0 || ix >= nx1 || iy<ny0 || iy>=ny1){
			return -1;
		} else {
			return (ix-nx0) + (iy-ny0) * nx;
		}
	}
	/*
	 * High performance version of Math.floor(d)
	 */
	public long mathFloor(double d){
		if (d >= 0) {	// positive number
			return (long) d;
		} else if ((long) d == d) {
			//negative integer
			return (long) d;
		} else {
			return -((long) (-d) + 1);
		}
	}

	public long[][] makeBorderData(String wkt) {
		OGCGeometry poly = OGCGeometry.fromText(wkt);
		List<Long> li = new ArrayList<Long>();
		double xs,xe,y;

		Envelope env = new Envelope();
		poly.getEsriGeometry().queryEnvelope(env);
		long nys = (long)floor((env.getYMin())*ppd);
		long nye = (long)floor((env.getYMax())*ppd);

		for(long j=nys;j<=nye;j++){
			String ls = String.format("LINESTRING(%s %s,%s %s)", 
					Double.toString(x0),Double.toString((j+0.5)/ppd), 
					Double.toString(x1),Double.toString((j+0.5)/ppd));
			OGCGeometry line = OGCGeometry.fromText(ls);
			OGCGeometry g = poly.intersection(line);
			if(g.isEmpty()){
				continue;
			}
			if(g instanceof OGCLineString){
				OGCPoint s = ((OGCLineString) g).startPoint();
				OGCPoint e = ((OGCLineString) g).endPoint();
				xs = s.X() + roundingError;
				xe = e.X() - roundingError;
				y = s.Y();
				li.add(getIndex(xs,y));
				li.add(getIndex(xe,y));
			} else if(g instanceof OGCMultiLineString){
				OGCMultiLineString ml = (OGCMultiLineString)g;
				for(int i=0;i<ml.numGeometries();i++){
					OGCLineString gg = (OGCLineString)ml.geometryN(i);
					OGCPoint s = gg.startPoint();
					OGCPoint e = gg.endPoint();
					xs = s.X() + roundingError;
					xe = e.X() - roundingError;
					y = s.Y();
					li.add(getIndex(xs,y));
					li.add(getIndex(xe,y));
				}
			}
		}
		long[][] ret = new long[2][li.size()/2];
		for(int i=0;i<li.size()/2;i++){
			ret[0][i] = li.get(2*i);
			ret[1][i] = li.get(2*i+1);
		}
		return ret;
	}

	@Override
	public String toString() {
		return String.format("x0=%s, x1=%s, y0=%s, y1=%s, ppd=%d",
				Double.toString(x0), Double.toString(x1), Double.toString(y0),
				Double.toString(y1), ppd);
	}

	@Override
	public boolean equals(Object o){
		EnvelopeExt a = (EnvelopeExt) o;
		return x0==a.x0 && x1==a.x1 && y0==a.y0 && y1==a.y1 && ppd==a.ppd;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(x0,x1,y0,y1,ppd);
	}
	public void output(DataOutputStream out) {
		try {
			out.writeDouble(x0);
			out.writeDouble(x1);
			out.writeDouble(y0);
			out.writeDouble(y1);
			out.writeInt(ppd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}