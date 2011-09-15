/**
 * ���� ������ ���� ��ó����.
 * ���¼� �м��Ⱑ �߰��Ϸ��� ��ó����� �ʼ����̴�. ������ �����ϴ� online ������ ���� �ٷ�鼭 ���¼� �м��Ⱑ �������� ����
 * ������ ó���ؾ� �Ǵ� ��찡 ����. �̰�� ��ó���⿡�� �Է� ������ ���� ó�����ָ� ���¼� �м��⿡�� �� ���� �ùٸ� ����� ����
 * �� �ִ�. ��ó���⿡�� �ַ� �ϴ� ���� ���屸���̴�. ��, �Է� ������ ��������� ������ ���̴�. �������� ���屸�п� ���ؼ���
 * �ַ� ����ǿ��� �����Ǿ���. Ư�� ������� ��ħǥ(.)�� ������ ���� �� ���Ǵ°� ��� ���� �ٸ� �ǹ̷� ���Ǵ°��� ���� ������
 * ����. ���⿡���� ���屸���� �� �� �ִ� Ư����ȣ�� �¿� ���ڿ��� ������ ���� ������ �ϴ� ������ ��ó���⸦ �����Ѵ�.
 * 
 * ũ�� ���屸���� �� �� �ִ� Ư����ȣ�� ����ǥ(?), ����ǥ(!), ��ħǥ(.)�̴�. ������ ���� ó���� �Ʒ��� ����.
 * 
 * ����ǥ�� ����ǥ�� ��Ÿ���� ��
 * 	- Ư�����ڿ� �پ ������ ���� ��쿡 ���屸���� �Ѵ�. ���⿡�� ���ϴ� Ư�����ڴ� �ƽ�Ű�ڵ尪�� ������ ���ڸ� ���Ѵ�.
 * 
 * ��ħǥ�� ��Ÿ���� ��
 * - �ڿ� ���ڿ� �پ ���� ���� �����Ѵ�.
 * - ��ħǥ �ٷ� ���� �����̸� �����Ѵ�. ���⿡�� ����������� �̿��Ͽ� ����� ��쿡�� �����ϸ� �� ��Ȯ�� ����� ���� �� ���� ���̴�.
 * - �����ʿ� ��ħǥ�� ������ �����Ѵ�. �̰��� ������ǥ�� ���� ó���̴�.
 * - ��ħǥ ���� �ٷ� ���� �ܾ��� ���̰� 2byte�����̸� ��ħǥ �� �ܾ�� ���屸���� �ϰ�, ������ �������� �� �ٸ� �� �������� ó���Ѵ�. �̰��� '��.', '��.' ��� ���� ���Ӹ�ǥ�� ���� ó���̴�.
 * - �� �̿ܿ��� ���屸���� �Ѵ�.
 * 
 * ���� ��� ��쿡 ���ؼ� ��ȣ�� ����ǥ�� ���� ó���� �ʿ��ϴ�. ���� ��Ģ�� �����Ͽ� ���屸���� �� �� ��ȣ�� ����ǥ ���� �������� �ʵ��� �Ѵ�.
 */

package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor;

import java.io.FileNotFoundException;
import java.io.IOException;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * @author Sangwon Park
 *
 */
public class SentenceSegmentor implements PlainTextProcessor {
	private int documentID = 0;
	private int sentenceID = 0;
	private boolean hasRemainingData = false;
	private String bufRes = null;
	private String[] bufEojeols = null;
	private int bufEojeolsIdx = 0;
	private boolean endOfDocument = false;

	/**
	 * �� ���忡 ���� ��ȣ�� �˻��Ѵ�.
	 * @param c Ȯ���� ����
	 * @return 
	 */
	private boolean isSym(char c) {
		switch (c) {
		case ')': return true;
		case ']': return true;
		case '}': return true;
		case '?': return true;
		case '!': return true;
		case '.': return true;
		case '\'': return true;
		case '\"': return true;
		}
		return false;
	}

