:: HanNanum 한국어 형태소 분석기 자바 버전 ::

* 작성일: 2010년 7월 31일

* 최종수정: 2011년 1월 10일

* 작성자: 박상원(hudoni@world.kaist.ac.kr)


1. 구성 

/JHanNanum 	소스코드, 플러그인 설정 파일, 기반 자료 
/GUIDemo	GUI 데모 프로그램
/README.txt 	안내 문서 
/data.zip 	기반 자료 
/conf.zip 	플러그인 설정 파일
/jhannanum.jar 	한나눔 라이브러리 


2. 한나눔 라이브러리 사용방법 

- jhannanum.jar를 라이브러리로 등록 

- 프로젝트 홈디렉토리에 data.zip 압축 해제 (PROJECT_ROOT/data) 

- 프로젝트 홈디렉토리에 conf.zip 압축 해제 (PROJECT_ROOT/conf) 

- 사용 예제 
  kr.ac.kaist.swrc.jhannanum.demo.WorkflowWithHMMTagger 
  kr.ac.kaist.swrc.jhannanum.demo.PredefinedWorkflow 


3. GUI 데모 실행 방법

- GUIDemo/execute.bat 실행

- 실행환경:
	Windows OS
	Java Runtime Environment 1.6 이상


4. 한나눔 플러그인 개발

- JHanNanum 디렉토리를 Eclipse IDE에서 Java Project로 등록

- 구현하고자 하는 단계의 플러그인 인터페이스를 구현하면 바로 한나눔 Workflow에 적용 가능합니다.


:: 2011년 1월 10일 ::

* 품사 태깅 단계 이후에 명사만을 추출하는 플러그인이 추가되었습니다. (NounExtractor)

* InformalSentenceFilter에서 발견된 패턴 뒷부분이 생략되는 문제를 수정했습니다.

* 한나눔의 Base Directory를 설정할 수 있도록 수정했습니다.
Workflow 생성시 Base Directory를 설정하면 이후에는 이전과 같이 Base Directory에 대한 상대 경로를 사용하면 됩니다.

예)

> 실제 파일 경로
	PROJECT_HOME/data/hannanum/conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json
	PROJECT_HOME/data/hannanum/conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json

> data/hannanum을 Base Directory로 설정
	Workflow workflow = new Workflow("data/hannanum");
	workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
	workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");



:: 2010년 11월 16일 ::

* 테스트 과정에서 발견된 버그들을 수정했습니다.

* 한나눔 형태소 분석기의 기능을 바로 더욱 간편이 확인할 수 있도록 바로 실행가능한 GUI Demo 프로그램을 추가했습니다.



:: 2010년 10월 31일 ::

* 자바 버전 한나눔 ver.0.8 release

* 플러그인 컴포넌트 기반의 아키텍처를 적용하여 새롭게 업데이트 하였습니다.

* 한나눔 워크플로에 대한 이해와 플러그인 추가 개발을 돕기 위한 GUI 기반 데모 프로그램을 추가했습니다.

* 새로운 한나눔에 대한 자세한 문서는 곧 업데이트 할 예정입니다.



:: 2010년 8월 31일 ::

* Trie 관련 테스트 코드 삭제

* HMMTagger 환경설정파일 활용 가능하도록 수정 (아래 예제 코드와 같이 환경설정파일 지정 가능)

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

			kr.ac.kaist.swrc.jhannanum.demo.WorkflowWithHMMTagger에서 전체 프로그램 확인 가능합니다.



:: 2010년 8월 12일 ::

* 현재까지 발견된 모든 문제점 및 버그를 수정하였습니다.

* WebInputFilter를 추가하였고 기본 Workflow에 반영했습니다. 

* 사용중 오류가 발생하는 문장이 있으면 게시판에 올려주시거나 E-mail: hudoni@world.kaist.ac.kr, hudoni@gmail.com 로 연락주세요.



:: 2010년 8월 3일 ::

* 좀 더 상세한 문서는 곧 업데이트 될 예정입니다.

* 현재 9만건의 댓글 데이터를 처리하는 과정에서 다양한 문제점을 발견했습니다. 현재 문제가 발생할 경우 에러메시지를 출력하고 지나치도록 설정을 했습니다. 지금까지 발견된 모든 문제를 처리한 후에 새로운 버전을 릴리즈 하겠습니다.

* 개발 중인 최신 버전은 SVN Repository를 통해 다운로드가 가능합니다.

* 문의 사항이 있으면 언제든지 게시판에 글을 올려주세요.
