/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.util.ArrayList;
import java.util.Iterator;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.share.Code;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PostProcessor {
	private String HA = null;
	private String AR = null;
	private String A_ = null;
	private String PV = null;
	private String XEU = null;
	private String DOB = null;
	private String GOB = null;
	private String EU = null;
	private String SU = null;
	private String NU = null;

	public PostProcessor() {
		HA = Code.toTripleString("��");
		AR = Code.toTripleString("��");
		A_ = Code.toTripleString("��");
		PV = Code.toTripleString("������");
		XEU = Code.toTripleString("���߾�ũƮ");
		DOB = Code.toTripleString("��");
		GOB = Code.toTripleString("��");
		EU = Code.toTripleString("��");
		SU = Code.toTripleString("����");
		NU = Code.toTripleString("�´�");
	}
	
	public SetOfSentences doPostProcessing(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		Iterator<Eojeol[]> iter = eojeolSetArray.iterator();
		
		while (iter.hasNext()) {
			Eojeol[] eojeolSet = iter.next();
			String prevMorph = "";
			
			for (int i = 0; i < eojeolSet.length; i++) {
				Eojeol eojeol = eojeolSet[i];
				String[] morphemes = eojeol.getMorphemes();
				String[] tags = eojeol.getTags();
				
				for (int j = 0; j < eojeol.length; j++) {
					String tri = Code.toTripleString(morphemes[j]);
					if (tags[j].startsWith("e")) {
						int prevLen = prevMorph.length();
						
						if (tri.startsWith(A_)) {		/* �� -> �� */
							if (prevLen >= 4 && prevMorph.charAt(prevLen-1) == EU.charAt(1) && !isXEU(prevMorph.charAt(prevLen-2)) && ((Code.isJungseong(prevMorph.charAt(prevLen-3)) && isPV(prevMorph.charAt(prevLen-3))) || (Code.isJongseong(prevMorph.charAt(prevLen-3)) && isPV(prevMorph.charAt(prevLen-4))))) {
								morphemes[j] = Code.toString(AR.toCharArray());
							} else if (prevLen >= 3 && prevMorph.charAt(prevLen-1) == DOB.charAt(2) && (prevMorph.substring(prevLen-3).equals(DOB) == false || prevMorph.substring(prevLen-3).equals(GOB) == false)) {
								// ���ұ�Ģ ������� ����
							} else if (prevLen>=2 && prevMorph.substring(prevLen-2).equals(HA)) {
							} else if (prevLen>=2 && ( (Code.isJungseong(prevMorph.charAt(prevLen-1)) && isPV(prevMorph.charAt(prevLen-1))) || (Code.isJongseong(prevMorph.charAt(prevLen-1)) && isPV(prevMorph.charAt(prevLen-2))) )) {	// ������ or ����
								morphemes[j] = Code.toString(AR.toCharArray());
							}
						} else if (tri.startsWith(EU.substring(0, 2)) || tri.startsWith(SU.substring(0, 4)) || tri.startsWith(NU.substring(0, 4))) {
							/* ������ Ż�� */
							if (prevLen >= 2 && (Code.isJungseong(prevMorph.charAt(prevLen-1)) || prevMorph.charAt(prevLen-1) == 0x11AF)) {
								morphemes[j] = Code.toString(tri.substring(2).toCharArray());
							}
						}
					}
					
					prevMorph = Code.toTripleString(morphemes[j]);
				}
			}
		}

		return sos;
	}

	private boolean isPV(char c) {
		if (PV.indexOf(c) == -1) {
			return false;
		}
		return true;
	}

	private boolean isXEU(char c) {
		if (XEU.indexOf(c) == -1) {
			return false;
		}
		return true;
	}
}
