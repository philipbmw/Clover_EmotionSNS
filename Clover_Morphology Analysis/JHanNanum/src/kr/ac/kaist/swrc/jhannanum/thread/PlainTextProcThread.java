/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.PlainSentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.PlainTextProcessor;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PlainTextProcThread extends Thread {
	PlainTextProcessor plainTextProcessor = null;
	LinkedBlockingQueue<PlainSentence> in;
	LinkedBlockingQueue<PlainSentence> out;
	
	public PlainTextProcThread(PlainTextProcessor plainTextProcessor, LinkedBlockingQueue<PlainSentence> in, LinkedBlockingQueue<PlainSentence> out) {
		this.plainTextProcessor = plainTextProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		PlainSentence ps = null;
		
		try {
			while (true) {
				ps = in.take();
				
				if ((ps = plainTextProcessor.doProcess(ps)) != null) {
					out.add(ps);
				}
				
				while (plainTextProcessor.hasRemainingData()) {
					if ((ps = plainTextProcessor.doProcess(null)) != null) {
						out.add(ps);
					}
				}
				
				if ((ps = plainTextProcessor.flush()) != null) {
					out.add(ps);
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
