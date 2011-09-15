package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.share.Code;

public class SegmentPosition {
	/**
	 * 세그먼트 위치 기록 자료구조.
	 * 
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
	 * 
	 */
	public class Position {
		char key;
		int state;

		/** 다음 세그먼트 위치 */
		int nextPosition;

		int sIndex;
		int uIndex;
		int nIndex;
		int morphCount;
		int[] morpheme = new int[MAX_MORPHEME_COUNT];
	}
	final public static int MAX_SEGMENT = 1024;

	final public static int MAX_MORPHEME_COUNT = 512;

	/** 사전 검색 단계 */
	final public static int SP_STATE_N = 0;

	/** 음운 변화에 의한 확장 단계 */
	final public static int SP_STATE_D = 1;

	final public static int SP_STATE_R = 2;

	/** 결합 규칙을 검사하는 단계 */
	final public static int SP_STATE_M = 3;

	final public static int SP_STATE_F = 4;

	final public static char POSITION_START_KEY = 0;
	private Position[] position = null;

	private int positionEnd = 0;

	public SegmentPosition() {
		position = new Position[MAX_SEGMENT];
		for (int i = 0; i < MAX_SEGMENT; i++) {
			position[i] = new Position();
		}
	}

	/**
	 * 새로운 Segment Position을 등록한다.
	 * 
	 * @param key
	 *            등록할 Segment Position의 key
	 * @return 등록된 Segment Position의 인덱스
	 */
	public int addPosition(char key) {
		position[positionEnd].key = key;
		position[positionEnd].state = SP_STATE_N;
		position[positionEnd].morphCount = 0;
		position[positionEnd].nextPosition = 0;
		position[positionEnd].sIndex = 0;
		position[positionEnd].uIndex = 0;
		position[positionEnd].nIndex = 0;

		return positionEnd++;
	}

	/**
	 * 인덱스에 해당하는 Segment Position을 반환한다.
	 * 
	 * @param index
	 *            Segment Position 인덱스
	 * @return index에 해당하는 Segment Position
	 */
	public Position getPosition(int index) {
		return position[index];
	}

	/**
	 * 형태소 분석 이전에 기존 데이터를 초기화한다. 인덱스 정보와 Segment Position 정보를 초기화하고, 앞으로 분석할 문장에
	 * 대해서 자료구조를 초기화한다.
	 * 
	 * @param str
	 *            앞으로 분석할 문장
	 */
	public void init(String str, Simti simti) {
		int prevIndex = 0;
		int nextIndex = 0;

		positionEnd = 0;
		prevIndex = addPosition(POSITION_START_KEY);
		position[prevIndex].state = SP_STATE_M;

		String rev = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			rev += str.charAt(i);
		}

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			nextIndex = addPosition(c);
			setPositionLink(prevIndex, nextIndex);
			prevIndex = nextIndex;

			simti.insert(rev.substring(0, str.length() - i).toCharArray(),
					nextIndex);
		}

		// 단어의 끝을 나타내는 위치로서의 역할
		setPositionLink(prevIndex, 0);
	}

	/**
	 * 해당 인덱스의 Segment Position에 대한 다음 Segment Position을 반환한다.
	 * 
	 * @param index
	 *            Segment Position 인덱스
	 * @return 다음 Segment Position
	 */
	public int nextPosition(int index) {
		return position[index].nextPosition;
	}

	/**
	 * Segment Position 정보를 출력한다.
	 */
	public void printPosition() {
		System.err.println("positionEnd: " + positionEnd);
		for (int i = 0; i < positionEnd; i++) {
			System.err.format("position[%d].key=%c nextPosition=%d\n", i, Code
					.toCompatibilityJamo(position[i].key),
					position[i].nextPosition);
		}
	}

	/**
	 * 두 Segment Position의 연결을 설정한다.
	 * 
	 * @param prevIndex
	 *            앞 노드의 인덱스
	 * @param nextIndex
	 *            다음 노드의 인덱스
	 * @return 연결된 앞 노드의 인덱스
	 */
	public int setPositionLink(int prevIndex, int nextIndex) {
		position[prevIndex].nextPosition = nextIndex;
		return prevIndex;
	}

}
