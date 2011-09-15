package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class Connection {
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
	
	/** 연결 정보 테이블 */
	private boolean[][] connectionTable = null;
	
	/**
	 * 멤버들을 빈 값으로 초기화한다.
	 */
	public Connection() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = null;
	}
	
	public boolean checkConnection(TagSet tagSet, int tag1, int tag2, int len1, int len2, int nextTagType) {
		String tag1Name = tagSet.getTagName(tag1);
		String tag2Name = tagSet.getTagName(tag2);
		
		if ((tag1Name.startsWith("nc") || tag1Name.charAt(0) == 'f') &&
				tag2Name.charAt(0) == 'n') {
			if (tag2Name.startsWith("nq")) {
				return false;
			} else if (len1 < 4 || len2 < 2) {
				return false;
			}
		}
		
//		System.err.println(tag1Name + "\t" + tag2Name + ": " + connectionTable[tag1][tag2] + " " + tagSet.checkTagType(nextTagType, tag2));
		return connectionTable[tag1][tag2] && tagSet.checkTagType(nextTagType, tag2);
	}
	
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = null;
	}
	
	/**
	 * 태그 집합 파일로부터 객체를 초기화한다.
	 * @param filePath	태그 집합 파일
	 * @param tagCount	총 태그 갯수
	 * @param tagSet	태그 집합 객체
	 * @throws IOException
	 */
	public void init(String filePath, int tagCount, TagSet tagSet) throws IOException {
		readFile(filePath, tagCount, tagSet);
	}
	
	/**
	 * 태그 집합 파일로부터 객체를 초기화한다.
	 * @param filePath	태그 집합 파일
	 * @param tagCount	총 태그 갯수
	 * @param tagSet	태그 집합 객체
	 * @throws IOException
	 */
	private void readFile(String filePath, int tagCount, TagSet tagSet) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		HashSet<Integer> tagSetA = new HashSet<Integer>();
		HashSet<Integer> tagSetB = new HashSet<Integer>();

		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		startTag = "";
		connectionTable = new boolean[tagCount][tagCount];
		
		for (int i = 0; i < tagCount; i++) {
			for (int j = 0; j < tagCount; j++) {
				connectionTable[i][j] = false;
			}
		}
		
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
			} else if ("CONNECTION".equals(lineToken)) {
				lineToken = lineTokenizer.nextToken();
				String[] tagLists = lineToken.split("\\*", 2);
				
				StringTokenizer tagTokenizer = new StringTokenizer(tagLists[0], ",()");
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					
					StringTokenizer tok = new StringTokenizer(tagToken, "-");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken();
						int[] fullTagIDSet = tagSet.getTags(t);
						
						if (fullTagIDSet != null) {
							for (int i = 0; i < fullTagIDSet.length; i++) {
								tagSetA.add(fullTagIDSet[i]);
							}
						} else {
							tagSetA.add(tagSet.getTagID(t));
						}
						while (tok.hasMoreTokens()) {
							tagSetA.remove(tagSet.getTagID(tok.nextToken()));
						}
					}
				}
				
				tagTokenizer = new StringTokenizer(tagLists[1], ",()");
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					
					StringTokenizer tok = new StringTokenizer(tagToken, "-");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken();
						int[] fullTagIDSet = tagSet.getTags(t);
						
						if (fullTagIDSet != null) {
							for (int i = 0; i < fullTagIDSet.length; i++) {
								tagSetB.add(fullTagIDSet[i]);
							}
						} else {
							tagSetB.add(tagSet.getTagID(t));
						}
						while (tok.hasMoreTokens()) {
							tagSetB.remove(tagSet.getTagID(tok.nextToken()));
						}
					}
				}
				
				Iterator<Integer> iterA = tagSetA.iterator();
				
				while (iterA.hasNext()) {
					int leftSide = iterA.next();
					Iterator<Integer> iterB = tagSetB.iterator();
					
					while (iterB.hasNext()) {
						connectionTable[leftSide][iterB.next()] = true;
					}
				}
				
				tagSetA.clear();
				tagSetB.clear();
			} else if ("START_TAG".equals(lineToken)) {
				startTag = lineTokenizer.nextToken();
			}
		}
		br.close();
	}
}
