/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.comm;

/**
 * �÷����ΰ��� ��� ��ü�� ���¼� �м� ������ ������ ��Ÿ����.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class PlainSentence extends CommObject {
	private String sentence = null;
	
	public PlainSentence(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
	}
	
	public PlainSentence(int documentID, int sentenceID, boolean endOfDocument, String sentence) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		this.sentence = sentence;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String toString() {
		if (sentence == null) {
			return "";
		}
		return sentence;
	}
}
