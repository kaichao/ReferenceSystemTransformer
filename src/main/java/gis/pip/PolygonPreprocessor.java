package gis.pip;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * 预处理，读取wkt文件，输出索引文件
 * 索引文件也可供除java以外的其它语言环境调用
 * @author kaichao
 *
 */
public class PolygonPreprocessor {
	private EnvelopeExt env;
	public PolygonPreprocessor(double x0, double x1, double y0, double y1, int ppd) {
		env = new EnvelopeExt(x0,x1,y0,y1,ppd);
	}

	public void preprocess(String wkt, String outputFile){
		try {
			// 可考虑用LittleEndianDataInputStream输出，以在java/C中可方便使用
			DataOutputStream out;
			out = new DataOutputStream(new FileOutputStream(outputFile));
			long[][] borders = env.makeBorderData(wkt);
			env.output(out);
			out.writeInt(borders[0].length);
			for(long[] ls : borders){
				for(long l : ls){
					out.writeLong(l);
				}
			}
			out.close();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String wktFile = "data/chinamap.wkt";
		int ppd = 128;
		PolygonPreprocessor pp = new PolygonPreprocessor(-180,180,-90,90,ppd);

		if(args.length > 0){
			wktFile = args[0];
		} 
		int n = 0;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(wktFile)));
			String line;
			while((line = in.readLine()) != null) {
				String pipFile = String.format("data/chinamap-%03d-%02d.pip",ppd,n);
				pp.preprocess(line.split(";")[1], pipFile);
				n++;
			}
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
