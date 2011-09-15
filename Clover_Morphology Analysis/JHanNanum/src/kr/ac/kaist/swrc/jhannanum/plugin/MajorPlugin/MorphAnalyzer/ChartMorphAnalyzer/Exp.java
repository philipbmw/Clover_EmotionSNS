/**
 *  
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import kr.ac.kaist.swrc.jhannanum.share.Code;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class Exp { 
/*
 * TAG_TYPE_YONGS : TAG_TYPE_YONGS
 * TAG_TYPE_EOMIES : TAG_TYPE_EOMIES
 * IRR_TYPE_S : IRR_TYPE_S
 * ᆮᆸᅮᆯᆨᅲᆾᅵᆨ : IRR_TYPE_D
 * ᆸᆸᅮᆯᆨᅲᆾᅵᆨ : IRR_TYPE_B
 * ᇂᆸᅮᆯᆨᅲᆾᅵᆨ : IRR_TYPE_H
 * ᆯᅳᆸᅮᆯᆨᅲᆾᅵᆨ : IRR_TYPE_REU
 * ᆯᅥᆸᅮᆯᆨᅲᆾᅵᆨ : IRR_TYPE_REO
 */

final static public int MAX_BUF_SIZE = 2048;
final static public int MAXPSET = 2048;

public int pset_end = 0;

public MorphemeChart mc = null;

