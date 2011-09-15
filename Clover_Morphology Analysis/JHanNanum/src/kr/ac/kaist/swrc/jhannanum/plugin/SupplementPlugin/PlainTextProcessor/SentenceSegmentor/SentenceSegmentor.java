/**
 * 문장 구분을 위한 전처리기.
 * 형태소 분석기가 견고하려면 전처리기는 필수적이다. 요즈음 급증하는 online 문서를 많이 다루면서 형태소 분석기가 깨끗하지 못한
 * 문서를 처리해야 되는 경우가 많다. 이경우 전처리기에서 입력 문서를 먼저 처리해주면 형태소 분석기에서 더 쉽게 올바른 결과를 얻을
 * 수 있다. 전처리기에서 주로 하는 일은 문장구분이다. 즉, 입력 문서를 문장단위로 나누는 일이다. 이제까지 문장구분에 대해서는
 * 주로 영어권에서 연구되었다. 특히 영어에서는 마침표(.)가 문장이 끝날 때 사용되는가 약어 등의 다른 의미로 사용되는가에 대한 연구가
 * 많다. 여기에서는 문장구분이 될 수 있는 특수기호의 좌우 문자열을 가지고서 문장 구분을 하는 간단한 전처리기를 구현한다.
 * 
 * 크게 문장구분이 될 수 있는 특수기호는 물음표(?), 느낌표(!), 마침표(.)이다. 각각에 대한 처리는 아래와 같다.
 * 
 * 물음표나 느낌표가 나타났을 때
 * 	- 특수문자와 붙어서 사용되지 않은 경우에 문장구분을 한다. 여기에서 말하는 특수문자는 아스키코드값을 가지는 문자를 말한다.
 * 
 * 마침표가 나타났을 때
 * - 뒤에 숫자와 붙어서 사용된 경우는 무시한다.
 * - 마침표 바로 전이 영문이면 무시한다. 여기에서 영어약어사전을 이용하여 약어인 경우에만 무시하면 더 정확한 결과를 얻을 수 있을 것이다.
 * - 오른쪽에 마침표가 나오면 무시한다. 이것은 말줄임표에 대한 처리이다.
 * - 마침표 앞의 바로 앞의 단어의 길이가 2byte이하이면 마침표 앞 단어에서 문장구분을 하고, 그줄의 나머지를 또 다른 한 문장으로 처리한다. 이것은 '가.', '나.' 등과 같은 말머리표에 대한 처리이다.
 * - 그 이외에는 문장구분을 한다.
 * 
 * 위의 모든 경우에 대해서 괄호나 따옴표에 대한 처리가 필요하다. 위의 규칙을 적용하여 문장구분을 할 때 괄호나 따옴표 쌍이 끊어지지 않도록 한다.
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
	 * 앞 문장에 붙일 부호를 검사한다.
	 * @param c 확인할 문자
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
				// 어절 중 . ! ? 가 없는 경우
				if (isFirstEojeol) {
					res = eojeols[i];
					isFirstEojeol = false;
				} else {
					res += " " + eojeols[i];
				}
			} else {
				// 어절 중 . ! ? 가 있는 경우 문자 하나씩 확인하며 처리
				char[] ca = eojeols[i].toCharArray();

				for (j = 0 ; isEOS == false && j < ca.length; j++) {
					switch (ca[j]) {
					case '.':
						if (j == 1) {
							// 말머리표로 인식
							continue;
						}
						if (j > 0) {
							// 마침표 앞이 영문인 경우 무시
							if (Character.isLowerCase(ca[j-1]) || Character.isUpperCase(ca[j-1]))  {
								continue;
							}
						}
						if (j < ca.length - 1) {
							// 마침표 뒤에 숫자가 있으면 무시 
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

						// 마침표(. ? !) 이후 연속적으로 나오는 특수 문자를 문장에 포함시킴
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
			// 어절의 나머지 부분이 있으면 버퍼에 등록
			if (j + 1 < eojeols[i].length()) {
				eojeols[i] = eojeols[i].substring(j + 1);
				bufEojeols = eojeols;
				bufEojeolsIdx = i;
				hasRemainingData = true;
			} else {
				if (i == eojeols.length - 1) {
					// 모든 어절을 처리한 경우
					hasRemainingData = false;
				} else {
					// 처리하지 않은 어절들이 있는 경우 버퍼에 등록
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