package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.share.JSONReader;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * 형태소 분석을 담당한다.
 * @author Sangwon Park
 */
public class ChartMorphAnalyzer implements MorphAnalyzer {


	final static private String MODULE_NAME = "MorphAnalyzer";

	private AnalyzedDic analyzedDic = null;
	private Trie systemDic = null;
	private Trie userDic = null;
	private NumberDic numDic = null;
	private TagSet tagSet = null;
	private Connection connection = null;
	private ConnectionNot connectionNot = null;

	private MorphemeChart chart = null;
	private Simti simti = null;

	/** 결합 불허용 규칙 파일 */
	private String fileConnectionsNot = "";

	/** 결합 허용 규칙 파일 */
	private String fileConnections = "";

	/** 기분석 사전 */
	private String fileDicAnalyzed = "";

	/** 시스템 사전 */
	private String fileDicSystem = "";

	/** 사용자 사전 */
	private String fileDicUser = "";

	/** 태그 집합 */
	private String fileTagSet = "";
	
	private LinkedList<Eojeol> eojeolList = null;
	
	private PostProcessor postProc = null;
	
	public String getName() {
		return MODULE_NAME;
	}
	
	private Eojeol[] processEojeol(String plainEojeol) {
		String analysis = analyzedDic.get(plainEojeol);

		eojeolList.clear();
		
		if (analysis != null) {
			// 기분석 사전에 등록되어 있는 경우
			StringTokenizer st = new StringTokenizer(analysis, "^");
			while (st.hasMoreTokens()) {
				String analyzed = st.nextToken();
				String[] tokens = analyzed.split("\\+|/");
				
				String[] morphemes = new String[tokens.length / 2];
				String[] tags = new String[tokens.length / 2];
				
				for (int i = 0, j = 0; i < morphemes.length; i++) {
					morphemes[i] = tokens[j++];
					tags[i] = tokens[j++];
				}
				Eojeol eojeol = new Eojeol(morphemes, tags);
				eojeolList.add(eojeol);
			}
		} else {
			// 기분석 사전에 등록되어 있지 않은 경우
			chart.init(plainEojeol);
			chart.analyze();
			chart.getResult();
		}
		
		return eojeolList.toArray(new Eojeol[0]);
	}

	@Override
	public SetOfSentences morphAnalyze(PlainSentence ps) {
		StringTokenizer st = new StringTokenizer(ps.getSentence(), " \t");
		
		String plainEojeol = null;
		int eojeolNum = st.countTokens();
		
		ArrayList<String> plainEojeolArray = new ArrayList<String>(eojeolNum);
		ArrayList<Eojeol[]> eojeolSetArray = new ArrayList<Eojeol[]>(eojeolNum);
				
		while (st.hasMoreTokens()) {
			plainEojeol = st.nextToken();
			
			plainEojeolArray.add(plainEojeol);
			eojeolSetArray.add(processEojeol(plainEojeol));
		}
		
		SetOfSentences sos = new SetOfSentences(ps.getDocumentID(), ps.getSentenceID(),
				ps.isEndOfDocument(), plainEojeolArray, eojeolSetArray);

		// 기존 한나눔에서는 독립적인 모듈로 후처리해줬던 기능을 형태소 분석기 안으로 이동시켰다. 
		sos = postProc.doPostProcessing(sos);

		return sos;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {
		JSONReader json = new JSONReader(configFile);
		
		fileDicSystem = baseDir + "/" + json.getValue("dic_system");
		fileDicUser = baseDir + "/" + json.getValue("dic_user");
		fileConnections = baseDir + "/" + json.getValue("connections");
		fileConnectionsNot = baseDir + "/" + json.getValue("connections_not");
		fileDicAnalyzed = baseDir + "/" + json.getValue("dic_analyzed");
		fileTagSet = baseDir + "/" + json.getValue("tagset");

		tagSet = new TagSet();
		tagSet.init(fileTagSet, TagSet.TAG_SET_KAIST);

		connection = new Connection();
		connection.init(fileConnections, tagSet.getTagCount(), tagSet);

		connectionNot = new ConnectionNot();
		connectionNot.init(fileConnectionsNot, tagSet);

		analyzedDic = new AnalyzedDic();
		analyzedDic.readDic(fileDicAnalyzed);

		systemDic = new Trie(Trie.DEFAULT_TRIE_BUF_SIZE_SYS);
		systemDic.read_dic(fileDicSystem, tagSet);

		userDic = new Trie(Trie.DEFAULT_TRIE_BUF_SIZE_USER);
		userDic.read_dic(fileDicUser, tagSet);

		numDic = new NumberDic();
		simti = new Simti();
		simti.init();
		eojeolList = new LinkedList<Eojeol>();
		
		chart = new MorphemeChart(tagSet, connection, systemDic, userDic, numDic, simti, eojeolList);
		
		postProc = new PostProcessor();
	}

	@Override
	public void shutdown() {
	}
}
