/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

import java.util.ArrayList;
import java.util.Iterator;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.PosTagger;
import kr.ac.kaist.swrc.jhannanum.share.JSONReader;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class HMMTagger implements PosTagger {
	private class MNode {
		Eojeol eojeol;
		String wp_tag;
		double prob_wt;
		double prob;
		int backptr;		/* for viterbi */
		int sibling;
	}
	
	private class WPhead {
		int mnode;
	}
	
	public static int MAXLINE = 10000;
	public static int TRUE = 1;
	public static int FALSE = 0;
	public static double SF = -4.60517018598809136803598290936873;		/* log 0.01 */
	
	public static String EOS = "eos";

	public static String BNK = "bnk";

	public WPhead[] wp = null;
	public int wp_end = 0;

	public MNode[] mn = null;
	public int mn_end = 0;

	public ProbabilityDBM pwt_pos_tf = null;
	public ProbabilityDBM ptt_pos_tf = null;
	public ProbabilityDBM ptt_wp_tf = null;

	public String PWT_POS_TDBM_FILE;	/* 형태소 나왔을 때 단어 발생확률 */
	public String PTT_POS_TDBM_FILE;	/* 형태소간 전이 확률 */
	public String PTT_WP_TDBM_FILE;	/* 어절간 전이 확률 */

	public boolean view = false;

	public String wtag = null;

	final static double PCONSTANT = -20.0;
	final static double LAMBDA = 0.9;

	final static double Lambda1 = LAMBDA;
	final static double Lambda2 = 1.0 - LAMBDA;
	
	@Override
	public Sentence tagPOS(SetOfSentences sos) {
		int v = 0, prev_v = 0, w = 0;
		ArrayList<String> plainEojeolArray = sos.getPlainEojeolArray();
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		
		/* 초기화를 한다. */
		reset();
		
		Iterator<String> plainEojeolIter = plainEojeolArray.iterator();
		for (Eojeol[] eojeolSet : eojeolSetArray) {
			String plainEojeol = null;
			if (plainEojeolIter.hasNext()) {
				plainEojeol = plainEojeolIter.next();
			} else {
				break;
			}
			w = new_wp(plainEojeol);
			
			for (int i = 0; i < eojeolSet.length; i++) {
				String now_tag;
				double probability;
	
				now_tag = PhraseTag.getPhraseTag(eojeolSet[i].getTags());
				probability = compute_wt(eojeolSet[i]);
				
				v = new_mnode(eojeolSet[i], now_tag, probability);
				if (i == 0) {
					wp[w].mnode = v;
					prev_v = v;
				} else {
					mn[prev_v].sibling = v;
					prev_v = v;
				}
			}
		}
		
		/* 비터비를 돌려 결과를 얻어낸다. */
		return end_sentence(sos);
	}

	@Override
	public void initialize(String baseDir, String configFile) throws Exception  {
		wp = new WPhead[5000];
		for (int i = 0; i < 5000; i++) {
			wp[i] = new WPhead();
		}
		wp_end = 1;

		mn = new MNode[10000];
		for (int i = 0; i < 10000; i++) {
			mn[i] = new MNode();
		}
		mn_end = 1;

		JSONReader json = new JSONReader(configFile);
		PWT_POS_TDBM_FILE = baseDir + "/" + json.getValue("pwt.pos");
		PTT_POS_TDBM_FILE = baseDir + "/" + json.getValue("ptt.pos");
		PTT_WP_TDBM_FILE = baseDir + "/" + json.getValue("ptt.wp");
			
		pwt_pos_tf = new ProbabilityDBM(PWT_POS_TDBM_FILE);
		ptt_wp_tf = new ProbabilityDBM(PTT_WP_TDBM_FILE);
		ptt_pos_tf = new ProbabilityDBM(PTT_POS_TDBM_FILE);
	}
	
	@Override
	public void shutdown() {

	}

	/**
	 * P(T_i, W_i)의 확률을 계산한다. T_i값은 여기서는 필요없다.
	 * @param eojeol
	 * @return
	 */
	private double compute_wt(Eojeol eojeol)
	{
		double current=0.0, tbigram, tunigram, lexicon;

		String tag;
		String bitag;
		String oldtag;

		tag = eojeol.getTag(0);

		/* P(t1|t0) 확률 계산 */
		bitag = "bnk-" + tag;				/* bnk_ncn */

		double[] prob = null;

		if ((prob = ptt_pos_tf.get(bitag)) != null) {
			/* current = P(t1|t0) */
			tbigram = prob[0];
		} else {
			/* current = P(t1|t0) = 0.01 */
			tbigram = PCONSTANT;
		}

		/* P(t1) 확률 계산 : interpolation에서 사용하기 위하여 */
		if ((prob = ptt_pos_tf.get(tag)) != null) {
			/* current = P(t1) */
			tunigram = prob[0];
		} else { 
			/* current = P(t1) = 0.01 */
			tunigram = PCONSTANT;
		}

		/* P(w|t) 확률 계산 */
		if ((prob = pwt_pos_tf.get(eojeol.getMorpheme(0) + "/" + tag)) != null) {
			/* current *= P(w|t1) */
			lexicon = prob[0];
		} else {
			/* current = P(w|t1) = 0.01 */
			lexicon = PCONSTANT;
		}

		/*                              
		 * 현재 확률 = P(w|t1) * P(t1|t0)
		 *                                      
		 *          ~= P(w|t1) * (P(t1|t0))^Lambda1 * (P(t1))^Lambda2
		 *          (단, Lambda1 + Lambda2 = 1)
		 */ 
//		current = lexicon + Lambda1*tbigram + Lambda2*tunigram;

		/* 
		 * 현재 확률 = P(w|t1)/P(t1) * P(t1|t0)/P(t1)
		 */
//		current = lexicon - tunigram + tbigram - tunigram;

		/* 
		 * 현재 확률 = P(w|t1) * P(t1|t0)
		 */
//		current = lexicon + tbigram ;
		
		/* 
		 * 현재 확률 = P(w|t1) * P(t1|t0) / P(t1)
		 */
		current = lexicon + tbigram - tunigram;
		oldtag = tag;


		for (int i = 1; i < eojeol.length; i++) {
			tag = eojeol.getTag(i);

			/* P(t_i|t_i-1) 확률(bigram) 계산 */
			bitag = oldtag + "-" + tag;

			if ((prob = ptt_pos_tf.get(bitag)) != null) {
				tbigram = prob[0];
			} else { 
				tbigram=PCONSTANT;
			}

			/* P(w|t) 확률 계산 */
			if ((prob = pwt_pos_tf.get(eojeol.getMorpheme(i) + "/" + tag)) != null) {
				/* current *= P(w|t) */
				lexicon = prob[0];
			} else {
				lexicon = PCONSTANT;
			}

			/* P(t) 확률 계산 */
			if ((prob = ptt_pos_tf.get(tag)) != null) {
				/* current = P(t) */
				tunigram = prob[0];
			} else { 
				/* current = P(t)=0.01 */
				tunigram = PCONSTANT;
			}

//			current += lexicon - tunigram + tbigram - tunigram;
//			current += lexicon + tbigram;
			current += lexicon + tbigram - tunigram;

			oldtag = tag;
		}

		/* 마지막에 공백에 대해서 */
		bitag = tag + "-bnk";

		/* P(bnk|t_last) */
		if ((prob = ptt_pos_tf.get(bitag)) != null) {
			tbigram = prob[0];
		} else { 
			tbigram = PCONSTANT;
		}

		/* P(bnk) 확률 계산, 사실 상수화 시킬 수 있음.*/
		if ((prob = ptt_pos_tf.get("bnk")) != null) {
			/* current = P(bnk) */
			tunigram = prob[0];
		} else { 
			tunigram=PCONSTANT;
		}

		/* P(w|bnk) = 1, ln값은 0이 된다. */
//		current += 0 - tunigram + tbigram - tunigram;
//		current += 0 + tbigram;
		current += 0 + tbigram - tunigram;

		return current;
	}

	private Sentence end_sentence(SetOfSentences sos) {
		int i, j, k;

		/* 마지막 노드를 만들어 줘야 한다.*/
		i = new_wp(" ");
		wp[i].mnode = new_mnode(null, "SF", 0);

		/* 비터비를 돌린다. */
		for (i = 1; i < wp_end - 1; i++) {
			for (j = wp[i].mnode; j != 0; j = mn[j].sibling) { 
				for (k = wp[i+1].mnode; k != 0; k = mn[k].sibling) {
					update_prob_score(j, k);
				}
			}
		}

		i = sos.length;
		Eojeol[] eojeols = new Eojeol[i];
		for (k = wp[i].mnode; k != 0; k = mn[k].backptr) {
			eojeols[--i] = mn[k].eojeol;
		}

		return new Sentence(sos.getDocumentID(), sos.getSentenceID(), sos.isEndOfDocument(), sos.getPlainEojeolArray().toArray(new String[0]), eojeols);
	}

	private int new_mnode(Eojeol eojeol, String wp_tag, double prob) {
		mn[mn_end].eojeol = eojeol;
		mn[mn_end].wp_tag = wp_tag;
		mn[mn_end].prob_wt = prob;
		mn[mn_end].backptr = 0;
		mn[mn_end].sibling = 0;
		return mn_end++;
	}
	
	public int new_wp(String str) {
		wp[wp_end].mnode = 0;
		return wp_end++;
	}

	private void reset() {
		wp_end = 1;
		mn_end = 1;
	}

	public void update_prob_score(int from, int to) {
		double PTT;
		double[] prob = null;
		double P;

		/* 먼저 전이 확률 P(T_i,T_i-1) 을 구함 */
		prob = ptt_wp_tf.get(mn[from].wp_tag + "-" +mn[to].wp_tag);
		
		if (prob == null) {
			/* 0.01을 자연로그 취한 값. Smoothing Factor */
			PTT = SF;
		} else {
			PTT = prob[0];
		}
		
		/* 위의 확률을 P(T_i)로 나눠준다.*/
		prob = ptt_wp_tf.get(mn[to].wp_tag);
		
		if (prob != null) {
			PTT -= prob[0];
		}
//		
//		/* 위의 확률을 P(T_i-1)로 나눠준다.*/
//		prob = ptt_wp_tf.get(mn[from].wp_tag);
//		
//		if (prob != null) {
//			PTT -= prob[0];
//		}

		if (mn[from].backptr == 0) {
			mn[from].prob = mn[from].prob_wt;
		}

		/* 
		 * 앞에까지 확률 * 전이확률 * 현재 확률
		 * PTT = P(T_i|T_i-1) / P(T_i)
		 * mn[to].prob_wt = P(T_i, W_i)
 		 */
		P = mn[from].prob + PTT + mn[to].prob_wt;

		// 중간 노드 확률값을 보여주게 출력
//		System.out.format("P:%f\t%s(%d:%s):%f -> %f -> %s(%d:%s):%f\n", P, mn[from].eojeol, 
//				from, mn[from].wp_tag, mn[from].prob, PTT, 
//				mn[to].eojeol, to, mn[to].wp_tag, mn[to].prob_wt );
	
		if (mn[to].backptr == 0 || P > mn[to].prob) {
			mn[to].backptr = from;
			mn[to].prob = P; 
		}
	}
}