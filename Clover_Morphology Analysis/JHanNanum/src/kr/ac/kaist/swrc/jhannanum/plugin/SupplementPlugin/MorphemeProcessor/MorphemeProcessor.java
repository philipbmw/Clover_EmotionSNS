package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * ���¼� �м� �÷����� ���Ŀ� ��ġ�ϴ� 2�ܰ� ���� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface MorphemeProcessor extends Plugin {
	/**
	 * ���¼� �м� ����� ���� ��ó�� �� ���͸� �۾� ���� �����Ѵ�.
	 * �Է°� ����� �ϳ��� ���忡 ���� ���¼� �м� ����� ���Ե� ������� �����̴�.
	 * @param sos	���¼� �м� ��� ������� ����
	 * @return	��ó�� �� ���͸� �۾��� �� ���¼� �м� ��� ������� ����
	 */
	abstract public SetOfSentences doProcess(SetOfSentences sos);
}
