package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class ProbabilityDBM {
	private Hashtable<String,double[]> table = null;
	
//	public static void main(String[] args) {
//		ProbabilityDBM pdbm = new ProbabilityDBM();
//		
//		try {
//			pdbm.init("data/Stat/PWT.pos");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public ProbabilityDBM(String fileName) throws IOException {
		table = new Hashtable<String,double[]>();
		init(fileName);
	}
	
	public void clear() {
		table.clear();
	}
	
	public double[] get(String key) {
		return table.get(key);
	}
	
	private void init(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		String[] tokens = null;
		double[] numbers = null;
		
		while ((line = br.readLine()) != null) {
			tokens = line.split(" ");
			
			numbers = new double[tokens.length - 1];
			
			for (int i = 0; i < tokens.length - 1; i++) {
				numbers[i] = Double.parseDouble(tokens[i + 1]);
			}
			
			if (tokens == null || tokens[0] == null || numbers == null ) {
				System.out.println("hi");
			}
			
			table.put(tokens[0], numbers);
		}
		br.close();
	}
}
