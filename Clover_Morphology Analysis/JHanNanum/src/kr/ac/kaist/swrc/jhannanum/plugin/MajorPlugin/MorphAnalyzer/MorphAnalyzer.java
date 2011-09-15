package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * 형태소 분석 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface MorphAnalyzer extends Plugin {
	/**
	 * 입력 받은 문장에 대해서 형태소 분석을 수행하여 결과를 반환한다.
	 * 여러 가지 분석이 가능하기 때문에 출력은 분석 결과 문장들의 집합이 되고,
	 * 각 결과 문장은 품사 태깅된 어절들의 집합이다.
	 * @param ps	형태소 분석 이전의 초기 문장
	 * @return	분석 결과 문장들의 집합
	 */
	abstract public SetOfSentences morphAnalyze(PlainSentence ps);

}
