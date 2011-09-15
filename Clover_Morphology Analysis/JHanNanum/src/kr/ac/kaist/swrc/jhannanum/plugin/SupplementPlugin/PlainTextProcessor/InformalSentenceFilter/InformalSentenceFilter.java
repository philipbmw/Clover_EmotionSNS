/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class InformalSentenceFilter implements PlainTextProcessor {
	final static private int REPEAT_CHAR_ALLOW = 5;

	@Override
	public PlainSentence doProcess(PlainSentence ps) {
		String word = null;
		StringBuffer buf = new StringBuffer();
		StringTokenizer st = new StringTokenizer(ps.getSentence(), " \t");

		while (st.hasMoreTokens()) {
			word = st.nextToken();

			/* 반복되는 특수기호 또는 1글자 문자 처리 */
			if (word.length() > REPEAT_CHAR_ALLOW) {
				char[] wordArray = word.toCharArray();
				int repeatCnt = 0;
				char checkChar = wordArray[0];

				buf.append(checkChar);

				for (int i = 1; i < wordArray.length; i++) {
					if (checkChar == wordArray[i]) {
						if (repeatCnt == REPEAT_CHAR_ALLOW - 1) {
							buf.append(' ');
							buf.append(wordArray[i]);
							repeatCnt = 0;
						} else {
							buf.append(wordArray[i]);
							repeatCnt++;
						}
					} else {
						if (checkChar == '.') {
							buf.append(' ');
						}
						buf.append(wordArray[i]);
						checkChar = wordArray[i];
						repeatCnt = 0;
					}
				}
			} else {
				buf.append(word);
			}
			buf.append(' ');
		}
		ps.setSentence(buf.toString());
		return ps;
	}


	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {

	}


	@Override
	public PlainSentence flush() {
		return null;
	}


	@Override
	public void shutdown() {

	}

	@Override
	public boolean hasRemainingData() {
		return false;
	}
}

