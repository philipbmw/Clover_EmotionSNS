/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * ǰ�� �°� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PosTagger extends Plugin {
	/**
	 * �ϳ��� ���忡 ���� ���¼� �м� ����� ������ �Է¹޾Ƽ� �� �� ���� ������ �м� �ϳ��� �����Ѵ�.
	 * @param sos	���¼� �м� ��� ������ ������� ����
	 * @return	���� ������ ���¼� �м��� ����
	 */
	abstract Sentence tagPOS(SetOfSentences sos);
}
