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
	/** 태그 집합 제목 */
	public String title = null;
	
	/** 태그 집합 버전 */
	public String version = null;
	
	/** 태그 집합 저작권 */
	public String copyright = null;
	
	/** 태그 집합 제작자 */
	public String author = null;
	
	/** 태그 집합 제작일 */
	public String date = null;
	
	/** 태그 집합 편집자 */
	public String editor = null;

	/** 시작 태그 */
	public String startTag = null;
	
	/** 연결 불허용 태그 정보 테이블 */
	private int[][] notTagTable = null;
	
	/** 연결 불허용 형태소 정보 테이블 */
	private String[][] notMorphTable = null;
	
	/** 불허용 규칙 수 */
	private int ruleCount = 0;
	
	
	/**
	 * 멤버들을 빈 값으로 초기화한다.
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
		/* 태그 확인 후 형태소 확인 */
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
	 * 연결 불허용 정보 파일로부터 객체를 초기화한다.
	 * @param filePath	연결 불허용 정보 파일
	 * @param tagSet	태그 집합 객체
	 * @throws IOException
	 */
	public void init(String filePath, TagSet tagSet) throws IOException {
		readFile(filePath, tagSet);
	}
	
	/**
	 * 연결 불허용 정보 파일로부터 객체를 초기화한다.
	 * @param filePath	연결 불허용 정보 파일
	 * @param tagSet	태그 집합 객체
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