package gis;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import gis.pip.PolygonOperator;
import gis.pip.PolygonPreprocessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * 点在多边形内算法的测试类
 * 
 * @author kaichao
 *
 */
public class PipTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test() throws Exception{
		System.out.format("PI:%s\n", String.valueOf(Math.PI));
		long[] arr = {10,20,30,40,50,60,70,80,90,100};
		for(int i=5;i<110;i+=5){
			System.err.format("%d,%d\n",i,Arrays.binarySearch(arr, i));
		}
		
	}

//	@Test
	public void testDataInputOutput() throws Exception{
		short s = 1;
		int i = 2;
		long l = 3;
		float f = 4;
		double d = 5;

		DataOutputStream out = new DataOutputStream(new FileOutputStream("/tmp/abc"));
		out.writeShort(s);
		out.writeInt(i);
		out.writeLong(l);
		out.writeFloat(f);
		out.writeDouble(d);
		out.close();

		DataInputStream in = new DataInputStream(new FileInputStream("/tmp/abc"));
		assertEquals(in.readShort(),s);
		assertEquals(in.readInt(),i);
		assertEquals(in.readLong(),l);
		assertEquals(in.readFloat(),f,1E-10);
		assertEquals(in.readDouble(),d,1E-10);
		in.close();
	}

	@Test
	public void testLittleEndianDataStream() throws Exception{
		short s = 1;
		int i = 2;
		long l = 3;
		float f = 4;
		double d = 5;

		LittleEndianDataOutputStream out = new LittleEndianDataOutputStream(
				new FileOutputStream("/tmp/abc"));
		out.writeShort(s);
		out.writeInt(i);
		out.writeLong(l);
		out.writeFloat(f);
		out.writeDouble(d);
		out.close();

		LittleEndianDataInputStream in = new LittleEndianDataInputStream(
				new FileInputStream("/tmp/abc"));
		assertEquals(in.readShort(),s);
		assertEquals(in.readInt(),i);
		assertEquals(in.readLong(),l);
		assertEquals(in.readFloat(),f,1E-10);
		assertEquals(in.readDouble(),d,1E-10);
		in.close();
	}
	@Test
	public void testChinaMap() throws Exception{
		int ppd = 128;
		String pipFile = String.format("data/chinamap-%03d.pip",ppd);

		String[] data = { "80,44,F", "90,44,T", "99,44,F", "115,44,T",
				"135,44,F", "80,40,T", "80,30,F", "100,30,T", "125,30,F",
				"120,24,F,台湾", "121,24,T,台湾", "122,24,F,台湾", "120,39,F,渤海湾" };	
		PolygonOperator po = new PolygonOperator(pipFile);

		for(String s : data){
			String[] ss = s.split(",");
			double lon = Double.parseDouble(ss[0]);
			double lat = Double.parseDouble(ss[1]);
			boolean b = "T".equals(ss[2]);
			System.err.println(s);
			assertEquals(po.containsPoint(lon, lat), b);
		}
	}

	@Test
	public void testRectangle() throws Exception{
		doTestRectangle(1);
		doTestRectangle(2);
		doTestRectangle(4);
		doTestRectangle(8);
		doTestRectangle(16);
		doTestRectangle(32);
	}
	private void doTestRectangle(int ppd) throws Exception{
		PolygonPreprocessor pp = new PolygonPreprocessor(-3,3,-2,2,ppd);
		String pipFile = String.format("target/rect-%03d.pip",ppd);
		pp.preprocess("MULTIPOLYGON(((-2 -1,-2 1, 2 1, 2 -1, -2 -1)))",pipFile);
		PolygonOperator po = new PolygonOperator(pipFile);

		assertTrue (po.containsPoint(0, 0));
		assertFalse(po.containsPoint(-3, 0));
		assertFalse(po.containsPoint(4, 0));
		assertFalse(po.containsPoint(0, -3));
		assertFalse(po.containsPoint(0, 4));

		//  (-2 -1)
		assertTrue(po.containsPoint(-2, -1));

		assertTrue(po.containsPoint(-2-eps2, -1));
		assertFalse(po.containsPoint(-2-eps1, -1));

		assertTrue(po.containsPoint(-2, -1-eps2));
		assertFalse(po.containsPoint(-2, -1-eps1));

		assertTrue(po.containsPoint(-2-eps2, -1-eps2));
		assertFalse(po.containsPoint(-2-eps1, -1-eps1));

		//  (-2 0)
		assertTrue(po.containsPoint(-2, 0));

		assertTrue(po.containsPoint(-2-eps2, 0));
		assertFalse(po.containsPoint(-2-eps1, 0));

		//  (-2 1)
		assertTrue(po.containsPoint(-2, 1));

		assertTrue(po.containsPoint(-2-eps2, 1));
		assertFalse(po.containsPoint(-2-eps1, 1));

		assertTrue(po.containsPoint(-2, 1+eps2));
		assertFalse(po.containsPoint(-2, 1+eps1));

		assertTrue(po.containsPoint(-2-eps2, 1+eps2));
		assertFalse(po.containsPoint(-2-eps1, 1+eps1));

		//  (0 1)
		assertTrue(po.containsPoint(0, 1));

		assertTrue(po.containsPoint(0, 1+eps2));
		assertFalse(po.containsPoint(0, 1+eps1));

		//  (2 1)
		assertTrue(po.containsPoint(2, 1));

		assertTrue(po.containsPoint(2+eps2, 1));
		assertFalse(po.containsPoint(2+eps1, 1));

		assertTrue(po.containsPoint(2, 1+eps2));
		assertFalse(po.containsPoint(2, 1+eps1));

		assertTrue(po.containsPoint(2+eps2, 1+eps2));
		assertFalse(po.containsPoint(2+eps1, 1+eps1));

		//  (2 0)
		assertTrue(po.containsPoint(2, 0));

		assertFalse(po.containsPoint(2+eps1, 0));
		assertTrue(po.containsPoint(2+eps2, 0));

		//  (2 -1)
		assertTrue(po.containsPoint(2, -1));

		assertFalse(po.containsPoint(2+eps1, -1));
		assertTrue(po.containsPoint(2+eps2, -1));

		assertFalse(po.containsPoint(2, -1-eps1));
		assertTrue(po.containsPoint(2, -1-eps2));

		assertFalse(po.containsPoint(2+eps1, -1-eps1));
		assertTrue(po.containsPoint(2+eps2, -1-eps2));

		//  (0 -1)
		assertTrue(po.containsPoint(0, -1));

		assertFalse(po.containsPoint(0, -1-eps1));
		assertTrue(po.containsPoint(0, -1-eps2));

	}

	// eps2 < tolerance < eps1
	private static double eps1 = 1.1e-8;
	private static double eps2 = 9e-9;

}
