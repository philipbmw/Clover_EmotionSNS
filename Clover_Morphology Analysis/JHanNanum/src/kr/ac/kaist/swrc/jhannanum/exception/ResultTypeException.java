package kr.ac.kaist.swrc.jhannanum.exception;

public class ResultTypeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private static final String[] objName = {null, "PlainSentence", "SetOfSentences", "Sentence"};
	
	private int phase = 0;
	
	public ResultTypeException(int phase) {
		this.phase = phase;
	}
	
	@Override
	public String getMessage() {
		return "The workflow ends in phase-" + phase + " so '" + objName[phase] + "' is required to store the result properly.";
	}
}
