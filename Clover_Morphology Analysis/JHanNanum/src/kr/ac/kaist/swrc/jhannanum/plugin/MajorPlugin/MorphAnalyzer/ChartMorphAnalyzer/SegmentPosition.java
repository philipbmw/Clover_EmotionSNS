package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.share.Code;

public class SegmentPosition {
	/**
	 * ���׸�Ʈ ��ġ ��� �ڷᱸ��.
	 * 
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
	 * 
	 */
	public class Position {
		char key;
		int state;

		/** ���� ���׸�Ʈ ��ġ */
		int nextPosition;

		int sIndex;
		int uIndex;
		int nIndex;
		int morphCount;
		int[] morpheme = new int[MAX_MORPHEME_COUNT];
	}
	final public static int MAX_SEGMENT = 1024;

	final public static int MAX_MORPHEME_COUNT = 512;

	/** ���� �˻� �ܰ� */
	final public static int SP_STATE_N = 0;

	/** ���� ��ȭ�� ���� Ȯ�� �ܰ� */
	final public static int SP_STATE_D = 1;

	final public static int SP_STATE_R = 2;

	/** ���� ��Ģ�� �˻��ϴ� �ܰ� */
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
	 * ���ο� Segment Position�� ����Ѵ�.
	 * 
	 * @param key
	 *            ����� Segment Position�� key
	 * @return ��ϵ� Segment Position�� �ε���
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
	 * �ε����� �ش��ϴ� Segment Position�� ��ȯ�Ѵ�.
	 * 
	 * @param index
	 *            Segment Position �ε���
	 * @return index�� �ش��ϴ� Segment Position
	 */
	public Position getPosition(int index) {
		return position[index];
	}

	/**
	 * ���¼� �м� ������ ���� �����͸� �ʱ�ȭ�Ѵ�. �ε��� ������ Segment Position ������ �ʱ�ȭ�ϰ�, ������ �м��� ���忡
	 * ���ؼ� �ڷᱸ���� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param str
	 *            ������ �м��� ����
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

		// �ܾ��� ���� ��Ÿ���� ��ġ�μ��� ����
		setPositionLink(prevIndex, 0);
	}

	/**
	 * �ش� �ε����� Segment Position�� ���� ���� Segment Position�� ��ȯ�Ѵ�.
	 * 
	 * @param index
	 *            Segment Position �ε���
	 * @return ���� Segment Position
	 */
	public int nextPosition(int index) {
		return position[index].nextPosition;
	}

	/**
	 * Segment Position ������ ����Ѵ�.
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
	 * �� Segment Position�� ������ �����Ѵ�.
	 * 
	 * @param prevIndex
	 *            �� ����� �ε���
	 * @param nextIndex
	 *            ���� ����� �ε���
	 * @return ����� �� ����� �ε���
	 */
	public int setPositionLink(int prevIndex, int nextIndex) {
		position[prevIndex].nextPosition = nextIndex;
		return prevIndex;
	}

}
