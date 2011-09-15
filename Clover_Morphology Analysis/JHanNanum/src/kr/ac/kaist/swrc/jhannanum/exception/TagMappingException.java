package kr.ac.kaist.swrc.jhannanum.exception;

public class TagMappingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String tag = null;
	
	public TagMappingException(String tag) {
		this.tag = tag;
	}
	
	@Override
	public String getMessage() {
		return "Failed to get a tag which is mapped with " + this.tag;
	}
}
