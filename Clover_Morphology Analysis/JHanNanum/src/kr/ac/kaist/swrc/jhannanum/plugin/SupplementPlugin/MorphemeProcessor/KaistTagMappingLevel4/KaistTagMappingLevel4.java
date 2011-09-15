/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.KaistTagMappingLevel4;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;
import kr.ac.kaist.swrc.jhannanum.share.TagMapper;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class KaistTagMappingLevel4 implements MorphemeProcessor {
	final private int TAG_LEVEL = 4;
	private HashMap<String,Eojeol> dupFilterMap = null;
	
	
	public KaistTagMappingLevel4() {
		dupFilterMap = new HashMap<String,Eojeol>();
	}
	@Override
	public SetOfSentences doProcess(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		ArrayList<Eojeol[]> resultSetArray = new ArrayList<Eojeol[]>();
		
		int len = eojeolSetArray.size();
		
		for (int pos = 0; pos < len; pos++) {
			Eojeol[] eojeolSet = eojeolSetArray.get(pos);
			dupFilterMap.clear();
			
			for (int i = 0; i < eojeolSet.length; i++) {
				String[] tags = eojeolSet[i].getTags();
				
				for (int j = 0; j < tags.length; j++) {
					tags[j] = TagMapper.getKaistTagOnLevel(tags[j], TAG_LEVEL);
				}
				
				String key = eojeolSet[i].toString();
				if (!dupFilterMap.containsKey(key)) {
					dupFilterMap.put(key, eojeolSet[i]);
				}
			}
			if (eojeolSet.length != dupFilterMap.size()) {
				resultSetArray.add(dupFilterMap.values().toArray(new Eojeol[0]));
			} else {
				resultSetArray.add(eojeolSet);
			}
		}
		
		sos.setEojeolSetArray(resultSetArray);
		return sos;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws Exception {
		
	}

	@Override
	public void shutdown() {
		
	}
}
