/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor;

import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class NounExtractor implements PosProcessor {
	private LinkedList<String> nounMorphemes = null;
	private LinkedList<String> nounTags = null;
	

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {
		nounMorphemes = new LinkedList<String>();
		nounTags = new LinkedList<String>();
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public Sentence doProcess(Sentence st) {
		Eojeol[] eojeols = st.getEojeols();
		
		for (int i = 0; i < eojeols.length; i++) {
			String[] morphemes = eojeols[i].getMorphemes();
			String[] tags = eojeols[i].getTags();
			nounMorphemes.clear();
			nounTags.clear();
			
			for (int j = 0; j < tags.length; j++) {
				char c = tags[j].charAt(0);
				//char c1 = tags[j].charAt(1);
				if (c == 'n') { //  n을 p로 바꿈. 형요사만
					//if (c == 'c'){
					nounMorphemes.add(morphemes[j]);
					nounTags.add(tags[j]);
					//}
				} 
				else if (c == 'p') {  //f->i 로 변경  ii는 감탄사.
					nounMorphemes.add(morphemes[j]);
					nounTags.add(tags[j]);
				}
				else if (c == 'i') {  //f->i 로 변경  ii는 감탄사.
					nounMorphemes.add(morphemes[j]);
					nounTags.add("ii");
				} else if (c == 'm') {  //f->i 로 변경  ii는 감탄사.
					nounMorphemes.add(morphemes[j]);
					nounTags.add(tags[j]);
				}
			}
			
			eojeols[i].setMorphemes(nounMorphemes.toArray(new String[0]));
			eojeols[i].setTags(nounTags.toArray(new String[0]));
		}
		
		st.setEojeols(eojeols);
		//System.out.println("어절 test");
		return st;
	}
	

}
