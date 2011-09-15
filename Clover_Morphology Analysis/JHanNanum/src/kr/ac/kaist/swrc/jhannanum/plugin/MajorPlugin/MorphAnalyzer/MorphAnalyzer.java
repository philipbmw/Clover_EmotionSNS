package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * ���¼� �м� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface MorphAnalyzer extends Plugin {
	/**
	 * �Է� ���� ���忡 ���ؼ� ���¼� �м��� �����Ͽ� ����� ��ȯ�Ѵ�.
	 * ���� ���� �м��� �����ϱ� ������ ����� �м� ��� ������� ������ �ǰ�,
	 * �� ��� ������ ǰ�� �±�� �������� �����̴�.
	 * @param ps	���¼� �м� ������ �ʱ� ����
	 * @return	�м� ��� ������� ����
	 */
	abstract public SetOfSentences morphAnalyze(PlainSentence ps);

}
