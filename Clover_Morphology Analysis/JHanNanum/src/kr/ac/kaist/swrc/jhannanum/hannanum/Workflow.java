/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.hannanum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.exception.ResultTypeException;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.PosTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;
import kr.ac.kaist.swrc.jhannanum.thread.MorphAnalyzerThread;
import kr.ac.kaist.swrc.jhannanum.thread.MorphemeProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PlainTextProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PosProcThread;
import kr.ac.kaist.swrc.jhannanum.thread.PosTaggerThread;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class Workflow {
	public static int MAX_SUPPLEMENT_PLUGIN_NUM = 8;
	
	private int maxSupplementPluginNum = 0;
	
	private boolean isThreadMode = false;
	
	private int outputPhaseNum = 0;
	
	private int outputQueueNum = 0;
	
	private LinkedList<Thread> threadList = null;
	
	
	/* Major Plugins */
	
	/** 2단계 주 플러그인 - 형태소 분석 플러그인 */  
	private MorphAnalyzer morphAnalyzer = null;
	
	/** 2단계 주 플러그인 - 형태소 분석 플러그인 환경 설정 파일 */  
	private String morphAnalyzerConfFile = null;
	
	/** 3단계 주 플러그인 - 품사 태거 플러그인 */
	private PosTagger posTagger = null;
	
	/** 3단계 주 플러그인 - 품사 태거 플러그인 환경 설정 파일 */
	private String posTaggerConfFile = null;
	
	
	/* Supplement Plugins */
	
	/** 1단계 보조 플러그인 - 초기 텍스트 처리 플러그인 */ 
	private PlainTextProcessor[] plainTextProcessors = null;
	
	/** 1단계 보조 플러그인 - 초기 텍스트 처리 플러그인 환경 설정 파일*/ 
	private String[] plainTextProcessorsConfFiles = null;
	
	/** 1단계 보조 플러그인 - 초기 텍스트 처리 플러그인 갯수 */
	private int plainTextPluginCnt = 0;
	
	/** 2단계 보조 플러그인 - 형태소 분석 이후 처리 플러그인 */
	private MorphemeProcessor[] morphemeProcessors = null;
	
	/** 2단계 보조 플러그인 - 형태소 분석 이후 처리 플러그인 환경 설정 파일 */
	private String[] morphemeProcessorsConfFiles = null;
	
	/** 2단계 보조 플러그인 - 형태소 분석 이후 처리 플러그인 갯수 */
	private int morphemePluginCnt = 0;
	
	/** 3단계 보조 플러그인 - 품사 태깅 이후 처리 플러그인 */
	private PosProcessor[] posProcessors = null;
	
	/** 3단계 보조 플러그인 - 품사 태깅 이후 처리 플러그인 환경 설정 파일 */
	private String[] posProcessorConfFiles = null;
	
	/** 3단계 보조 플러그인 - 품사 태깅 이후 처리 플러그인 갯수 */
	private int posPluginCnt = 0;
	
	private boolean isInitialized = false;
	
	private String baseDir = null;
	
	
	/* Communication Queues */
	
	/** 1단계 입출력 큐 */
	ArrayList<LinkedBlockingQueue<PlainSentence>> queuePhase1 = null;
	
	/** 2단계 입출력 큐 */
	ArrayList<LinkedBlockingQueue<SetOfSentences>> queuePhase2 = null;
	
	/** 3단계 입출력 큐 */
	ArrayList<LinkedBlockingQueue<Sentence>> queuePhase3 = null;
	
	/**
	 * 생성자.
	 * 각 단계 보조 플러그인의 최대 갯수는 Workflow.MAX_SUPPLEMENT_PLUGIN_NUM 로 설정된다.
	 */
	public Workflow() {
		this.maxSupplementPluginNum = MAX_SUPPLEMENT_PLUGIN_NUM;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = ".";
	}
	
	/**
	 * 생성자.
	 * 각 단계 보조 플러그인의 최대 갯수는 Workflow.MAX_SUPPLEMENT_PLUGIN_NUM 로 설정된다.
	 * @param baseDir 한나눔 라이브러리의 기본 경로 - 설정 파일에 기록된 경로 앞에 추가된다.
	 */
	public Workflow(String baseDir) {
		this.maxSupplementPluginNum = MAX_SUPPLEMENT_PLUGIN_NUM;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = baseDir;
	}
	
	/**
	 * 생성자.
	 * @param maxSupplementPluginNum 각 단계 보조 플러그인의 최대 갯수
	 */
	public Workflow(String baseDir, int maxSupplementPluginNum) {
		this.maxSupplementPluginNum = maxSupplementPluginNum;
		
		plainTextProcessors = new PlainTextProcessor[maxSupplementPluginNum];
		morphemeProcessors = new MorphemeProcessor[maxSupplementPluginNum];
		posProcessors = new PosProcessor[maxSupplementPluginNum];
		plainTextProcessorsConfFiles = new String[maxSupplementPluginNum];
		morphemeProcessorsConfFiles = new String[maxSupplementPluginNum];
		posProcessorConfFiles = new String[maxSupplementPluginNum];
		
		queuePhase1 = new ArrayList<LinkedBlockingQueue<PlainSentence>>(maxSupplementPluginNum);
		queuePhase2 = new ArrayList<LinkedBlockingQueue<SetOfSentences>>(maxSupplementPluginNum + 1);
		queuePhase3 = new ArrayList<LinkedBlockingQueue<Sentence>>(maxSupplementPluginNum + 1);
		
		threadList = new LinkedList<Thread>();
		
		isInitialized = true;
		
		this.baseDir = baseDir;
	}
	
	/** 
	 * 2단계 주 플러그인 - 형태소 분석 플러그인 설정 
	 * @param ma	형태소 분석 플러그인
	 * @param configFile	플러그인 환경 설정 파일 - basdeDir에 대한 상대 경로
	 */
	public void setMorphAnalyzer(MorphAnalyzer ma, String configFile) {
		morphAnalyzer = ma;
		morphAnalyzerConfFile = baseDir + "/" + configFile;
	}
	
	/**
	 * 3단계 주 플러그인 - 품사 태거 플러그인 설정
	 * @param tagger	품사 태거 플러그인
	 * @param configFile	플러그인 환경 설정 파일 - basdeDir에 대한 상대 경로
	 */
	public void setPosTagger(PosTagger tagger, String configFile) {
		posTagger = tagger;
		posTaggerConfFile = baseDir + "/" + configFile;
	}
	
	/**
	 * 1단계 보조 플러그인 - 초기 텍스트 처리 플러그인을  마지막에 추가
	 * @param plugin Workflow에 추가하고자 하는 플러그인
	 * @param configFile	플러그인 환경 설정 파일 - basdeDir에 대한 상대 경로
	 */
	public void appendPlainTextProcessor(PlainTextProcessor plugin, String configFile) {
		plainTextProcessorsConfFiles[plainTextPluginCnt] = baseDir + "/" + configFile;
		plainTextProcessors[plainTextPluginCnt++] = plugin;
	}
	
	/**
	 * 2단계 보조 플러그인 - 형태소 분석 이후 처리 플러그인을 마지막에 추가
	 * @param plugin Workflow에 추가하고자 하는 플러그인
	 * @param configFile	플러그인 환경 설정 파일 - basdeDir에 대한 상대 경로
	 */
	public void appendMorphemeProcessor(MorphemeProcessor plugin, String configFile) {
		morphemeProcessorsConfFiles[morphemePluginCnt] = baseDir + "/" + configFile;
		morphemeProcessors[morphemePluginCnt++] = plugin;
	}
	
	/**
	 * 3단계 보조 플러그인 - 품사 태깅 이후 처리 플러그인을 마지막에 추가
	 * @param plugin Workflow에 추가하고자 하는 플러그인
	 * @param configFile	플러그인 환경 설정 파일 - basdeDir에 대한 상대 경로
	 */
	public void appendPosProcessor(PosProcessor plugin, String configFile) {
		posProcessorConfFiles[posPluginCnt] = baseDir + "/" + configFile;
		posProcessors[posPluginCnt++] = plugin;
	}
	
	/**
	 * 플러그인 배치 이후 Workflow를 활성화 시킨다.
	 * threadMode 값을 true로 설정하면 플러그인들은 각각의 thread 위에서 병렬적으로 분석을 수행한다. 
	 * @param threadMode true: 다중 쓰레드 기반 Workflow 동작, false: 단일 쓰레드로 동작
	 * @throws Exception 
	 * @throws Exception 
	 */
	public void activateWorkflow(boolean threadMode) throws Exception {
		if (threadMode) {
			isThreadMode = true;
			
			// 1단계 보조 플러그인들 초기화 및 통신 큐 준비
			LinkedBlockingQueue<PlainSentence> in1 = null;
			LinkedBlockingQueue<PlainSentence> out1 = new LinkedBlockingQueue<PlainSentence>();
			
			queuePhase1.add(out1);
			
			for (int i = 0; i < plainTextPluginCnt; i++) {
				in1 = out1;
				out1 = new LinkedBlockingQueue<PlainSentence>();
				queuePhase1.add(out1);
				
				plainTextProcessors[i].initialize(baseDir, plainTextProcessorsConfFiles[i]);
				threadList.add(new PlainTextProcThread(plainTextProcessors[i], in1, out1));
			}
			
			if (morphAnalyzer == null) {
				outputPhaseNum = 1;
				outputQueueNum = plainTextPluginCnt;
				runThreads();
				return;
			}
			in1 = out1;
			
			// 2단계 주 플러그인 초기화 및 통신 큐 준비
			LinkedBlockingQueue<SetOfSentences> in2 = null;
			LinkedBlockingQueue<SetOfSentences> out2 = new LinkedBlockingQueue<SetOfSentences>();
			
			queuePhase2.add(out2);
			morphAnalyzer.initialize(baseDir, morphAnalyzerConfFile);
			
			threadList.add(new MorphAnalyzerThread(morphAnalyzer, in1, out2));
			
			// 2단계 보조 플러그인들 초기화
			for (int i = 0; i < morphemePluginCnt; i++) {
				in2 = out2;
				out2 = new LinkedBlockingQueue<SetOfSentences>();
				
				queuePhase2.add(out2);
				morphemeProcessors[i].initialize(baseDir, morphemeProcessorsConfFiles[i]);
				
				threadList.add(new MorphemeProcThread(morphemeProcessors[i], in2, out2));
			}
			
			if (posTagger == null) {
				outputPhaseNum = 2;
				outputQueueNum = morphemePluginCnt;
				runThreads();
				return;
			}
			in2 = out2;
			
			// 3단계 주 플러그인 초기화 및 통신 큐 준비
			LinkedBlockingQueue<Sentence> in3 = null;
			LinkedBlockingQueue<Sentence> out3 = new LinkedBlockingQueue<Sentence>();
			
			posTagger.initialize(baseDir, posTaggerConfFile); 
			queuePhase3.add(out3);
			
			threadList.add(new PosTaggerThread(posTagger, in2, out3));
			
			// 3단계 보조 플러그인들 초기화 및 통신 큐 준비
			for (int i = 0; i < posPluginCnt; i++) {
				in3 = out3;
				out3 = new LinkedBlockingQueue<Sentence>();
				
				queuePhase3.add(out3);
				posProcessors[i].initialize(baseDir, posProcessorConfFiles[i]);
				
				threadList.add(new PosProcThread(posProcessors[i], in3, out3));
			}
			
			outputPhaseNum = 3;
			outputQueueNum = posPluginCnt;
			runThreads();
		} else {
			isThreadMode = false;
			
			// 1단계 보조 플러그인들 초기화 및 통신 큐 준비
			queuePhase1.add(new LinkedBlockingQueue<PlainSentence>());
			
			for (int i = 0; i < plainTextPluginCnt; i++) {
				plainTextProcessors[i].initialize(baseDir, plainTextProcessorsConfFiles[i]);
				queuePhase1.add(new LinkedBlockingQueue<PlainSentence>());
			}
			
			if (morphAnalyzer == null) {
				outputPhaseNum = 1;
				outputQueueNum = plainTextPluginCnt;
				return;
			}
			
			// 2단계 주 플러그인 초기화 및 통신 큐 준비
			morphAnalyzer.initialize(baseDir, morphAnalyzerConfFile);
			queuePhase2.add(new LinkedBlockingQueue<SetOfSentences>());
			
			// 2단계 보조 플러그인들 초기화
			for (int i = 0; i < morphemePluginCnt; i++) {
				morphemeProcessors[i].initialize(baseDir, morphemeProcessorsConfFiles[i]);
				queuePhase2.add(new LinkedBlockingQueue<SetOfSentences>());
			}
			
			if (posTagger == null) {
				outputPhaseNum = 2;
				outputQueueNum = morphemePluginCnt;
				return;
			}
			
			// 3단계 주 플러그인 초기화 및 통신 큐 준비
			posTagger.initialize(baseDir, posTaggerConfFile);
			queuePhase3.add(new LinkedBlockingQueue<Sentence>());
			
			// 3단계 보조 플러그인들 초기화 및 통신 큐 준비
			for (int i = 0; i < posPluginCnt; i++) {
				posProcessors[i].initialize(baseDir, posProcessorConfFiles[i]);
				queuePhase3.add(new LinkedBlockingQueue<Sentence>());
			}
			
			outputPhaseNum = 3;
			outputQueueNum = posPluginCnt;
		}
	}
	
	private void runThreads() {
		for (Thread th : threadList) {
			th.start();
		}
	}
	
	public void close() {
		if (isThreadMode) {
			for (Thread th : threadList) {
				th.interrupt();
			}
			threadList.clear();
		}
	}
	
	public void clear() {
		close();
		
		if (isInitialized) {
			queuePhase1.clear();
			queuePhase2.clear();
			queuePhase3.clear();
			isThreadMode = false;
			outputPhaseNum = 0;
			outputQueueNum = 0;
			plainTextPluginCnt = 0;
			morphemePluginCnt = 0;
			posPluginCnt = 0;
			morphAnalyzer = null;
			posTagger = null;
		}
	}
	
	public void analyze(String document) {
		String[] strArray = document.split("\n");
		LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(0);
		
		if (queue == null) {
			return;
		}
		
		for (int i = 0; i < strArray.length - 1; i++) {
			queue.add(new PlainSentence(0, i, false, strArray[i].trim()));
		}
		queue.add(new PlainSentence(0, strArray.length - 1, true, strArray[strArray.length - 1].trim()));
		
		if (!isThreadMode) {
			analyzeInSingleThread(); 
		}
	}
	
	public void analyze(File document) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(document));
		LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(0);
		
		if (queue == null) {
			return;
		}
		
		String line = null;
		int i = 0;
		
		while ((line = br.readLine()) != null) {
			if (br.ready()) {
				queue.add(new PlainSentence(0, i++, false, line.trim()));
			} else {
				queue.add(new PlainSentence(0, i++, true, line.trim()));
				break;
			}
		}
		
		br.close();
		
		if (!isThreadMode) {
			analyzeInSingleThread(); 
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getResultOfSentence(T a) throws ResultTypeException {
		Object objClass = a.getClass();
		
		try {
			if (PlainSentence.class.equals(objClass)) {
				if (outputPhaseNum != 1) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(outputQueueNum);
				a = (T)queue.take();
			} else if (SetOfSentences.class.equals(objClass)) {
				if (outputPhaseNum != 2) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<SetOfSentences> queue = queuePhase2.get(outputQueueNum);
				a = (T)queue.take();
			} else if (Sentence.class.equals(objClass)) {
				if (outputPhaseNum != 3) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<Sentence> queue = queuePhase3.get(outputQueueNum);
				a = (T)queue.take();
			} else {
				throw new ResultTypeException(outputPhaseNum);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return a;
	}
	
	public String getResultOfSentence() {
		String res = null;
		
		try {
			switch (outputPhaseNum) {
			case 1:
				LinkedBlockingQueue<PlainSentence> out1 = queuePhase1.get(outputQueueNum);
				res = out1.take().toString();
				break;
			case 2:
				LinkedBlockingQueue<SetOfSentences> out2 = queuePhase2.get(outputQueueNum);
				res = out2.take().toString();
				break;
			case 3:
				LinkedBlockingQueue<Sentence> out3 = queuePhase3.get(outputQueueNum);
				res = out3.take().toString();
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public <T> LinkedList<T> getResultOfDocument(T a) throws ResultTypeException {
		Object objClass = a.getClass();
		LinkedList<T> list = new LinkedList<T>();
		
		try {
			if (PlainSentence.class.equals(objClass)) {
				if (outputPhaseNum != 1) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<PlainSentence> queue = queuePhase1.get(outputQueueNum);
				while (true) {
					PlainSentence ps = queue.take();
					list.add((T)ps);
					if (ps.isEndOfDocument()) {
						break;
					}
				}
			} else if (SetOfSentences.class.equals(objClass)) {
				if (outputPhaseNum != 2) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<SetOfSentences> queue = queuePhase2.get(outputQueueNum);
				while (true) {
					SetOfSentences sos = queue.take();
					list.add((T)sos);
					if (sos.isEndOfDocument()) {
						break;
					}
				}
			} else if (Sentence.class.equals(objClass)) {
				if (outputPhaseNum != 3) {
					throw new ResultTypeException(outputPhaseNum);
				}
				LinkedBlockingQueue<Sentence> queue = queuePhase3.get(outputQueueNum);
				while (true) {
					Sentence sent = queue.take();
					list.add((T)sent);
					if (sent.isEndOfDocument()) {
						break;
					}
				}
			} else {
				throw new ResultTypeException(outputPhaseNum);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String getResultOfDocument() {
		StringBuffer buf = new StringBuffer();
		
		try {
			switch (outputPhaseNum) {
			case 1:
				LinkedBlockingQueue<PlainSentence> out1 = queuePhase1.get(outputQueueNum);
				while (true) {
					PlainSentence ps = out1.take();
					buf.append(ps);
					buf.append('\n');
					
					if (ps.isEndOfDocument()) {
						break;
					}
				}
				break;
			case 2:
				LinkedBlockingQueue<SetOfSentences> out2 = queuePhase2.get(outputQueueNum);
				while (true) {
					SetOfSentences sos = out2.take();
					buf.append(sos);
					buf.append('\n');
					
					if (sos.isEndOfDocument()) {
						break;
					}
				}
				break;
			case 3:
				LinkedBlockingQueue<Sentence> out3 = queuePhase3.get(outputQueueNum);
				while (true) {
					Sentence sent = out3.take();
					buf.append(sent);
					buf.append('\n');
					
					if (sent.isEndOfDocument()) {
						break;
					}
				}
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	
	/**
	 * 1단계 통신 큐 리스트에서 첫번째 큐에 있는 데이터를 읽어서 분석을 진행한다. 
	 */
	private void analyzeInSingleThread() {
		// 1단계 분석
		if (plainTextPluginCnt == 0) {
			return;
		}
		
		LinkedBlockingQueue<PlainSentence> inQueue1 = null;
		LinkedBlockingQueue<PlainSentence> outQueue1 = null;
		PlainSentence ps = null;
		outQueue1 = queuePhase1.get(0);
		
		for (int i = 0; i < plainTextPluginCnt; i++) {
			inQueue1 = outQueue1;
			outQueue1 = queuePhase1.get(i+1);
			
			while ((ps = inQueue1.poll()) != null) {
				if ((ps = plainTextProcessors[i].doProcess(ps)) != null) {
					outQueue1.add(ps);
				}
				
				while (plainTextProcessors[i].hasRemainingData()) {
					if ((ps = plainTextProcessors[i].doProcess(null)) != null) {
						outQueue1.add(ps);
					}
				}
				
				if ((ps = plainTextProcessors[i].flush()) != null) {
					outQueue1.add(ps);
				}
			}
		}
		
		// 2단계 분석
		if (morphAnalyzer == null) {
			return;
		}
		
		LinkedBlockingQueue<SetOfSentences> inQueue2 = null;
		LinkedBlockingQueue<SetOfSentences> outQueue2 = null;
		SetOfSentences sos = null;
		inQueue1 = outQueue1;
		outQueue2 = queuePhase2.get(0);
		
		while ((ps = inQueue1.poll()) != null) {
			if ((sos = morphAnalyzer.morphAnalyze(ps)) != null) {
				outQueue2.add(sos);
			}
		}
		
		if (morphemePluginCnt == 0) {
			return;
		}
		
		for (int i = 0; i < morphemePluginCnt; i++) {
			inQueue2 = outQueue2;
			outQueue2 = queuePhase2.get(i+1);

			while ((sos = inQueue2.poll()) != null) {
				if ((sos = morphemeProcessors[i].doProcess(sos)) != null) {
					outQueue2.add(sos);
				}
			}
		}
		
		// 3단계 분석
		if (posTagger == null) {
			return;
		}
		
		LinkedBlockingQueue<Sentence> inQueue3 = null;
		LinkedBlockingQueue<Sentence> outQueue3 = null;
		Sentence sent = null;
		inQueue2 = outQueue2;
		outQueue3 = queuePhase3.get(0);
		
		while ((sos = inQueue2.poll()) != null) {
			if ((sent = posTagger.tagPOS(sos)) != null) {
				outQueue3.add(sent);
			}
		}
		
		if (posPluginCnt == 0) {
			return;
		}
		
		for (int i = 0; i < posPluginCnt; i++) {
			inQueue3 = outQueue3;
			outQueue3 = queuePhase3.get(i+1);

			while ((sent = inQueue3.poll()) != null) {
				if ((sent = posProcessors[i].doProcess(sent)) != null) {
					outQueue3.add(sent);
				}
			}
		}
	}
}
