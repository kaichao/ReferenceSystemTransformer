package gis;

import static org.junit.Assert.*;
import gis.RefSysTransformer;
import gis.LonLat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransfomerTest {

	@Before
	public void setUp() throws Exception {
		t0 = System.currentTimeMillis();
	}
	long t0,t1;
	@After
	public void tearDown() throws Exception {
		t1 = System.currentTimeMillis();
		System.out.format("%d ms\n",t1-t0);
	}
//	@Test
	public void test() {
		LonLat p = new LonLat(116.404, 39.915);
		RefSysTransformer t1,t2,t3,t4;
		t1 = new RefSysTransformer("BD-09","GCJ-02");
		t2 = new RefSysTransformer("GCJ-02","BD-09");
		t3 = new RefSysTransformer("WGS-84","GCJ-02");
		t4 = new RefSysTransformer("GCJ-02","WGS-84");

		System.out.println(t1.getTransformed(p));
		System.out.println(t2.getTransformed(p));
		System.out.println(t3.getTransformed(p));
		System.out.println(t4.getTransformed(p));

		System.out.println(t2.getTransformed(t1.getTransformed(p)).distance(p));
		System.out.println(t1.getTransformed(t2.getTransformed(p)).distance(p));
		System.out.println(t3.getTransformed(t4.getTransformed(p)).distance(p));
		System.out.println(t4.getTransformed(t3.getTransformed(p)).distance(p));
	}
	/*
	 * 测试GCJ-02、WGS-84坐标变换的可逆性
	 */
	@Test
	public void testReversibility_GCJ_WGS() {
		RefSysTransformer t1,t2;
		t1 = new RefSysTransformer("WGS-84","GCJ-02");
		t2 = new RefSysTransformer("GCJ-02","WGS-84");
		LonLat p0,p1,p2;
		double max1 = 0, max2 = 0;

		long tm0,tm1;
		tm0 = System.currentTimeMillis();

		// 选取[116 40, 96 30]之间的10^8个点做测试
		for(int i=0;i<10000;i++){
			for(int j=0;j<10000;j++){
				p0 = new LonLat(116.0-i*0.0002, 40- j*0.0001);
				p1 = t1.getTransformed(t2.getTransformed(p0));
				p2 = t2.getTransformed(t1.getTransformed(p0));
				max1 = Math.max(p0.distance(p1),max1);
				max2 = Math.max(p0.distance(p2),max2);
				assertEquals(p1,p0);
				assertEquals(p2,p0);
			}
		}
		tm1 = System.currentTimeMillis();
		System.out.format("time : %d ms\n",tm1-tm0);
		
		System.out.println("GCJ-02 <--> WGS-84");
		System.out.println(max1);
		System.out.println(max2);
	}

	/*
	 * 测试GCJ-02、BD-09坐标变换的可逆性
	 */
//	@Test
	public void testReversibility_GCJ_BD() {
		RefSysTransformer t1,t2;
		t1 = new RefSysTransformer("GCJ-02","BD-09");
		t2 = new RefSysTransformer("BD-09","GCJ-02");
		LonLat p0,p1,p2;
		double max1 = 0, max2 = 0;
		// 选取[116 40, 96 30]之间的10^8个点做测试
		for(int i=0;i<1000;i++){
			for(int j=0;j<100;j++){
				p0 = new LonLat(116.0-i*0.002, 40- j*0.001);
				p1 = t1.getTransformed(t2.getTransformed(p0));
				p2 = t2.getTransformed(t1.getTransformed(p0));
				max1 = Math.max(p0.distance(p1),max1);
				max2 = Math.max(p0.distance(p2),max2);
				assertEquals(p1,p0);
				assertEquals(p2,p0);
			}
		}

		System.out.println("GCJ-02 <--> BD-09");
		System.out.println(max1);
		System.out.println(max2);
	}
}
