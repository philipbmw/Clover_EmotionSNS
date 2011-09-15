/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.SetOfSentences;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.MorphemeProcessor;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class MorphemeProcThread extends Thread {
	MorphemeProcessor morphProcessor = null;
	LinkedBlockingQueue<SetOfSentences> in;
	LinkedBlockingQueue<SetOfSentences> out;
	
	public MorphemeProcThread(MorphemeProcessor morphProcessor, LinkedBlockingQueue<SetOfSentences> in, LinkedBlockingQueue<SetOfSentences> out) {
		this.morphProcessor = morphProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		SetOfSentences sos = null;
		
		try {
			while (true) {
				sos = in.take();
				
				if ((sos = morphProcessor.doProcess(sos)) != null) {
					out.add(sos);
				}
			}
		} catch (InterruptedException e) {
		}
	}

}
