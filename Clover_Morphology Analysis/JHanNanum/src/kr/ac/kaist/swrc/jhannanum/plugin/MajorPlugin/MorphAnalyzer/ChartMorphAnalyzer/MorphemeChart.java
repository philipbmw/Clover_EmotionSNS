package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.util.ArrayList;
import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.SegmentPosition.Position;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.Trie.INFO;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.Trie.TNODE;
import kr.ac.kaist.swrc.jhannanum.share.Code;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

public class MorphemeChart {
	/**
	 * 형태소 분석 Chart Node.
	 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
	 */
	public class Morpheme {
		/** 형태소 태그 */
		int tag;
		
		/** 음운 */
		int phoneme;
		
		/** 다음 세그먼트 위치 */
		int nextPosition;
		
		/** 품사 제한 */
		int nextTagType;
		
		/** 처리 단계 */
		int state;
		
		/** 연결된 형태소 수 */
		int connectionCount;
		
		/** 연결된 형태소 리스트 */
		int[] connection = new int[MAX_MORPHEME_CONNECTION];
		
		String str = "";
	}
	final static private String CHI_REPLACE = "HAN_CHI";
	final static private String ENG_REPLACE = "HAN_ENG";
	
	private LinkedList<String> chiReplacementList = null;
	private LinkedList<String> engReplacementList = null;
	
	private int engReplaceIndex = 0;
	private int chiReplaceIndex = 0;
	
	final private static int MAX_MORPHEME_CONNECTION = 30;
	
	final private static int MAX_MORPHEME_CHART = 2046;
	final private static int MORPHEME_STATE_INCOMPLETE = 2;
	final private static int MORPHEME_STATE_SUCCESS = 1;

	final private static int MAX_CANDIDATE_NUM = 100000;
	
	final private static int MORPHEME_STATE_FAIL = 0;
	public Morpheme[] chart = null;
	
	public int chartEnd = 0;
	private TagSet tagSet = null;
	private Connection connection = null;
	
	private SegmentPosition sp = null;
	private String bufString = "";
	
	private int[] segmentPath = new int[SegmentPosition.MAX_SEGMENT];
	private Exp exp = null; 
	private Trie systemDic = null;
	private Trie userDic = null;
	private NumberDic numDic = null;
	
	private Simti simti = null;
	private int printResultCnt = 0;
	
	private LinkedList<Eojeol> resEojeols = null;
	private ArrayList<String> resMorphemes = null;
	private ArrayList<String> resTags = null;
	
	public MorphemeChart(TagSet tagSet, Connection connection, Trie systemDic, Trie userDic, NumberDic numDic, Simti simti, LinkedList<Eojeol> resEojeolList) {
		chart = new Morpheme[MAX_MORPHEME_CHART];
		for (int i = 0; i < MAX_MORPHEME_CHART; i++) {
			chart[i] = new Morpheme();
		}
		
		this.sp = new SegmentPosition();
		this.tagSet = tagSet;
		this.connection = connection;
		this.exp = new Exp(this, tagSet);
		this.systemDic = systemDic;
		this.userDic = userDic;
		this.numDic = numDic;
		this.simti = simti;
		this.resEojeols = resEojeolList;
		
		resMorphemes = new ArrayList<String>();
		resTags = new ArrayList<String>();
		
		chiReplacementList = new LinkedList<String>();
		engReplacementList = new LinkedList<String>();
	}
	
	/**
	 * 새로운 형태소를 차트에 추가한다.
	 * @param tag	태그 식별자
	 * @param phoneme	음소
	 * @param nextPosition	다음 형태소의 인덱스
	 * @param nextTagType	다음 형태소 태그의 타입
	 * @return	차트의 마지막 인덱스
	 */
	public int addMorpheme(int tag, int phoneme, int nextPosition, int nextTagType) {
		chart[chartEnd].tag = tag;
		chart[chartEnd].phoneme = phoneme;
		chart[chartEnd].nextPosition = nextPosition;
		chart[chartEnd].nextTagType = nextTagType;
		chart[chartEnd].state = MORPHEME_STATE_INCOMPLETE;
		chart[chartEnd].connectionCount = 0;
		return chartEnd++;
	}
	
