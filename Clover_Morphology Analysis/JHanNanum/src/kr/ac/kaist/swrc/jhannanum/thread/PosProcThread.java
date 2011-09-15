/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.thread;

import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.PosProcessor;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PosProcThread extends Thread {
	PosProcessor posProcessor = null;
	LinkedBlockingQueue<Sentence> in;
	LinkedBlockingQueue<Sentence> out;
	
	public PosProcThread(PosProcessor posProcessor, LinkedBlockingQueue<Sentence> in, LinkedBlockingQueue<Sentence> out) {
		this.posProcessor = posProcessor;
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		Sentence sent = null;
		
		try {
			while (true) {
				sent = in.take();
				
				if ((sent = posProcessor.doProcess(sent)) != null) {
					out.add(sent);
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
