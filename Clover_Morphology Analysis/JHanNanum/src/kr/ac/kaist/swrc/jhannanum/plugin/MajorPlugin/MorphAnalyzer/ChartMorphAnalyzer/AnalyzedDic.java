package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * ���¼� ��� ����. ���������� Hashtable�� �̿��Ѵ�.
 * @author Sangwon
 *
 */
public class AnalyzedDic {
	private Hashtable<String, String> dictionary;
	
	/**
	 * ������.
	 */
	public AnalyzedDic() {
		dictionary = new Hashtable<String, String>();
	}

	/**
	 * ���� ���Ϸκ��� �����͸� �о Hashtable�� �����Ѵ�. ���� ������ ������ ������ ����. "������\t����\n" 
	 * @param dictionaryFileName	���� ���� �̸�
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AnalyzedDic(String dictionaryFileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		dictionary = new Hashtable<String, String>();
		
		this.readDic(dictionaryFileName);
	}

	/**
	 * �ε��� �����͸� �����Ѵ�.
	 */
	public void clear() {
		dictionary.clear();
	}
	
	/**
	 * ������ ��ϵǾ� �ִ� item�� ���� ������ ��ȯ�Ѵ�.
	 * @param item	�˻� item
	 * @return	item�� �����ϴ� ����
	 */
	public String get(String item) {
		return dictionary.get(item);
	}
	
	/**
	 * ���� ���Ϸκ��� �����͸� �о Hashtable�� �����Ѵ�. ���� ������ ������ ������ ����. "������\t����\n" 
	 * @param dictionaryFileName	���� ���� �̸�
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readDic(String dictionaryFileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		dictionary.clear();
		String str = "";
		
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName), "euc-kr"));

		while ((str = in.readLine()) != null) {
			str.trim();
			if (str.equals("")) {
				continue;
			}
			
			StringTokenizer tok	= new StringTokenizer(str, "\t");
			String key = tok.nextToken();
			String value = "";
			while (tok.hasMoreTokens()) {
				value += tok.nextToken() + "\n";
			}
			dictionary.put(key, value.trim());
			
			// ���� �ε� �׽�Ʈ
			// System.out.println("key:"+key+ " value:"+value);
		}
	}
}
