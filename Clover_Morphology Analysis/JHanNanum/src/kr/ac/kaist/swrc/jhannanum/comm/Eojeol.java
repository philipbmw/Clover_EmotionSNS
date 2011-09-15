/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.comm;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class Eojeol {
	public int length = 0;
	private String[] morphemes = null;
	private String[] tags = null;
	
	public Eojeol() {
	}
	
	public Eojeol(String[] morphemes, String[] tags) {
		this.morphemes = morphemes;
		this.tags = tags;
		if (morphemes.length < tags.length) {
			length = morphemes.length;
		} else {
			length = tags.length;
		}
	}
	
	public String[] getMorphemes() {
		return morphemes;
	}
	
	public String getMorpheme(int index) {
		return morphemes[index];
	}
	
	public void setMorphemes(String[] morphemes) {
		this.morphemes = morphemes;
		if (tags != null && tags.length < morphemes.length) {
			length = tags.length;
		} else {
			length = morphemes.length;
		}
	}
	
	public int setMorpheme(int index, String morpheme) {
		if (index >= 0 && index < morphemes.length) {
			morphemes[index] = morpheme;
			return index;
		} else {
			return -1;
		}
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public String getTag(int index) {
		return tags[index];
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
		if (morphemes != null && morphemes.length < tags.length) {
			length = morphemes.length;
		} else {
			length = tags.length;
		}
	}
	
	public int setTag(int index, String tag) {
		if (index >= 0 && index < tags.length) {
			tags[index] = tag;
			return index;
		} else {
			return -1;
		}
	}
	
	public String toString() {
		String str = "";
		for (int i = 0; i < length; i++) {
			if (i != 0) {
				str += "+";
			}
			str += morphemes[i] + "/" + tags[i];
		}
		return str;
	}
}
