/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.comm;

import java.util.ArrayList;

/**
 * 플러그인과의 통신 객체로 형태소 분석 결과 생성된 문장들의 집합이다.
 * 각 문장은 품사 태깅된 어절들의 집합이다.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class SetOfSentences extends CommObject {
	public int length = 0;
	private ArrayList<Eojeol[]> eojeolSetArray = null;
	private ArrayList<String> plainEojeolArray = null;
	
	public SetOfSentences(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		eojeolSetArray = new ArrayList<Eojeol[]>();
		plainEojeolArray = new ArrayList<String>();
	}
	
	public SetOfSentences(int documentID, int sentenceID, boolean endOfDocument, ArrayList<String> plainEojeolArray, ArrayList<Eojeol[]> eojeolSetArray) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		if (eojeolSetArray != null) {
			length = eojeolSetArray.size();
		}
		this.plainEojeolArray = plainEojeolArray;
		this.eojeolSetArray = eojeolSetArray;
	}
	
	public ArrayList<String> getPlainEojeolArray() {
		return plainEojeolArray;
	}

	public void setPlainEojeolArray(ArrayList<String> plainEojeolArray) {
		this.plainEojeolArray = plainEojeolArray;
	}

	public boolean addPlainEojeol(String eojeol) {
		return plainEojeolArray.add(eojeol);
	}

	public boolean addEojeolSet(Eojeol[] eojeols) {
		return eojeolSetArray.add(eojeols);
	}

	public ArrayList<Eojeol[]> getEojeolSetArray() {
		return eojeolSetArray;
	}

	public void setEojeolSetArray(ArrayList<Eojeol[]> eojeolSetArray) {
		this.eojeolSetArray = eojeolSetArray;
	}
	
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			str += plainEojeolArray.get(i) + "\n";
			Eojeol[] eojeolArray = eojeolSetArray.get(i);
			for (int j = 0; j < eojeolArray.length; j++) {
				str += "\t" + eojeolArray[j] + "\n";
			}
			str += "\n";
		}
		return str;
	}
}