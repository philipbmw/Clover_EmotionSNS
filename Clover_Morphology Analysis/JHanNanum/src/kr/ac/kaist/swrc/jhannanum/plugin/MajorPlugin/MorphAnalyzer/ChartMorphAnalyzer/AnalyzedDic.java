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
 * 형태소 기반 사전. 내부적으로 Hashtable을 이용한다.
 * @author Sangwon
 *
 */
public class AnalyzedDic {
	private Hashtable<String, String> dictionary;
	
	/**
	 * 생성자.
	 */
	public AnalyzedDic() {
		dictionary = new Hashtable<String, String>();
	}

	/**
	 * 사전 파일로부터 데이터를 읽어서 Hashtable을 구성한다. 사전 파일의 구성은 다음과 같다. "아이템\t내용\n" 
	 * @param dictionaryFileName	사전 파일 이름
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AnalyzedDic(String dictionaryFileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		dictionary = new Hashtable<String, String>();
		
		this.readDic(dictionaryFileName);
	}

	/**
	 * 로드한 데이터를 해제한다.
	 */
	public void clear() {
		dictionary.clear();
	}
	
	/**
	 * 사전에 등록되어 있는 item에 대한 내용을 반환한다.
	 * @param item	검색 item
	 * @return	item에 대응하는 내용
	 */
	public String get(String item) {
		return dictionary.get(item);
	}
	
	/**
	 * 사전 파일로부터 데이터를 읽어서 Hashtable을 구성한다. 사전 파일의 구성은 다음과 같다. "아이템\t내용\n" 
	 * @param dictionaryFileName	사전 파일 이름
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
			
			// 사전 로드 테스트
			// System.out.println("key:"+key+ " value:"+value);
		}
	}
}