	public int altSegment(String str) {
		int prev = 0;
		int next = 0;
		int match;
		int len;
		int to;
		
		len = str.length();
		
		String rev = "";
		for (int i = len - 1; i >= 0; i--) {
			rev += str.charAt(i);
		}
		
		char[] revStrArray = rev.toCharArray();
		
		match = simti.search(revStrArray);
		to = simti.fetch(rev.substring(0, match).toCharArray());
		
		for (int i = 0; i < str.length(); i++) {
			if (len <= match) {
				break;
			}
			next = sp.addPosition(str.charAt(i));
			if (prev != 0) {
				sp.setPositionLink(prev, next);
			}
			
			simti.insert(rev.substring(0, len).toCharArray(), next);
			prev = next;
			len--;
		}
		
		if (prev != 0) {
			sp.setPositionLink(prev, to);
		}
		
		return simti.fetch(revStrArray);
	}
	
	public int analyze() {
		int res = 0;
		
		res = analyze(0, TagSet.TAG_TYPE_ALL);

		if (res > 0) {
			return res;
		} else {
			return analyzeUnknown();
		}
	}
	
	private int analyze(int chartIndex, int tagType) {
		int from, to;
		int i,j,x, y;
		int mp;
		char c;
		int nc_idx;
		TNODE node;
		LinkedList<INFO> infoList = null;
		INFO info = null;
		
		// System.err.println("analyze call start: " + (++call_count) + ", chart_id: " + chartIndex + ", ttype: " + tagType);
		
		int sidx = 1;
		int uidx = 1;
		int nidx = 1;
		Position fromPos = null;
		Position toPos = null;
		Morpheme morph = chart[chartIndex]; 
		from = morph.nextPosition;
		fromPos = sp.getPosition(from);
		// System.out.println("*** 1 " + from);
		
		switch(sp.getPosition(from).state) {
		default:
			// System.out.println("*** 2 " + from);
			// System.err.println("analyze call end: " + (call_count--) + ", chart_id: " + chartIndex + ", ttype: " + tagType);
			return 0;
			
		// 사전 검색
		case SegmentPosition.SP_STATE_N:
			// System.out.println("*** 3 " + from);
			i = 0;
			bufString = "";
			
			// 입력된 어절에 대해서 최소 단위의 형태소부터 증가시키면서 모든 조합에 대한 사전을 검색한다.
			for (to = from; to != SegmentPosition.POSITION_START_KEY; to = sp.nextPosition(to)) {
				toPos = sp.getPosition(to);
				c = toPos.key;
				
				if (sidx != 0) {
					sidx = systemDic.node_look(c, sidx);
				}
				if (uidx != 0) {
					uidx = userDic.node_look(c, uidx);
				}
				if (nidx != 0) {
					nidx = numDic.node_look(c, nidx);
				}
				
				toPos.sIndex = sidx;
				toPos.uIndex = uidx;
				toPos.nIndex = nidx;
				
				bufString += c;
				segmentPath[i++] = to;
			}
			
			nidx = 0;
			
			for ( ; i > 0; i--) {
				to = segmentPath[i-1];
				toPos = sp.getPosition(to);
				
				// 시스템 사전 검색하여 저장
				if (toPos.sIndex != 0) {
					node = systemDic.get_node(toPos.sIndex);
					if ((infoList = node.info_list) != null) {
						for (j = 0; j < infoList.size(); j++) {
							info = infoList.get(j);

							nc_idx = addMorpheme(info.tag, info.phoneme, sp.nextPosition(to), 0);
							chart[nc_idx].str = bufString.substring(0, i);
							fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						}
					}
				}
				
				// 사용자 사전 검색하여 저장
				if (toPos.uIndex != 0) {
					node = userDic.get_node(toPos.uIndex);
					if ((infoList = node.info_list) != null) {
						for (j = 0; j < infoList.size(); j++) {
							info = infoList.get(j);
							nc_idx = addMorpheme(info.tag, info.phoneme, sp.nextPosition(to), 0);
							chart[nc_idx].str = bufString.substring(0, i);
							fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						}
					}
				}
				
				// 숫자 사전 검색하여 저장
				if (nidx == 0 && toPos.nIndex != 0) {
					if (numDic.isNum(toPos.nIndex)) {
						nc_idx = addMorpheme(tagSet.numTag, TagSet.PHONEME_TYPE_ALL, sp.nextPosition(to), 0);
						chart[nc_idx].str = bufString.substring(0, i);
						fromPos.morpheme[fromPos.morphCount++] = nc_idx;
						nidx = toPos.nIndex;
					} else {
						nidx = 0;
					}
				}
			}
			
			fromPos.state = SegmentPosition.SP_STATE_D; 

		// 음운 변화에 의한 확장
		case SegmentPosition.SP_STATE_D:
			// System.out.println("*** 4 " + from);
			exp.prule(from, morph.str, bufString, sp);
			sp.getPosition(from).state = SegmentPosition.SP_STATE_R;
			
		case SegmentPosition.SP_STATE_R:
			// System.out.println("*** 5 " + from);
			/*
			 * 아래의 경우에 이곳에서 시작
			 * - 미등록 처리하는 경우
			 * - 재귀 호출이 있는 경우
			 */
			
//			for (int k = 0; k < fromPos.morphCount; k++) {
//				 System.out.print(tagSet.getTagName(chart[fromPos.morpheme[k]].tag) + "," + Code.toString(chart[fromPos.morpheme[k]].str.toCharArray()) + " ");
//			}
//			 System.out.println();
			x = 0;
			for (i = 0; i < fromPos.morphCount; i++) {
				mp = fromPos.morpheme[i];
				
				/*
				 * '습니다' 재귀호출을 막기 위한 부분
				 * 완벽하지 않은 처리이므로 이후 보정이 필요함
				 */
				if (tagSet.checkTagType(tagType, chart[mp].tag) == false) {
					continue;
				}
				
				/*
				 * '바보니' 에서 '니'가 중복 처리되는 것을 막는 부분
				 * 예) 바보+니 | 바보+이
				 */
				if (chart[mp].state == MORPHEME_STATE_INCOMPLETE) {
					y = analyze(mp, chart[mp].nextTagType);
					x += y;
					
					if (y != 0) {
						chart[mp].state = MORPHEME_STATE_SUCCESS;
					} else {
						chart[mp].state = MORPHEME_STATE_FAIL;
					}
				}
				else {
					x += chart[mp].connectionCount;
				}
			}
			
			if (x == 0) {
				if (tagType == TagSet.TAG_TYPE_ALL) {
					// 품사의 일부분만 본 경우는 나중에 다시 처리해야 함
					fromPos.state = SegmentPosition.SP_STATE_F;
				}
				// System.err.println("analyze call end: " + (call_count--) + ", chart_id: " + chartIndex + ", ttype: " + tagType);
				return 0;
			}
			
			if (tagType == TagSet.TAG_TYPE_ALL) {
				fromPos.state = SegmentPosition.SP_STATE_M;
			}
			
		// 결합 규칙 검사
		case SegmentPosition.SP_STATE_M:
			// System.out.println("*** 6 " + from);
			// System.out.println("@@@@@@@@@@@ " + fromPos.morphCount);
			for (i = 0; i < fromPos.morphCount; i++) {
				mp = fromPos.morpheme[i];
				
				if (chart[mp].state == MORPHEME_STATE_SUCCESS &&
					connection.checkConnection(
							tagSet,
							morph.tag,
							chart[mp].tag,
							morph.str.length(),
							chart[mp].str.length(),
							morph.nextTagType)) {
					morph.connection[morph.connectionCount++] = mp;
				}
			}
		}
		// System.err.println("analyze call end: " + (call_count--) + ", chart_id: " + chartIndex + ", ttype: " + tagType);
		return morph.connectionCount;
	}
	
