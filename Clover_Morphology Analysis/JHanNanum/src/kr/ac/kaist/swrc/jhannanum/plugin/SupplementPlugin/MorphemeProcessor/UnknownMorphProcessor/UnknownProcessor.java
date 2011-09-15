package kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;

public class UnknownProcessor implements MorphemeProcessor {
	@Override
	public SetOfSentences doProcess(SetOfSentences sos) {
		ArrayList<Eojeol[]> eojeolSetArray = sos.getEojeolSetArray();
		
		for (Eojeol[] eojeolSet : eojeolSetArray) {
			for (int i = 0; i < eojeolSet.length; i++){
				String[] tags = eojeolSet[i].getTags();
				for (int k = 0; k < tags.length; k++) {
					if (tags[k].equals("unk")) {
						tags[k] = "unkk";
					}
				}
			}
		}
		
		return sos;
	}

	@Override
	public void initialize(String baseDir, String configFile) throws FileNotFoundException, IOException {
		
	}

	@Override
	public void shutdown() {
	}
}