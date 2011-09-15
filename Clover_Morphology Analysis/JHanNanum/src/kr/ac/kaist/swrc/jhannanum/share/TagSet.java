package kr.ac.kaist.swrc.jhannanum.share;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * �±� ������ �ٷ��.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class TagSet {
	final public static int TAG_SET_KAIST = 0;
	
	final public static int TAG_TYPE_ALL = 0;
	final public static int TAG_TYPE_VERBS = 1;
	final public static int TAG_TYPE_NOUNS = 2;
	final public static int TAG_TYPE_NPS = 3;
	final public static int TAG_TYPE_ADJS = 4;
	final public static int TAG_TYPE_NBNP = 5;
	final public static int TAG_TYPE_JOSA = 6;
	final public static int TAG_TYPE_YONGS = 7;
	final public static int TAG_TYPE_EOMIES = 8;
	final public static int TAG_TYPE_JP = 9;
	final public static int TAG_TYPE_COUNT = 10;
	
	final public static int PHONEME_TYPE_ALL = 0;

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

	/** �±� ���� */
	private ArrayList<String> tagList = null;
	
	/** �ұ�Ģ ���� */
	private ArrayList<String> irregularList = null;
	
	/** �±� ���� ���� */
	private HashMap<String, int[]> tagSetMap = null;
	
	/** �±� Ÿ�� ���� */
	private int[][] tagTypeTable = null;
	
	public int[] indexTags = null;
	public int[] unkTags = null;
	public int iwgTag = 0; 
	public int unkTag = 0;
	public int numTag = 0;
	

	public int IRR_TYPE_B;
	public int IRR_TYPE_S;
	public int IRR_TYPE_D;
	public int IRR_TYPE_H;
	public int IRR_TYPE_REU;
	public int IRR_TYPE_REO;

	/**
	 * ������� �� ������ �ʱ�ȭ�Ѵ�.
	 */
	public TagSet() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList = new ArrayList<String>();
		irregularList = new ArrayList<String>();
		tagSetMap = new HashMap<String, int[]>();
		tagTypeTable = new int[TAG_TYPE_COUNT][];
	}
	
	/**
	 * ������ Ÿ���� Ȯ���Ѵ�.
	 * @param phonemeType	���� Ÿ��
	 * @param phoneme	����
	 * @return	true: ���Ұ� �ش� Ÿ�Կ� ����, false: ������ ����
	 */
	public boolean checkPhonemeType(int phonemeType, int phoneme) {
		if (phonemeType == PHONEME_TYPE_ALL) {
			return true;
		}
		return phonemeType == phoneme;
	}
	
	/**
	 * �±��� Ÿ���� Ȯ���Ѵ�.
	 * @param tagType	�±� Ÿ��
	 * @param tag	�±�
	 * @return	true: �±װ� �ش� Ÿ�Կ� ����, false: ������ ����
	 */
	public boolean checkTagType(int tagType, int tag) {
		if (tagType == TAG_TYPE_ALL) {
			return true;
		}
		for (int i = 0; i < tagTypeTable[tagType].length; i++) {
			if (tagTypeTable[tagType][i] == tag) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �ε��� �����͸� �����.
	 */
	public void clear() {
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList.clear();
		irregularList.clear();
		tagSetMap.clear();
	}
	
	/**
	 * �ұ�Ģ ��ȣ�� ��ȯ�Ѵ�.
	 * @param irregular	�ұ�Ģ �̸�
	 * @return	�ұ�Ģ ��ȣ
	 */
	public int getIrregularID(String irregular) {
		return irregularList.indexOf(irregular);
	}
	
	/**
	 * �ұ�Ģ �ĺ��ڿ� �ش��ϴ� �ұ�Ģ �̸��� ��ȯ�Ѵ�.
	 * @param irregularID	�ұ�Ģ �ĺ���
	 * @return	�ұ�Ģ �̸�
	 */
	public String getIrregularName(int irregularID) {
		return irregularList.get(irregularID);
	}
	
	/**
	 * �ε�� �±��� ���� ��ȯ�Ѵ�.
	 * @return	�ε�� �±� ��
	 */
	public int getTagCount() {
		return tagList.size();
	}
	
	/**
	 * �±� ��ȣ�� ��ȯ�Ѵ�.
	 * @param tag	�±� �̸�
	 * @return	�±� ��ȣ, �߸��� �±��� ��� -1
	 */
	public int getTagID(String tag) {
		return tagList.indexOf(tag);
	}

	/**
	 * �±� �ĺ��ڿ� �ش��ϴ� �±� �̸��� ��ȯ�Ѵ�.
	 * @param tagID	�±� �ĺ���
	 * @return	�±� �̸�
	 */
	public String getTagName(int tagID) {
		return tagList.get(tagID);
	}
	
	/**
	 * �±� ���տ� ���ϴ� �±׵��� ��ȯ�Ѵ�.
	 * @param tagSetName	�±� ���� �̸�
	 * @return	�±� ��ȣ ����Ʈ
	 */
	public int[] getTags(String tagSetName) {
		return tagSetMap.get(tagSetName);
	}
	
	/**
	 * �±� ���� ���Ϸκ��� ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param filePath	�±� ���� ����
	 * @throws IOException
	 */
	public void init(String filePath, int tagSetFlag) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String line = null;
		
		title = "";
		version = "";
		copyright = "";
		author = "";
		date = "";
		editor = "";
		tagList.clear();
		irregularList.clear();
		tagSetMap.clear();
		
		ArrayList<Integer> tempTagNumbers = new ArrayList<Integer>();
		
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
			} else if ("TAG".equals(lineToken)) {
				tagList.add(lineTokenizer.nextToken());
			} else if ("TSET".equals(lineToken)) {
				String tagSetName = lineTokenizer.nextToken();
				StringTokenizer tagTokenizer = new StringTokenizer(lineTokenizer.nextToken(), " ");
				
				while (tagTokenizer.hasMoreTokens()) {
					String tagToken = tagTokenizer.nextToken();
					int tagNumber = tagList.indexOf(tagToken);
					
					if (tagNumber != -1) {
						tempTagNumbers.add(tagNumber);
					} else {
						int[] values = tagSetMap.get(tagToken);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								tempTagNumbers.add(values[i]);
							}
						}
					}
				}
				int[] tagNumbers = new int[tempTagNumbers.size()];
				Iterator<Integer> iter = tempTagNumbers.iterator();
				for (int i = 0; iter.hasNext(); i++) {
					tagNumbers[i] = iter.next();
				}
				tagSetMap.put(tagSetName, tagNumbers);
				tempTagNumbers.clear();
				
			} else if ("IRR".equals(lineToken)) {
				irregularList.add(lineTokenizer.nextToken());
			}
		}
		br.close();
		
		setTagTypes(tagSetFlag);
		indexTags = tagSetMap.get("index");
		unkTags = tagSetMap.get("unkset");
		iwgTag = tagList.indexOf("iwg");
		unkTag = tagList.indexOf("unk");
		numTag = tagList.indexOf("nnc");
		
		IRR_TYPE_B=getIrregularID("irrb");
		IRR_TYPE_S=getIrregularID("irrs");
		IRR_TYPE_D=getIrregularID("irrd");
		IRR_TYPE_H=getIrregularID("irrh");
		IRR_TYPE_REU=getIrregularID("irrlu");
		IRR_TYPE_REO=getIrregularID("irrle");
	}

	public void setTagTypes(int tagSetFlag) {
		if (tagSetFlag == TAG_SET_KAIST) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			int[] values = null;
			Iterator<Integer> iter = null;

			// verb
			values = tagSetMap.get("pv");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsm");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			list.add(tagList.indexOf("px"));
			tagTypeTable[TAG_TYPE_VERBS] = new int[list.size()];
			iter = list.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				tagTypeTable[TAG_TYPE_VERBS][i] = iter.next(); 
			}
			list.clear();

			// noun
			tagTypeTable[TAG_TYPE_NOUNS] = tagSetMap.get("n");
			
			// nps
			tagTypeTable[TAG_TYPE_NPS] = tagSetMap.get("np");
			
			// adjs
			tagTypeTable[TAG_TYPE_ADJS] = tagSetMap.get("pa");
			
			// eomies
			tagTypeTable[TAG_TYPE_EOMIES] = tagSetMap.get("e");
			
			// yongs
			values = tagSetMap.get("p");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsv");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			values = tagSetMap.get("xsm");
			for (int i = 0; i < values.length; i++) {
				list.add(values[i]);
			}
			list.add(tagList.indexOf("ep"));
			list.add(tagList.indexOf("jp"));
			
			tagTypeTable[TAG_TYPE_YONGS] = new int[list.size()];
			iter = list.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				tagTypeTable[TAG_TYPE_YONGS][i] = iter.next(); 
			}
			list.clear();
			
			// jp
			tagTypeTable[TAG_TYPE_JP] = new int[1];
			tagTypeTable[TAG_TYPE_JP][0] = tagList.indexOf("jp");
			
			// nbnp
			tagTypeTable[TAG_TYPE_NBNP] = new int[3];
			tagTypeTable[TAG_TYPE_NBNP][0] = tagList.indexOf("nbn");
			tagTypeTable[TAG_TYPE_NBNP][1] = tagList.indexOf("npd");
			tagTypeTable[TAG_TYPE_NBNP][2] = tagList.indexOf("npp");
			
			// josa
			tagTypeTable[TAG_TYPE_JOSA] = new int[6];
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jxc");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jco");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jca");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcm");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcs");
			tagTypeTable[TAG_TYPE_JOSA][0] = tagList.indexOf("jcc");
		}
	}
}
