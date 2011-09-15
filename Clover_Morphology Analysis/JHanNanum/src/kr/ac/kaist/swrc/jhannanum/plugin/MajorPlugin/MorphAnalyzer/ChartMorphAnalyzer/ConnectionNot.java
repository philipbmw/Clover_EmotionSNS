package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class ConnectionNot {
	/** �±� ���� ���� */
	public String title = null;
	
	/** �±� ���� ���� */
	public String version = null;
	
	/** �±� ���� ���۱� */
	public String copyright = null;
	
	/** �±� ���� ������ */
	public String author = null;
	
	/** �±� ���� ������ */
	public String date = null;
	
	/** �±� ���� ������ */
	public String editor = null;

	/** ���� �±� */
	public String startTag = null;
	
	/** ���� ����� �±� ���� ���̺� */
	private int[][] notTagTable = null;
	
	/** ���� ����� ���¼� ���� ���̺� */
	private String[][] notMorphTable = null;
	
	/** ����� ��Ģ �� */
	private int ruleCount = 0;
	
	
	/**
	 * ������� �� ������ �ʱ�ȭ�Ѵ�.
	 */
	public ConnectionNot() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
	}
	
	public boolean checkConnection() {
		/* �±� Ȯ�� �� ���¼� Ȯ�� */
		return true;
	}
	
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		ruleCount = 0;
		notTagTable = null;
		notMorphTable = null;
	}
	
	/**
	 * ���� ����� ���� ���Ϸκ��� ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param filePath	���� ����� ���� ����
	 * @param tagSet	�±� ���� ��ü
	 * @throws IOException
	 */
	public void init(String filePath, TagSet tagSet) throws IOException {
		readFile(filePath, tagSet);
	}
	
	/**
	 * ���� ����� ���� ���Ϸκ��� ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param filePath	���� ����� ���� ����
	 * @param tagSet	�±� ���� ��ü
	 * @throws IOException
	 */
	private void readFile(String filePath, TagSet tagSet) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		ArrayList<String> ruleList = new ArrayList<String>();

		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		ruleCount = 0;
		
		while ((line = br.readLine()) != null) {
			StringTokenizer lineTokenizer = new StringTokenizer(line, "\t");
			if (lineTokenizer.hasMoreElements() == false) {
				continue;
			}
			
			String lineToken = lineTokenizer.nextToken();
			
			if (lineToken.startsWith("@")) {
				if ("@title".equals(lineToken)) {
					title = lineTokenizer.nextToken();
				} else if ("@version".equals(lineToken)) {
					version = lineTokenizer.nextToken();
				} else if ("@copyright".equals(lineToken)) {
					copyright = lineTokenizer.nextToken();
				} else if ("@author".equals(lineToken)) {
					author = lineTokenizer.nextToken();
				} else if ("@date".equals(lineToken)) {
					date = lineTokenizer.nextToken();
				} else if ("@editor".equals(lineToken)) {
					editor = lineTokenizer.nextToken();
				}
			} else if ("CONNECTION_NOT".equals(lineToken)) {
				ruleList.add(lineTokenizer.nextToken());
			}
		}
		
		ruleCount = ruleList.size();
		
		notTagTable = new int[ruleCount][2];
		notMorphTable = new String[ruleCount][2];

		Iterator<String> iter = ruleList.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			String rule = iter.next();
			StringTokenizer st = new StringTokenizer(rule, " ");
			notMorphTable[i][0] = st.nextToken();
			notTagTable[i][0] = tagSet.getTagID(st.nextToken());
			notMorphTable[i][1] = st.nextToken();
			notTagTable[i][1] = tagSet.getTagID(st.nextToken());
		}
		
		ruleList.clear();
		br.close();
	}
}