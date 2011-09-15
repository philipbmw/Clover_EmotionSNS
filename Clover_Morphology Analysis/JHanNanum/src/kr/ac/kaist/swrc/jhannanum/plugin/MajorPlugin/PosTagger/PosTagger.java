/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * 품사 태거 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PosTagger extends Plugin {
	/**
	 * 하나의 문장에 대한 형태소 분석 결과의 집합을 입력받아서 그 중 가장 적합한 분석 하나를 선택한다.
	 * @param sos	형태소 분석 결과 생성된 문장들의 집합
	 * @return	가장 유망한 형태소 분석된 문장
	 */
	abstract Sentence tagPOS(SetOfSentences sos);
}
