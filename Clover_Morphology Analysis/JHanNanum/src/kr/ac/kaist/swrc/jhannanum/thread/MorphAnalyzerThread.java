/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.MorphAnalyzer;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class MorphAnalyzerThread extends Thread {
	MorphAnalyzer ma = null;
	LinkedBlockingQueue<PlainSentence> in;
	LinkedBlockingQueue<SetOfSentences> out;
	
	public MorphAnalyzerThread(MorphAnalyzer ma, LinkedBlockingQueue<PlainSentence> in, LinkedBlockingQueue<SetOfSentences> out) {
		this.ma = ma;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		PlainSentence ps = null;
		SetOfSentences sos = null;
		
		try {
			while (true) {
				ps = in.take();
				
				if ((sos = ma.morphAnalyze(ps)) != null) {
					out.add(sos);
				}
			}
		} catch (InterruptedException e) {
		}
	}

}