	public int analyzeUnknown() {
		int i;
		int nc_idx;
		
		bufString = "";
		
		Position pos_1 = sp.getPosition(1);
		
		for (i = 1; i != 0; i = sp.nextPosition(i)) {
			Position pos = sp.getPosition(i);

			bufString += pos.key;
			
			if (Code.isChoseong(pos.key)) {
				continue;
			}
			
			nc_idx = addMorpheme(tagSet.unkTag, TagSet.PHONEME_TYPE_ALL, sp.nextPosition(i), TagSet.TAG_TYPE_ALL);
			chart[nc_idx].str = bufString;
			
			pos_1.morpheme[pos_1.morphCount++] = nc_idx;
			pos_1.state = SegmentPosition.SP_STATE_R;
		}
		
		chart[0].connectionCount = 0;
		
		return analyze(0, 0);
	}
	
	/**
	 * 해당 형태소 정보가 차트에 등록되어 있는지 확인한다.
	 * @param morpheme	형태소의 차트 인덱스
	 * @param morphemeLen	형태소의 차트 인덱스 배열의 길이 
	 * @param tag	태그 식별자
	 * @param phoneme	음소
	 * @param nextPosition	다음 형태소의 인덱스
	 * @param nextTagType	다음 형태소 태그의 타입
	 * @param str	해당 문자열
	 * @return	true: 차트에 있음, false: 차트에 없음
	 */
	public boolean checkChart(int[] morpheme, int morphemeLen, int tag, int phoneme, int nextPosition, int nextTagType, String str) {
		for (int i = 0; i < morphemeLen; i++) {
			Morpheme morph = chart[morpheme[i]];
			if (morph.tag == tag &&
					morph.phoneme == phoneme &&
					morph.nextPosition == nextPosition &&
					morph.nextTagType == nextTagType &&
					morph.str.equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	public void getResult() {
		
		printResultCnt = 0;
		printChart(0);
	}
	
	/**
	 * 형태소 차트를 초기화한다.
	 */
	public void init(String word) {
		simti.init();
		word = preReplace(word);
		sp.init(Code.toTripleString(word), simti);
		
		chartEnd = 0;
		Position p = sp.getPosition(0);
		p.morpheme[p.morphCount++] = chartEnd;
		chart[chartEnd].tag = tagSet.iwgTag;
		chart[chartEnd].phoneme = 0;
		chart[chartEnd].nextPosition = 1;
		chart[chartEnd].nextTagType = 0;
		chart[chartEnd].state = MORPHEME_STATE_SUCCESS;
		chart[chartEnd].connectionCount = 0;
		chart[chartEnd].str = "";
		chartEnd++;
	}
	
	public void phonemeChange(int from, String front, String back, int ftag, int btag, int phoneme) {
		TNODE node = null;
		int size = 0;
		boolean x, y;
		int next;
		int nc_idx;
		
		// 앞 부분 사전 검색
		node = systemDic.fetch(front.toCharArray());
		if (node != null && node.info_list != null) {
			size = node.info_list.size();
		}
		
		Position pos = sp.getPosition(from);
		
		for (int i = 0; i < size; i++) {
			INFO info = node.info_list.get(i);
			
			// 앞 부분 품사 비교			
			x = tagSet.checkTagType(ftag, info.tag);
			
			// 앞 부분 음운 비교
			y = tagSet.checkPhonemeType(phoneme, info.phoneme);
			
			if (x && y) {
				next = altSegment(back);
				
				if (checkChart(pos.morpheme, pos.morphCount, info.tag, info.phoneme, next, btag, front) == false) {
					nc_idx = addMorpheme(info.tag, info.phoneme, next, btag);
					chart[nc_idx].str = front;
					pos.morpheme[pos.morphCount++] = nc_idx;
				} else {
					System.err.println("phonemeChange: exit");
					System.exit(0);
				}
			}
		}
	}
	
	private void printChart(int chartIndex) {
		int i;
		Morpheme morph = chart[chartIndex];
		int engCnt = 0;
		int chiCnt = 0;

		if (chartIndex == 0) {
			for (i = 0; i < morph.connectionCount; i++) {
				resMorphemes.clear();
				resTags.clear();
				printChart(morph.connection[i]);
			}
		} else {
//			String tmp = morph.str;
//			if (morph.str.indexOf('/') != -1) {
//				tmp = tmp.replaceAll("/", "\\/");
//			}
//			if (morph.str.indexOf('+') != -1) {
//				tmp = tmp.replaceAll("\\+", "\\\\+");
//			}
//			printBuf += tmp;
			String morphStr = Code.toString(morph.str.toCharArray());
			int idx = 0;
			engCnt = 0;
			chiCnt = 0;
			while (idx != -1) {
				if ((idx = morphStr.indexOf(ENG_REPLACE)) != -1) {
					engCnt++;
					morphStr = morphStr.replaceFirst(ENG_REPLACE, engReplacementList.get(engReplaceIndex++));
				} else if ((idx = morphStr.indexOf(CHI_REPLACE)) != -1) {
					chiCnt++;
					morphStr = morphStr.replaceFirst(CHI_REPLACE, chiReplacementList.get(chiReplaceIndex++));
				}
			}
			
			resMorphemes.add(morphStr);
			resTags.add(tagSet.getTagName(morph.tag));

			for (i = 0; i < morph.connectionCount && printResultCnt < MAX_CANDIDATE_NUM; i++) {
				if (morph.connection[i] == 0) {
					String[] mArray = resMorphemes.toArray(new String[0]);
					String[] tArray = resTags.toArray(new String[0]);
					resEojeols.add(new Eojeol(mArray, tArray));
					
					printResultCnt++;
				} else {
					printChart(morph.connection[i]);
				}
			}
			
			resMorphemes.remove(resMorphemes.size() - 1);
			resTags.remove(resTags.size() - 1);
			if (engCnt > 0) {
				engReplaceIndex -= engCnt;
			}
			if (chiCnt > 0) {
				chiReplaceIndex -= chiCnt;
			}
		}
	}
	
	/**
	 * 차트 내용을 출력한다.
	 */
	public void printMorphemeAll() {
		 System.err.println("chartEnd: " + chartEnd);
		for (int i = 0; i < chartEnd; i++) {
			System.err.println("chartID: " + i);
			System.err.format("%s/%s.%s nextPosition=%c nextTagType=%s state=%d ",
					Code.toString(chart[i].str.toCharArray()),
					tagSet.getTagName(chart[i].tag),
					tagSet.getIrregularName(chart[i].phoneme),
					Code.toCompatibilityJamo(sp.getPosition(chart[i].nextPosition).key),
					tagSet.getTagName(chart[i].nextTagType),
					chart[i].state);
			System.err.print("connection=");
			for (int j = 0; j < chart[i].connectionCount; j++) {
				 System.err.print(chart[i].connection[j] + ", ");
			}
			System.err.println();
		}
	}
	
	private String preReplace(String str) {
		String result = "";
		boolean engFlag = false;
		boolean chiFlag = false;
		String buf = "";
		
		engReplacementList.clear();
		chiReplacementList.clear();
		engReplaceIndex = 0;
		chiReplaceIndex = 0;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (((c >= 'a' && c <= 'z') || c >= 'A' && c <= 'Z')) {
				// 영어
				if (engFlag) {
					buf += c;
				} else {
					if (engFlag) {
						engFlag = false;
						engReplacementList.add(buf);
						buf = "";
					}
					result += ENG_REPLACE;
					buf += c;
					engFlag = true;
				}

			} else if (((c >= 0x2E80 && c <= 0x2EFF) || (c >= 0x3400 && c <= 0x4DBF)) || (c >= 0x4E00 && c < 0x9FBF) ||
					(c >= 0xF900 && c <= 0xFAFF) && chiFlag) {
				// 한자
				if (chiFlag) {
					buf += c;
				} else {
					if (chiFlag) {
						chiFlag = false;
						chiReplacementList.add(buf);
						buf = "";
					}
					result += CHI_REPLACE;
					buf += c;
					chiFlag = true;
				}
			} else {
				result += c;
				if (engFlag) {
					engFlag = false;
					engReplacementList.add(buf);
					buf = "";
				}
				if (chiFlag) {
					chiFlag = false;
					chiReplacementList.add(buf);
					buf = "";
				}
			}
		}
		if (engFlag) {
			engReplacementList.add(buf);
		}
		if (chiFlag) {
			chiReplacementList.add(buf);
		}
		return result;
	}
}
