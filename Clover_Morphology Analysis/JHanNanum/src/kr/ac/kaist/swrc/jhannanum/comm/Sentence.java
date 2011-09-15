/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.comm;

/**
 * �÷����ΰ��� ��� ��ü�� ���¼� �м��� �ϳ��� ������ �ٷ��.
 * ������ ���¼� �м��� �������� �������� �����ȴ�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class Sentence extends CommObject {
	public int length = 0;
	private String[] plainEojeols = null;
	private Eojeol[] eojeols = null;
	
	public Sentence(int documentID, int sentenceID, boolean endOfDocument) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
	}
	
	public Sentence(int documentID, int sentenceID, boolean endOfDocument, String[] plainEojeols, Eojeol[] eojeols) {
		super.setDocumentID(documentID);
		super.setSentenceID(sentenceID);
		super.setEndOfDocument(endOfDocument);
		
		this.eojeols = eojeols;
		this.plainEojeols = plainEojeols;
		
		if (eojeols != null && plainEojeols != null) {
			if (plainEojeols.length <= eojeols.length) {
				length = eojeols.length;
			} else {
				length = plainEojeols.length;
			}
		} else {
			length = 0;
		}
	}

	public Eojeol[] getEojeols() {
		return eojeols;
	}
	
	public Eojeol getEojeol(int index) {
		return eojeols[index];
	}

	public void setEojeols(Eojeol[] eojeols) {
		this.eojeols = eojeols;
		this.length = eojeols.length;
	}
	
	public void setEojeol(int index, Eojeol eojeol) {
		eojeols[index] = eojeol;
	}
	
	public void setEojeol(int index, String[] morphemes, String[] tags) {
		Eojeol eojeol = new Eojeol(morphemes, tags);
		eojeols[index] = eojeol;
	}
	
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			str += plainEojeols[i] + "\n";
			str += "\t" + eojeols[i].toString() + "\n\n";
		}
		return str;
	}
	
	public String[] getPlainEojeols() {
		return plainEojeols;
	}

	public void setPlainEojeols(String[] plainEojeols) {
		this.plainEojeols = plainEojeols;
	}
}