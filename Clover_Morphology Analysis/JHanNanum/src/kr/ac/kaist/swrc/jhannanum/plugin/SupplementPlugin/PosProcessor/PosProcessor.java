package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * 품사 태거 이후에 위치하는 3단계 보조 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PosProcessor extends Plugin {
	/**
	 * 품사 태깅된 문장에 대한 후처리 및 다른 응용 프로그램을 위한 전처리 작업 등을 수행한다.
	 * 입력과 출력은 품사 태깅된 문장이다.
	 * @param st	형태소 분석 결과 문장
	 * @return	형태소 분석 결과 문장
	 */
	abstract public Sentence doProcess(Sentence st);
}
