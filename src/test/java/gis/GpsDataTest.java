package gis;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GpsDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception{
		String fileName = "/Volumes/Transcend/gps-sh/sjtu_taxigps20070201.txt";
		RefSysTransformer t = new RefSysTransformer("GCJ-02","WGS-84");

//		Pattern p = Pattern.compile("(\\d*,\\d*,)([^,]+),([^,]+),(.*)");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		new FileInputStream(fileName)));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format("%s.new.txt",fileName))));

		String line;
		int num = 0;
		while((line = in.readLine())!=null){
//			Matcher m = p.matcher(line);
			int index = line.indexOf(",");
			int p1 = line.indexOf(",", index+1);
			int p2 = line.indexOf(",", p1+1);
			int p3 = line.indexOf(",", p2+1);
			String s1 = line.substring(0, p1);
			String s2 = line.substring(p1+1, p2);
			String s3 = line.substring(p2+1, p3);
			String s4 = line.substring(p3+1);

			double lon = Double.parseDouble(s2);
			double lat = Double.parseDouble(s3);
			LonLat pnt0 = new LonLat(lon, lat);
			LonLat pnt1 = t.getTransformed(pnt0);
			String str = String.format("%s,%.5f,%.5f,%s\n",s1,pnt1.lon,pnt1.lat,s4);
			out.append(str);
			num ++;
			if(num % 100000 == 0){
				System.out.println(num);
			}
		}
		
		in.close();
		out.close();
	}

}
