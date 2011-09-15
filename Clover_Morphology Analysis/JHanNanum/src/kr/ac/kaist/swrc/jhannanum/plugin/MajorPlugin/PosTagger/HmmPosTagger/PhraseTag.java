/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PhraseTag {
	public static String getPhraseTag(String[] tags) {
		char[] res = {'.', '.'};
		int end = tags.length - 1;
		
		if (tags.length < 4) {
			String[] tmp = {"", "", "", ""};
	
			/* 초기화 */
			for (int i = 0 ; i < tags.length; i++) {
				tmp[i] = tags[i];
			}
			tags = tmp;
		}

		if (tags.length <= 0 || tags[0].length() == 0) {
			return String.copyValueOf(res);
		}

		/* 먼저 등장하는 태그 순서대로 검사 */
		switch (tags[0].charAt(0)) {
		case 'm':
			if (tags[0].startsWith("ma")) {
				if (tags[1].startsWith("p")) {
					res[0] = 'P';
				} else if (tags[1].startsWith("x")) {
					res[0] = 'P';
				} else if (tags[1].startsWith("jcp")) {
					res[0] = 'P';
				} else {
					res[0] = 'A';
				}
			} else if (tags[0].matches("m^a.*")) {
				if (tags[end].startsWith("j")) {
					res[0] = 'N';
				} else if (tags[1].startsWith("n")) {
					res[0] = 'N';
				} else if (tags[1].startsWith("p")) {
					res[0] = 'P';
				} else {
					res[0] = 'M';
				}
			}
			break;

		case 'e':
			if (tags[0].startsWith("ecc") || tags[0].startsWith("ecs")) {
				res[0] = 'C';
			}
			break;

		case 'f':
			res[0] = 'N';
			break;

		case 'i':
			if (tags[1].startsWith("j")) {
				res[0] = 'N';
			} else {
				res[0] = 'I';
			}
			break;

		case 'n':
			if (tags[1].matches("x.(v|m).*")) {
				if (tags[2].matches("..n.*") || tags[3].matches("..n.*")) {
					res[0] = 'N';
				} else {
					res[0] = 'P';
				}
			} else if (tags[1].matches("x.n.*")) {
				res[0] = 'N';
			} else if (tags[1].startsWith("p")) {
				if (tags[2].matches("..n.*") || tags[3].matches("..n.*")) {
					res[0] = 'N';
				} else {
					res[0] = 'P';
				}
			} else {
				res[0] = 'N';
			}
			break;

		case 'p':
			if (tags[1].startsWith("xsa")) {
				res[0] = 'A';
			} else if (tags[1].startsWith("etn") || tags[2].startsWith("n")) {
				res[0] = 'N';
			} else {
				res[0] = 'P';
			}
			break;

		case 's':
			if (tags[1].startsWith("su") || tags[2].startsWith("j")) {
				res[0] = 'N';
			} else if (tags[2].startsWith("n") || tags[end].startsWith("j")) {
				res[0] = 'N';
			} else {
				res[0] = 'S';
			}

			if (tags[0].startsWith("sf") || tags[1].startsWith("s")) {
				res[1] = 'F';
			}
			break;

		case 'x':
			if (tags[0].startsWith("xsn") || tags[0].startsWith("xp")) {
				res[0] = 'N';
			}
			break;
		}

		/* 마지막에 나오는 태그 이용해서 검사 */
		String lastTag = tags[end];
		switch (lastTag.charAt(0)) {
		case 'e':
			if (lastTag.startsWith("ecc") || lastTag.startsWith("ecs") || lastTag.startsWith("ecx")) {
				res[1] = 'C';
			} else if (lastTag.startsWith("ef")) {
				res[1] = 'F';
			} else if (lastTag.startsWith("etm")) {
				res[1] = 'M';
			} else if (lastTag.startsWith("etn")) {
				res[1] = 'N';
			}
			break;

		case 'j':
			if (lastTag.startsWith("jcv")) {
				res[0] = 'I';
			} else if (lastTag.startsWith("jx")) {
				if (res[0] == 'A') {
					res[1] = 'J';
				} else {
					res[1] = 'X';
				}
			} else if (lastTag.startsWith("jcj")) {
				if (res[0] == 'A'){
					res[1] = 'J';
				} else {
					res[1] = 'Y';
				}
			} else if (lastTag.startsWith("jca")) {
				res[1] = 'A';
			} else if (lastTag.startsWith("jcm")) {
				if (res[0] == 'A') {
					res[1] = 'J';
				} else {
					res[1] = 'M';
				}
			} else if (lastTag.startsWith("jc")) {
				res[1] = 'J';
			}
			break;
			
		case 'm':
			if (lastTag.matches("m^a.*")) {
				res[1] = 'M';
			} else if (lastTag.startsWith("mag")) {
				res[1] = 'A';
			}
			break;

		case 'n':
			if (lastTag.startsWith("n")) {
				res[0] = 'N';
			}
			break;
		
		case 'x':
			if (lastTag.startsWith("xsa")) {
				res[1] = 'A';
			}
			break;
			
			
		}

		/* 후처리 */
		if (res[0] == res[1]) {
			res[1] = '.';
		} else if (res[0] == '.') {
			res[0] = res[1];
			res[1] = '.';
		}

		if (res[0] == 'A') {
			if (res[1] == 'M') {
				res[0] = 'N';
			}
		} else if (res[0] == 'M') {
			if (res[1] == 'A') {
				res[0] = 'A';
			} else if (res[1] == 'F') {
				res[0] = 'N';
			} else if (res[1] == 'C') {
				res[0] = 'N';
			}
		} else if (res[0] == 'I') {
			if (res[1] == 'M' || res[1] == 'J') {
				res[0] = 'N';
			} else if (res[1] == 'C') {
				res[0] = 'P';
			} else if( res[1] == 'F') {
				res[0] = 'N';
			}
		}

		if (res[0] == res[1]) {
			res[1] = '.';
		}

		return String.copyValueOf(res);
	}
}