	@Override
	public PlainSentence doProcess(PlainSentence ps) {
		String[] eojeols = null;
		String res = null;
		boolean isFirstEojeol = true;
		boolean isEOS = false;
		int i = 0;
		int j = 0;

		if (bufEojeols != null) {
			eojeols = bufEojeols;
			i = bufEojeolsIdx;

			bufEojeols = null;
			bufEojeolsIdx = 0;
		} else {
			if (ps == null) {
				return null;
			}

			if (documentID != ps.getDocumentID()) {
				documentID = ps.getDocumentID();
				sentenceID = 0;
			}

			String str = null;
			if ((str = ps.getSentence()) == null) {
				return null;
			}
			eojeols = str.split("\\s");

			endOfDocument = ps.isEndOfDocument();
		}

		for ( ; isEOS == false && i < eojeols.length; i++) {
			if (!eojeols[i].matches(".*(\\.|\\!|\\?).*")) {
				// ���� �� . ! ? �� ���� ���
				if (isFirstEojeol) {
					res = eojeols[i];
					isFirstEojeol = false;
				} else {
					res += " " + eojeols[i];
				}
			} else {
				// ���� �� . ! ? �� �ִ� ��� ���� �ϳ��� Ȯ���ϸ� ó��
				char[] ca = eojeols[i].toCharArray();

				for (j = 0 ; isEOS == false && j < ca.length; j++) {
					switch (ca[j]) {
					case '.':
						if (j == 1) {
							// ���Ӹ�ǥ�� �ν�
							continue;
						}
						if (j > 0) {
							// ��ħǥ ���� ������ ��� ����
							if (Character.isLowerCase(ca[j-1]) || Character.isUpperCase(ca[j-1]))  {
								continue;
							}
						}
						if (j < ca.length - 1) {
							// ��ħǥ �ڿ� ���ڰ� ������ ���� 
							if (Character.isDigit(ca[j+1])) {
								continue;
							}
						}
						isEOS = true;
						break;
					case '!':
						isEOS = true;
						break;
					case '?':
						isEOS = true;
						break;
					}

					if (isEOS) {
						if (isFirstEojeol) {
							res = eojeols[i].substring(0, j) + " " + ca[j]; 
							isFirstEojeol = false;
						} else {
							res += " " + eojeols[i].substring(0, j) + " " + ca[j];
						}

						// ��ħǥ(. ? !) ���� ���������� ������ Ư�� ���ڸ� ���忡 ���Խ�Ŵ
						while (j < ca.length - 1) {
							if (isSym(ca[j+1])) {
								j++;
								res += ca[j];
							} else {
								break;
							}
						}
					}
				}
				if (isEOS == false) {
					if (isFirstEojeol) {
						res = eojeols[i]; 
						isFirstEojeol = false;
					} else {
						res += " " + eojeols[i];
					}
				}
			}
		}

		i--;
		j--;

		if (isEOS) {
			// ������ ������ �κ��� ������ ���ۿ� ���
			if (j + 1 < eojeols[i].length()) {
				eojeols[i] = eojeols[i].substring(j + 1);
				bufEojeols = eojeols;
				bufEojeolsIdx = i;
				hasRemainingData = true;
			} else {
				if (i == eojeols.length - 1) {
					// ��� ������ ó���� ���
					hasRemainingData = false;
				} else {
					// ó������ ���� �������� �ִ� ��� ���ۿ� ���
					bufEojeols = eojeols;
					bufEojeolsIdx = i + 1;
					hasRemainingData = true;
				}
			}

			if (bufRes == null) {
				return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
			} else {
				res = bufRes + " " + res;
				bufRes = null;
				return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
			}
		} else {
			if (res != null && res.length() > 0) {
				bufRes = res;
			}
			hasRemainingData = false;
			return null;
		}
	}

	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {

	}

	@Override
	public void shutdown() {
	}

	@Override
	public PlainSentence flush() {
		if (bufRes != null) {
			String res = bufRes;
			bufRes = null;
			hasRemainingData = false;
			return new PlainSentence(documentID, sentenceID++, !hasRemainingData && endOfDocument, res);
		} else {
			return null;
		}
	}

	@Override
	public boolean hasRemainingData() {
		return hasRemainingData;
	}
}