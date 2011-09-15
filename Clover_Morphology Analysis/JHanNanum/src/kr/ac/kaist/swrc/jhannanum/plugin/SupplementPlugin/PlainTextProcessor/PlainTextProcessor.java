package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * 형태소 분석 플러그인 이전에 위치하는 1단계 보조 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PlainTextProcessor extends Plugin {
	/**
	 * 초기 입력 문장에 대한 전처리 및 필터링 작업을 수행한 결과 문장을 반환한다.
	 * @param ps	초기 입력 문장
	 * @return	전처리 및 필터링 작업을 수행한 결과 문장
	 */
	abstract public PlainSentence doProcess(PlainSentence ps);
	
	/**
	 * 플러그인의 내부에 아직 처리해야 할 데이터가 남아있는지 확인한다.
	 * true를 반환한다면 시스템이 다음 doProcess() 메소드를 호출할 때 파라미터로 null을 넘겨준다.
	 * 따라서 플러그인 내부에서 입력 버퍼를 관리하지 않아도 된다.
	 * @return	true 처리해야 할 데이터가 남아있음, false 처리해야 할 데이터가 없음
	 */
	abstract public boolean hasRemainingData();
	
	/**
	 * 내부적으로 보관하고 있는 처리 결과를 반환한다.
	 * 현재까지 처리하던 데이터와 다음에 처리할 데이터가 서로 독립적인 경우 시스템이 이 메소드를 호출한다.
	 * 시스템은 hasRemainingData()가 false를 반환하는 경우에만 이 메소드를 호출한다. 
	 * 예) 한 문서의 분석이 끝나고 다음 문서를 분석하려고 하는 경우
	 * @return	보관하고 있던 분석 결과, 없는 경우 null
	 */
	abstract public PlainSentence flush();
}
