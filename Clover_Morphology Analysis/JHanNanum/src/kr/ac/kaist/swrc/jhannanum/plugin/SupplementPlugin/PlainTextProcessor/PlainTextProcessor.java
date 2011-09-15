package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.Plugin;

/**
 * ���¼� �м� �÷����� ������ ��ġ�ϴ� 1�ܰ� ���� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public interface PlainTextProcessor extends Plugin {
	/**
	 * �ʱ� �Է� ���忡 ���� ��ó�� �� ���͸� �۾��� ������ ��� ������ ��ȯ�Ѵ�.
	 * @param ps	�ʱ� �Է� ����
	 * @return	��ó�� �� ���͸� �۾��� ������ ��� ����
	 */
	abstract public PlainSentence doProcess(PlainSentence ps);
	
	/**
	 * �÷������� ���ο� ���� ó���ؾ� �� �����Ͱ� �����ִ��� Ȯ���Ѵ�.
	 * true�� ��ȯ�Ѵٸ� �ý����� ���� doProcess() �޼ҵ带 ȣ���� �� �Ķ���ͷ� null�� �Ѱ��ش�.
	 * ���� �÷����� ���ο��� �Է� ���۸� �������� �ʾƵ� �ȴ�.
	 * @return	true ó���ؾ� �� �����Ͱ� ��������, false ó���ؾ� �� �����Ͱ� ����
	 */
	abstract public boolean hasRemainingData();
	
	/**
	 * ���������� �����ϰ� �ִ� ó�� ����� ��ȯ�Ѵ�.
	 * ������� ó���ϴ� �����Ϳ� ������ ó���� �����Ͱ� ���� �������� ��� �ý����� �� �޼ҵ带 ȣ���Ѵ�.
	 * �ý����� hasRemainingData()�� false�� ��ȯ�ϴ� ��쿡�� �� �޼ҵ带 ȣ���Ѵ�. 
	 * ��) �� ������ �м��� ������ ���� ������ �м��Ϸ��� �ϴ� ���
	 * @return	�����ϰ� �ִ� �м� ���, ���� ��� null
	 */
	abstract public PlainSentence flush();
}
