/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.PosTagger;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PosTaggerThread extends Thread {
	PosTagger tagger = null;
	LinkedBlockingQueue<SetOfSentences> in;
	LinkedBlockingQueue<Sentence> out;
	
	public PosTaggerThread(PosTagger tagger, LinkedBlockingQueue<SetOfSentences> in, LinkedBlockingQueue<Sentence> out) {
		this.tagger = tagger;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		SetOfSentences sos = null;
		Sentence sent = null;
		
		try {
			while (true) {
				sos = in.take();
				
				if ((sent = tagger.tagPOS(sos)) != null) {
					out.add(sent);
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
