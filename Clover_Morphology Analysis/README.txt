:: HanNanum �ѱ��� ���¼� �м��� �ڹ� ���� ::

* �ۼ���: 2010�� 7�� 31��

* ��������: 2011�� 1�� 10��

* �ۼ���: �ڻ��(hudoni@world.kaist.ac.kr)


1. ���� 

/JHanNanum 	�ҽ��ڵ�, �÷����� ���� ����, ��� �ڷ� 
/GUIDemo	GUI ���� ���α׷�
/README.txt 	�ȳ� ���� 
/data.zip 	��� �ڷ� 
/conf.zip 	�÷����� ���� ����
/jhannanum.jar 	�ѳ��� ���̺귯�� 


2. �ѳ��� ���̺귯�� ����� 

- jhannanum.jar�� ���̺귯���� ��� 

- ������Ʈ Ȩ���丮�� data.zip ���� ���� (PROJECT_ROOT/data) 

- ������Ʈ Ȩ���丮�� conf.zip ���� ���� (PROJECT_ROOT/conf) 

- ��� ���� 
  kr.ac.kaist.swrc.jhannanum.demo.WorkflowWithHMMTagger 
  kr.ac.kaist.swrc.jhannanum.demo.PredefinedWorkflow 


3. GUI ���� ���� ���

- GUIDemo/execute.bat ����

- ����ȯ��:
	Windows OS
	Java Runtime Environment 1.6 �̻�


4. �ѳ��� �÷����� ����

- JHanNanum ���丮�� Eclipse IDE���� Java Project�� ���

- �����ϰ��� �ϴ� �ܰ��� �÷����� �������̽��� �����ϸ� �ٷ� �ѳ��� Workflow�� ���� �����մϴ�.


:: 2011�� 1�� 10�� ::

* ǰ�� �±� �ܰ� ���Ŀ� ��縸�� �����ϴ� �÷������� �߰��Ǿ����ϴ�. (NounExtractor)

* InformalSentenceFilter���� �߰ߵ� ���� �޺κ��� �����Ǵ� ������ �����߽��ϴ�.

* �ѳ����� Base Directory�� ������ �� �ֵ��� �����߽��ϴ�.
Workflow ������ Base Directory�� �����ϸ� ���Ŀ��� ������ ���� Base Directory�� ���� ��� ��θ� ����ϸ� �˴ϴ�.

��)

> ���� ���� ���
	PROJECT_HOME/data/hannanum/conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json
	PROJECT_HOME/data/hannanum/conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json

> data/hannanum�� Base Directory�� ����
	Workflow workflow = new Workflow("data/hannanum");
	workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
	workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");



:: 2010�� 11�� 16�� ::

* �׽�Ʈ �������� �߰ߵ� ���׵��� �����߽��ϴ�.

* �ѳ��� ���¼� �м����� ����� �ٷ� ���� ������ Ȯ���� �� �ֵ��� �ٷ� ���డ���� GUI Demo ���α׷��� �߰��߽��ϴ�.



:: 2010�� 10�� 31�� ::

* �ڹ� ���� �ѳ��� ver.0.8 release

* �÷����� ������Ʈ ����� ��Ű��ó�� �����Ͽ� ���Ӱ� ������Ʈ �Ͽ����ϴ�.

* �ѳ��� ��ũ�÷ο� ���� ���ؿ� �÷����� �߰� ������ ���� ���� GUI ��� ���� ���α׷��� �߰��߽��ϴ�.

* ���ο� �ѳ����� ���� �ڼ��� ������ �� ������Ʈ �� �����Դϴ�.



:: 2010�� 8�� 31�� ::

* Trie ���� �׽�Ʈ �ڵ� ����

* HMMTagger ȯ�漳������ Ȱ�� �����ϵ��� ���� (�Ʒ� ���� �ڵ�� ���� ȯ�漳������ ���� ����)

	...

	HanNanum han = new HanNanum();
    	
    	try {
    		/* Setting up the work flow */
    		han.addModule(new WebInputFilter(), null);
    		han.addModule(new SentenceSplitter(), null);
			han.addModule(new MorphAnalyzer(), "data/morph.conf");
			han.addModule(new UnknownProcessor(), null);
			han.addModule(new USNProcessor(), null);
			han.addModule(new HMMTagger(), "data/tagger.conf");

	...

			kr.ac.kaist.swrc.jhannanum.demo.WorkflowWithHMMTagger���� ��ü ���α׷� Ȯ�� �����մϴ�.



:: 2010�� 8�� 12�� ::

* ������� �߰ߵ� ��� ������ �� ���׸� �����Ͽ����ϴ�.

* WebInputFilter�� �߰��Ͽ��� �⺻ Workflow�� �ݿ��߽��ϴ�. 

* ����� ������ �߻��ϴ� ������ ������ �Խ��ǿ� �÷��ֽðų� E-mail: hudoni@world.kaist.ac.kr, hudoni@gmail.com �� �����ּ���.



:: 2010�� 8�� 3�� ::

* �� �� ���� ������ �� ������Ʈ �� �����Դϴ�.

* ���� 9������ ��� �����͸� ó���ϴ� �������� �پ��� �������� �߰��߽��ϴ�. ���� ������ �߻��� ��� �����޽����� ����ϰ� ����ġ���� ������ �߽��ϴ�. ���ݱ��� �߰ߵ� ��� ������ ó���� �Ŀ� ���ο� ������ ������ �ϰڽ��ϴ�.

* ���� ���� �ֽ� ������ SVN Repository�� ���� �ٿ�ε尡 �����մϴ�.

* ���� ������ ������ �������� �Խ��ǿ� ���� �÷��ּ���.
