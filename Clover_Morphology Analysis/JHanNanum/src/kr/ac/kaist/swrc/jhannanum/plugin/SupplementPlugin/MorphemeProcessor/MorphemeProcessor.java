package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * 형태소 분석 플러그인 이후에 위치하는 2단계 보조 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface MorphemeProcessor extends Plugin {
	/**
	 * 형태소 분석 결과에 대한 후처리 및 필터링 작업 등을 수행한다.
	 * 입력과 출력은 하나의 문장에 대한 형태소 분석 결과가 포함된 문장들의 집합이다.
	 * @param sos	형태소 분석 결과 문장들의 집합
	 * @return	후처리 및 필터링 작업을 한 형태소 분석 결과 문장들의 집합
	 */
	abstract public SetOfSentences doProcess(SetOfSentences sos);
}
