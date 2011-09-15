/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.comm;

/**
 * �÷����ΰ��� ��� ��ü.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class CommObject {
	private int documentID = 0;
	private int sentenceID = 0;
	private boolean endOfDocument = false;
	
	public boolean isEndOfDocument() {
		return endOfDocument;
	}

	public void setEndOfDocument(boolean endOfDocument) {
		this.endOfDocument = endOfDocument;
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public int getSentenceID() {
		return sentenceID;
	}

	public void setSentenceID(int sentenceID) {
		this.sentenceID = sentenceID;
	}
}
