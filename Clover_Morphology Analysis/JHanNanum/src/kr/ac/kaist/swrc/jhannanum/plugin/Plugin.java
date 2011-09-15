/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin;

/**
 * �ѳ��� ���¼� �м��� �÷����� �������̽�.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public interface Plugin {
	/**
	 * ��ũ�÷ΰ� �����ϱ� ������ �÷������� �ʱ�ȭ�ϱ� ���ؼ� ȣ��ȴ�.
	 * �÷����� ���� ������ ���� ���� �ɼ��� ������ �� �� �ִ�.
	 * @param baseDir		�ѳ��� �⺻ ���
	 * @param configFile	�÷����� ���� ���� ���
	 * @throws Exception 
	 */
	abstract public void initialize(String baseDir, String configFile) throws Exception;
	
	/**
	 * �÷������� �� �̻� Ȱ����� �ʴ� �������� ȣ��ȴ�.
	 * �޸� ���� ���� ������ �۾��� �����ϸ� �ȴ�.
	 */
	abstract public void shutdown();
	
}