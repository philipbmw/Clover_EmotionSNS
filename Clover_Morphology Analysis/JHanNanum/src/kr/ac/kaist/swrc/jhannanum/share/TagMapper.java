package kr.ac.kaist.swrc.jhannanum.share;

public class TagMapper {

	public static String getKaistTagOnLevel(String tag, int level){
		if (tag == null || level > 4 || level < 1) {
			return null;
		}
		
		int tagLen = tag.length();
		if (tagLen > level) {
			return tag.substring(0, level);
		} else {
			return tag;
		}
	}
}
