/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin;

/**
 * 한나눔 형태소 분석기 플러그인 인터페이스.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public interface Plugin {
	/**
	 * 워크플로가 동작하기 이전에 플러그인을 초기화하기 위해서 호출된다.
	 * 플러그인 설정 파일을 통해 동작 옵션을 지정해 줄 수 있다.
	 * @param baseDir		한나눔 기본 경로
	 * @param configFile	플러그인 설정 파일 경로
	 * @throws Exception 
	 */
	abstract public void initialize(String baseDir, String configFile) throws Exception;
	
	/**
	 * 플러그인이 더 이상 활용되지 않는 시점에서 호출된다.
	 * 메모리 해제 등의 마무리 작업을 수행하면 된다.
	 */
	abstract public void shutdown();
	
}