private TagSet tagSet = null;

	public String[][] pset = 
	{
			{"초성","ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ"},
			{"종성","ᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ"},
			{"중성","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},

			{"음성모음","ᅥᅮᅧᅲᅦᅯᅱᅨ"},
			{"양성모음","ᅡᅩᅣᅢᅪᅬᅤ"},
			{"중성모음","ᅳᅵ"},

			/* 것 관련 : 걸로, 걸, 겁니다, 건 거면 */
			{"rule_것l",""},
			{"rule_것","ᄂᄆᄅᆫᆯᆸ"},
			{"rule_것r",""},

			/* ᆯ탈락*/
			{"l11","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
			{"11"," ᆫᆯᆷᆸᄂᄉ"},
			{"r11",""},

			/* ᆯ탈락2 */
			{"l11-1","ᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
			{"11-1","ᄂᄉ"},
			{"r11-1",""},

			/* ᅳ탈락*/
			{"l12",""},
			{"12","ᅡᅥ"},
			{"r12",""},

			/* ᅡ 탈락 */
			{"l13",""},
			{"13","ᅡ"},
			{"r13",""},

			/* ᅥ 탈락 */
			{"l14",""},
			{"14","ᅥᅦᅧᅢ"},
			{"r14",""},

			/* ᆮ 불규칙 */
			{"l21","ᆯ"},
			{"21","ᄋ"},
			{"r21","ᅥᅡᅳ"},

			/* ᆺ 불규칙 */
			{"l22","ᅡᅥᅮᅳᅵ"},
			{"22","ᄋ"},
			{"r22","ᅥᅡᅳ"},

			/* ᆸ 불규칙 */
			{"l23","ᄋ"},
			{"23","ᅮ"},
			{"r23",""},

			/* ᆸ 불규칙 2 */
			{"l24","ᄋ"},
			{"24","ᅪ"},
			{"r24",""},

			/* ᆸ 불규칙 3 */
			{"l25","ᄋ"},
			{"25","ᅯ"},
			{"r25",""},

			/* ᇂ 불규칙 */
			{"l26","ᄀᄃᄅᄆᄋ"},
			{"26","ᅡᅣ"},
			{"r26",""},

			/* ᇂ 불규칙 2 */
			{"l27","ᄀᄃᄅᄆᄄᄋ"},
			{"27","ᅢᅤ"},
			{"r27",""},

			/* ᇂ 불규칙 3 */
			{"l28","ᄀᄃᄅᄆᄄᄋ"},
			{"28","ᅥ"},
			{"r28",""},

			/* 르 불규칙 */
			{"l29","ᆯ"},
			{"29","ᄅ"},
			{"r29","ᅥᅡ"},

			/* 러 불규칙 */
			{"l30","ᅳ"},
			{"30","ᄅ"},
			{"r30","ᅥ"},

			/* 우 불규칙 */
			{"l31","ᄑ"},
			{"31","ᅥ"},
			{"r31",""},

			/* 여 불규칙 */
			{"l32","ᄒ"},
			{"32","ᅡ"},
			{"r32","ᄋ"},

			/* 여 불규칙 2 */
			{"l33","ᄒ"},
			{"33","ᅢ"},
			{"r33",""},

			/* ᅩ/ᅮ축약 */
			{"l51",""},
			{"51","ᅪᅯ"},
			{"r51",""},

			/* ᅬ 축약 */
			{"l52",""},
			{"52","ᅫ"},
			{"r52",""},

			/*
			 * ᅵ 축약
			 * ᅧ 에 관련되어서는 negative rule/don't care condition까지
			 * 첫음절 출현이냐 이후 음절 출현에 따라 양상이 많이 달라진다.
			 * 여기에서는 첫음절 '여'만을 따로 금지하는 것으로 한다
			 */
			{"l53",""},
			{"53","ᅧ"},
			{"r53",""},

			/* 
			 * 어미 '으' 탈락
			 * l54는 '으/스/느'탈락에서 모두 공유함
			 */ 
			{"l54","ᆯᅡᅣᅥᅧᅩᅭᅮᅲᅳᅵᅢᅤᅦᅨᅬᅱᅴᅪᅯᅫᅰ"},
			{"54"," ᆫᆯᆷᆸᄂᄅᄆᄉᄋ"},
			{"r54",""}
	};

	public Exp(MorphemeChart mc, TagSet tagSet) {
		this.mc = mc;
		this.tagSet = tagSet;
		pset_end = pset.length;
	}

	public String insert(String str,int cur, String s) {
		return str.substring(0, cur) + s + str.substring(cur);
	}

	public int	pcheck(String base, int idx, String str) {
		char c;
		
		if (idx < base.length()) {
			c = base.charAt(idx);
		} else {
			c = '\0';
		}
		
		for (int i = 0; i < pset_end; i++) {
			if (pset[i][0].equals(str)) {
				if (pset[i][1].length() == 0) {
					return 1;
				} else {
					int index = pset[i][1].indexOf(c);
					if (index == -1) {
						return 0;
					} else {
						return index + 1;
					}
				}
			}
		}
		return 0;
	}

	public void prule(int from, String prev, String str, SegmentPosition sp) {
		int i;
		rule_NP(from, prev, str);
		// sp.printPosition();
		
		for(i=0;i<str.length();i++) {
			rule_rem(from,prev,str,i);
			// sp.printPosition();
			rule_irr_word(from,prev,str,i);
			// sp.printPosition();
			rule_irr_word2(from,prev,str,i);
			// sp.printPosition();
			rule_shorten(from,prev,str,i);
			// sp.printPosition();
			rule_eomi_u(from,prev,str,i);
			// sp.printPosition();
			rule_johwa(from,prev,str,i);
			// sp.printPosition();
			rule_i(from,prev,str,i);
			// sp.printPosition();
			rule_gut(from,prev,str,i);
			// sp.printPosition();
		}
	}

	public String replace(String str, int cur, String s) {
		char[] array = str.toCharArray();
		if (s.length() == 0) {
			System.err.println("Exp.java: replace(): s is to short");
			System.exit(0);
		}
		array[cur] = s.charAt(0);
		return String.valueOf(array);
	}

	public void rule_eomi_u(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		
		if (cur > str.length()) {
			return;
		}
		
		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&pcheck(str,cur,"54")!=0
				&&pcheck(str,cur+1,"r54")!=0) {
			new_str=insert(str,cur,"으");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&strncmp(str,cur,"ᆸ니",0,3)==0) {
			new_str=insert(str,cur,"스");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
		if ((cur>0 && pcheck(str,cur-1,"l54")!=0)
				&&strncmp(str,cur,"ᆫ다",0,3)==0) {
			new_str=insert(str,cur,"느");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	public void rule_gut(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		
		if (cur >= str.length()) {
			return;
		}

		if (cur>1&& strncmp(str,cur-2,"거",0,2)==0 &&
				pcheck(str,cur,"rule_것")!=0) {

			if (str.charAt(cur)=='ᆸ') {
				if (strncmp(str,cur,"ᆸ니",0,3)==0) {
					new_str=insert(str,cur,"ᆺ이");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
				}
			} else {
				if (strncmp(str,cur,"ᆯ로",0,3)==0) {
					new_str=replace(str,cur,"ᆺ");
					new_str=insert(new_str,cur+1,"으");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);

				} else if (str.charAt(cur)=='ᆯ'||str.charAt(cur)=='ᆫ') {
					if (str.length() != cur + 1) {
						new_str=insert(str,cur,"ᆺ이");
						buf = new_str.substring(0,cur+1);
						buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
						mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
					}

					new_str=insert(str,cur,"ᆺ으");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
				} else {
					new_str=insert(str,cur,"ᆺ이");
					buf = new_str.substring(0,cur+1);
					buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
					mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JP,0);
				}
			}
		} 
	}

	public void rule_i(int from, String prev, String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		
		if (cur+2 > str.length()) {
			return;
		}

		if ((prev!=null&&prev.length() != 0&&cur==0)
				&&pcheck(prev,prev.length()-1,"중성")!=0) {

			if (strncmp(str,0,"여",0,2)==0) {
				new_str=replace(str,cur+1,"ᅥ");
				new_str=insert(new_str,cur+1,"ᅵᄋ");
				buf = new_str.substring(0,cur+2);
				buf2 = new_str.substring(cur+2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_JP,TagSet.TAG_TYPE_EOMIES,0);
			} else {
				if (pcheck(str,0,"종성")!=0||
						strncmp(str,0,"는",0,3)==0||strncmp(str,0,"은",0,3)==0||
						strncmp(str,0,"음",0,3)==0||strncmp(str,2,"는",0,3)==0) 
					return;
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,"이",str,TagSet.TAG_TYPE_JP,TagSet.TAG_TYPE_EOMIES,0);
				buf = "이" + str;
				rule_eomi_u(from,prev,buf,cur+2);
			}
		}
	}

	/* 
	 * 2. 불규칙 처리 
	 * 'ᆮ'불규칙
	 * 'ᆺ'불규칙
	 * 'ᆸ'불규칙
	 * 'ᇂ'불규칙
	 * '르'불규칙
	 * '러'불규칙
	 * '우'불규칙
	 * '여'불규칙
	 */
	public void rule_irr_word(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		int len = str.length();
		
		/* 'ᆮ'불규칙
		 * ᆯ+ ᆼᅥ, ᆼᅡ,ᆼᅳ 일때, ᆯ을 ᆮ으로 복원.
		 */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l21")!=0)
				&&pcheck(str,cur,"21")!=0
				&&pcheck(str,cur+1,"r21")!=0) {
			new_str = replace(str,cur-1,"ᆮ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_D);
		}

		/* 'ᆺ'불규칙
		 * ᆺ? ᆼᅥ, ᆼᅡ,ᆼᅳ 일때, ᆺ을 살려냄
		 */
		if ((cur>0&&cur<len&&pcheck(str,cur-1,"l22")!=0)
				&&pcheck(str,cur,"22")!=0
				&&pcheck(str,cur+1,"r22")!=0) {
			new_str=insert(str,cur,"ᆺ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_S);
		}

		/* ᆸ 불규칙. ᅮ */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l23")!=0)
				&&pcheck(str,cur,"23")!=0
				&&pcheck(str,cur+1,"r23")!=0) {
			new_str=replace(str,cur,"ᅳ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		/* ᆸ 불규칙 */
		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l24")!=0)
				&&pcheck(str,cur,"24")!=0
				&&pcheck(str,cur+1,"r24")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l25")!=0)
				&&pcheck(str,cur,"25")!=0
				&&pcheck(str,cur+1,"r25")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur-1,"ᆸ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_B);
		}

		/* ᇂ 불규칙 ᅡᅣ */
		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l26")!=0)
				&&pcheck(str,cur,"26")!=0
				&&pcheck(str,cur+1,"r26")!=0) {
			new_str=insert(str,cur+1,"ᇂ으");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}

		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l27")!=0)
				&&pcheck(str,cur,"27")!=0
				&&pcheck(str,cur+1,"r27")!=0) {
			if (str.charAt(cur)=='ᅢ') {
				new_str=replace(str,cur,"ᅡ");
			} else {
				new_str=replace(str,cur,"ᅣ");
			}
			new_str=insert(new_str,cur+1,"ᇂ어");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
			//			이운재 추가
			if (str.charAt(cur)=='ᅢ') {
				new_str = replace(str,cur,"ᅥ");
			} else {
				new_str = replace(str,cur,"ᅧ");
			}
			new_str=insert(new_str,cur+1,"ᇂ어");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}
		if ((cur>0&&cur+1<len&&pcheck(str,cur-1,"l28")!=0)
				&&pcheck(str,cur,"28")!=0
				&&pcheck(str,cur+1,"r28")!=0) {
			new_str=replace(str,cur,"ᅥ");
			new_str=insert(new_str,cur+1,"ᇂᄋ");
			buf = new_str.substring(0,cur+2);
			buf2 = new_str.substring(cur+2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_H);
		}


		/* 르 불규칙
		 * 르+ᆼᅥ : 'ᆯ러' 로 변화
		 * */

		if ((cur>0&&cur<len&&pcheck(str,cur-1,"l29")!=0)
				&&pcheck(str,cur,"29")!=0
				&&pcheck(str,cur+1,"r29")!=0) {
			new_str = replace(str,cur,"ᅳ");
			if (new_str.charAt(cur+1)=='ᅡ') 
				new_str = new_str.substring(0, cur+1) + 'ᅥ' + new_str.substring(cur+2);
			new_str = insert(new_str,cur+1,"ᄋ");
			new_str = new_str.substring(0, cur-1) + Code.toChoseong(new_str.charAt(cur-1)) + new_str.substring(cur);

			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_REU);
		}
		/* 러 불규칙
		 * 이르러 -> 이르 + 어
		 * */

		if ((cur>0&&cur<=len&&pcheck(str,cur-1,"l30")!=0)
				&&pcheck(str,cur,"30")!=0
				&&pcheck(str,cur+1,"r30")!=0&&(cur-2>=0&&str.charAt(cur-2)=='ᄅ')) {
			new_str=replace(str,cur,"ᄋ");
			buf = new_str.substring(0,cur);
			buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,tagSet.IRR_TYPE_REO);
		}
	}

	public void rule_irr_word2(int from,String prev,String str,int cur)
	{
		String buf;
		String buf2;
		String new_str;
		
		if (cur >= str.length()) {
			return;
		}
		
		if ((cur>0&&pcheck(str,cur-1,"l31")!=0)
				&&pcheck(str,cur,"31")!=0
				&&pcheck(str,cur+1,"r31")!=0) {
			new_str=replace(str,cur,"ᅮ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		if ((cur>0&&pcheck(str,cur-1,"l32")!=0)
				&&pcheck(str,cur,"32")!=0
				&&pcheck(str,cur+1,"r32")!=0&&str.charAt(cur+2)=='ᅧ') {
			new_str=replace(str,cur+2,"ᅥ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		if ((cur>0&&pcheck(str,cur-1,"l33")!=0)
				&&pcheck(str,cur,"33")!=0
				&&pcheck(str,cur+1,"r33")!=0) {
			new_str=replace(str,cur,"ᅡ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	public void rule_johwa(int from,String prev,String str,int cur)	{
		String buf;
		String buf2;
		String new_str;
		if (cur>0&&pcheck(str,cur-1,"양성모음")!=0) {
			if (cur+2<str.length()&&str.charAt(cur+1)=='ᄋ'&&str.charAt(cur+2)=='ᅡ') {
				new_str=replace(str,cur+2,"ᅥ");
				buf = new_str.substring(0,cur+1);
				buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
			else if (cur+1<str.length()&&str.charAt(cur)=='ᄋ'&&str.charAt(cur+1)=='ᅡ') {
				new_str=replace(str,cur+1,"ᅥ");
				buf = new_str.substring(0,cur);
				buf2 = new_str.substring(cur);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
		}
	}

	public void rule_NP(int from,String prev,String str) {
		String buf;

		if (strncmp(str,0,"내가",0,4)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"네가",0,4)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"제가",0,4)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저",str+2,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"내",0)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"네",0)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strcmp(str,0,"제",0)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저","의",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"내게",0,4)==0) {
			buf = "에" + str.substring(2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"나",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"네게",0,4)==0) {
			buf = "에" + str.substring(2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"너",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"제게",0,4)==0) {
			buf = "에" + str.substring(2);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"저",buf,TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		} else if (strncmp(str,0,"나",0,2)==0) {
			if (str.length()==3&&str.charAt(2)=='ᆫ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"나","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 3&&str.charAt(2)=='ᆯ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"나","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strncmp(str,0,"너",0,2)==0) {
			if (str.length() == 3&&str.charAt(2)=='ᆫ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"너","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 3&&str.charAt(2)=='ᆯ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"너","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strncmp(str,0,"누구",0,4)==0) {
			if (str.length() == 5&&str.charAt(4)=='ᆫ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"누구","는",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			} else if (str.length() == 5&&str.charAt(4)=='ᆯ') {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
				mc.phonemeChange(from,"누구","를",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
			}
		} else if (strcmp(str,0,"무언가",0)==0) {
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()));
			mc.phonemeChange(from,"무엇","인가",TagSet.TAG_TYPE_NBNP,TagSet.TAG_TYPE_JOSA,0);
		}
	}

	/* 
	 * 1. 탈락현상 처리 
	 * ᆯ탈락, ᅳ탈락, ᅡ탈락 현상 처리
	 */
	public void rule_rem(int from,String prev,String str,int cur) {
		String buf;
		String buf2;
		String new_str;
		
		if (cur >= str.length()) {
			return;
		}
		
		// ᆯ탈락 처리.
		if ((cur>0&&pcheck(str,cur-1,"l11")!=0)
				&&(pcheck(str,cur,"11")!=0 || strncmp(str,cur,"오",0,2)==0)
				&&pcheck(str,cur+1,"r11")!=0) {

			String buf3;
			new_str=insert(str,cur,"ᆯ");
			buf3 = new_str;

			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			rule_eomi_u(from,prev,buf3,cur+1);
		}

		if ((cur>0&&pcheck(str,cur-1,"l12")!=0)
				&&pcheck(str,cur,"12")!=0
				&&pcheck(str,cur+1,"r12")!=0
				||(cur==1&&str.charAt(cur)!='ᅡ')
		) {
			new_str = replace(str,cur,"ᅥ");
			new_str = insert(new_str,cur,"ᅳᄋ");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 
		 * "ᅡ"탈락 처리
		 */
		if ((cur>0&&pcheck(str,cur-1,"l13")!=0)
				&&pcheck(str,cur,"13")!=0 
				&&pcheck(str,cur+1,"r13")!=0) {
			new_str = insert(str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		if ((cur>0&&pcheck(str,cur-1,"l14")!=0)
				&&pcheck(str,cur,"14")!=0
				&&pcheck(str,cur+1,"r14")!=0) {
			new_str=insert(str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}
	}

	/* 3. 아/어 축약
	 * ᅩ/ᅮ 축약
	 * 
	 * ᅵ 축약
	 * ᅬ 축약
	 * */
	public void rule_shorten(int from,String prev,String str,int cur)
	{
		String buf;
		String buf2;
		String new_str;
		if (cur >= str.length()) {
			return;
		}
		
		if ((cur>0&&pcheck(str,cur-1,"l51")!=0)
				&&pcheck(str,cur,"51")!=0
				&&pcheck(str,cur+1,"r51")!=0) {
			if (str.charAt(cur)=='ᅪ') {
				new_str=replace(str,cur,"ᅩ");
			} else {
				new_str=replace(str,cur,"ᅮ");
			}
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* ᅬ 축약
		 * */

		if ((cur>0&&pcheck(str,cur-1,"l52")!=0)
				&&pcheck(str,cur,"52")!=0
				&&pcheck(str,cur+1,"r52")!=0) {
			new_str=replace(str,cur,"ᅬ");
			new_str=insert(new_str,cur+1,"어");
			buf = new_str.substring(0,cur+1);
			buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
			mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
		}

		/* 
		 * ᅵ 축약
		 * ᅵ + ᅥ -> ᅧ
		 */
		if (cur>0)
		{
			if (((cur>1 || (str.charAt(cur-1)!='ᄋ'))&&pcheck(str,cur-1,"l53")!=0)
					&&pcheck(str,cur,"53")!=0
					&&pcheck(str,cur+1,"r53")!=0) {

				new_str=replace(str,cur,"ᅵ");
				new_str=insert(new_str,cur+1,"어");
				buf = new_str.substring(0,cur+1);
				buf2 = new_str.substring(cur+1);
					// System.out.println("Prev: " + Code.toString(prev.toCharArray()) + ", " + "Str: " + Code.toString(str.toCharArray()) + ", " + "Cur: " + cur);
				mc.phonemeChange(from,buf,buf2,TagSet.TAG_TYPE_YONGS,TagSet.TAG_TYPE_EOMIES,0);
			}
		}
	}

	public int scheck(char c, String str) {
		if (str.length() == 0) {
			return 1;
		}
		int index;
		if ((index = str.indexOf(c)) == -1) {
			return 0;
		} else {
			return index + 1;
		}
	}

	private int strcmp(String s1, int i1, String s2, int i2) {
		int l1 = s1.length() - i1;
		int l2 = s2.length() - i2;

		int len = l1;
		boolean diff = false;

		if (len > l2) {
			len = l2;
		}

		while (len-- > 0) {
			if (s1.charAt(i1++) != s2.charAt(i2++)) {
				diff = true;
				break;
			}
		}

		if (diff == false && l1 != l2) {
			if (l1 > l2) {
				return s1.charAt(i1);
			} else {
				return -s2.charAt(i2);
			}
		}
		return s1.charAt(i1-1) - s2.charAt(i2-1);
	}

	private int strncmp(String s1, int i1, String s2, int i2, int len) {
		if (s1.length() - i1 < len) {
			return 1;
		} else if (s2.length() - i2 < len) {
			return -1;
		}
		while (len-- > 0) {
			if (s1.charAt(i1++) != s2.charAt(i2++)) {
				break;
			}
		}
		return s1.charAt(i1-1) - s2.charAt(i2-1);
	}
}


