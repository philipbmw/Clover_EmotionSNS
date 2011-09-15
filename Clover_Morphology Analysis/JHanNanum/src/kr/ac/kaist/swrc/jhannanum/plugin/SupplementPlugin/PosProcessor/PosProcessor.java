package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * ǰ�� �°� ���Ŀ� ��ġ�ϴ� 3�ܰ� ���� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PosProcessor extends Plugin {
	/**
	 * ǰ�� �±�� ���忡 ���� ��ó�� �� �ٸ� ���� ���α׷��� ���� ��ó�� �۾� ���� �����Ѵ�.
	 * �Է°� ����� ǰ�� �±�� �����̴�.
	 * @param st	���¼� �м� ��� ����
	 * @return	���¼� �м� ��� ����
	 */
	abstract public Sentence doProcess(Sentence st);
}
