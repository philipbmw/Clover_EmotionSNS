package kr.ac.kaist.swrc.jhannanum.share;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Code {
	public final static int ENCODING_TRIPLE = 0;
	public final static int ENCODING_UNICODE = 1;
	
	public final static int JAMO_CHOSEONG = 0;
	
	public final static int JAMO_JUNGSEONG = 1;
	
	public final static int JAMO_JONGSEONG = 2;
	
	public final static char HANGUL_FILLER = 0x3164;
	
	private final static char[] CHOSEONG_LIST = 
		{'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
	
	private final static char[] JONGSEONG_LIST = 
		{HANGUL_FILLER, 'ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
	
	private final static byte[] CHOSEONG_LIST_REV =
		{0,1,-1,2,-1,-1,3,4,5,-1,-1,-1,-1,-1,-1,-1,6,7,8,-1,9,10,11,12,13,14,15,16,17,18};
	
	private final static byte[] JONGSEONG_LIST_REV =
		{1,2,3,4,5,6,7,-1,8,9,10,11,12,13,14,15,16,17,-1,18,19,20,21,22,-1,23,24,25,26,27};
	
	public static void convertFile(String srcFileName, String desFileName, int srcEncoding, int desEncoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFileName), "UTF-8"));
		String line = null;
		
		if (srcEncoding == ENCODING_UNICODE && desEncoding == ENCODING_TRIPLE) {
			while ((line = br.readLine()) != null) {
				char[] buf = toTripleArray(line);
				bw.write(buf);
				bw.write('\n');
			}
		} else if (srcEncoding == ENCODING_TRIPLE && desEncoding == ENCODING_UNICODE) {
			while ((line = br.readLine()) != null) {
				String buf = toString(line.toCharArray());
				bw.write(buf);
				bw.write('\n');
			}
		}
		br.close();
		bw.close();
	}
	
	public static boolean isChoseong(char c) {
		if (c >= 0x1100 && c <= 0x1112) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isJongseong(char c) {
		if (c >= 0x11A8 && c <= 0x11C2) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isJungseong(char c) {
		if (c >= 0x1161 && c <= 0x1175) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String args[]) {
//		String str = "안녕하세요. 저는 박상원 입니다. 값솽갋뉡닼 ㄱㄴㄷㄹㅁㄲㄸ  ^ㄱ^ㄴ^ㄷ^ㄹ^ㅁ^ㄲ^ㄸ  abcd 12345 !@#$% §★○";
//		System.out.println("원문:" + str);
//		char[] tri = Code.toTripleArray(str);
//		
//		System.out.print("코드1:");
//		for (int i = 0; i < tri.length; i++) {
//			System.out.format("%c ", Code.toCompatibilityJamo(tri[i]));
//		}
//		System.out.print("\n코드2:");
//		for (int i = 0; i < tri.length; i++) {
//			System.out.format("%x ", (short)tri[i]);
//		}
//		System.out.println();
//		System.out.println("변환:" + String.valueOf(tri));
//		System.out.println("재변환:" + Code.toString(tri));
//		System.out.println("---------------------------------------");
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("test1.txt"), "UTF-8"));
//			str = br.readLine();
//			System.out.println(str);
//			str = br.readLine();
//			str = Code.toString(str.toCharArray());
//			tri = str.toCharArray();
//			System.out.print("\n코드2:");
//			for (int i = 0; i < tri.length; i++) {
//				System.out.format("%x ", (short)tri[i]);
//			}
//			System.out.println();
//			System.out.println("변환:" + String.valueOf(tri));
//			System.out.println("재변환:" + Code.toString(tri));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			Code.convertFile("data/src/Exp.java", "data/src/Exp1.java", Code.ENCODING_UNICODE, Code.ENCODING_TRIPLE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static char toChoseong(char jongseong) {
		if (jongseong >= 0x11A8 && jongseong <= 0x11C2) {
			jongseong -= 0x11A7;
			// 종성
			char tmp = JONGSEONG_LIST[jongseong];
			tmp -= 0x3131;
			if (CHOSEONG_LIST_REV[tmp] != -1) {
				return (char)(CHOSEONG_LIST_REV[tmp] + 0x1100);
			}
		}
		return jongseong;
	}
	
	public static char toCompatibilityJamo(char jamo) {
		if (jamo >= 0x1100 && jamo < 0x1100 + CHOSEONG_LIST.length) {
			return CHOSEONG_LIST[jamo - 0x1100];
		}
		if (jamo >= 0x1161 && jamo <= 0x1175) {
			return (char)(jamo - 0x1161 + 0x314F);
		}
		if (jamo == 0) {
			return HANGUL_FILLER;
		} else {
			if (jamo >= 0x11A8 && jamo < 0x11A7 + JONGSEONG_LIST.length) {
				return JONGSEONG_LIST[jamo - 0x11A7];
			}
		}
		return jamo;
	}
	
	public static char toJamo(char jamo, int flag) {
		char result = 0;
		switch (flag) {
		case JAMO_CHOSEONG:
			if (jamo >= 0 && jamo <= 0x12) {
				result = (char)(jamo + 0x1100);
			}
			break;
		case JAMO_JUNGSEONG:
			if (jamo >= 0 && jamo <= 0x14) {
				result = (char)(jamo + 0x1161);
			}
			break;
		case JAMO_JONGSEONG:
			if (jamo >= 1 && jamo <= 0x1B) {
				result = (char)(jamo + 0x11A7);
			}
			break;
		}
		return result;
	}
	
	public static String toString(char[] tripleArray) {
		String result = "";
		int i = 0;
		int len = tripleArray.length;
		
		int cho;
		int jung;
		int jong;
		
		if (len == 0) {
			return "";
		}
		
		char c = tripleArray[i];
		
		while (i < len) {
			if (c >= 0x1100 && c <= 0x1112) {
				cho = c - 0x1100;

				if (++i < len) {
					c = tripleArray[i];
				}
				if (c >= 0x1161 && c <= 0x1175 && i < len) {
					jung = c - 0x1161;
					
					if (++i < len) {
						c = tripleArray[i];
					}
					if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
						jong = c - 0x11A7;
						
						// 초성 + 중성 + 종성
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
						if (++i < len) {
							c = tripleArray[i];
						}
					} else {
						// 초성 + 중성
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
					}
				} else {
					// 초성 (홀로 쓰인 초성 자음은 "^자음"으로 표기)
					char tmp = CHOSEONG_LIST[cho];
					if (tmp == 'ㅃ' || tmp == 'ㅉ' || tmp == 'ㄸ') {
						result += CHOSEONG_LIST[cho];
					} else {
						result += "^" + CHOSEONG_LIST[cho];
					}
				}
			} else if (c >= 0x1161 && c <= 0x1175 && i < len) {
				jung = c - 0x1161;
				
				// 중성
				result += (char)(jung + 0x314F);
				
				if (++i < len) {
					c = tripleArray[i];
				}
			} else if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
				jong = c - 0x11A7;
				
				// 종성
				result += JONGSEONG_LIST[jong];
				
				if (++i < len) {
					c = tripleArray[i];
				}
			} else {
				result += c;
				
				if (++i < len) {
					c = tripleArray[i];
				}
			}
		}
		return result;
	}
	
	public static String toString(char[] tripleArray, int len) {
		String result = "";
		int i = 0;
		
		int cho;
		int jung;
		int jong;
		
		char c = tripleArray[i++];
		
		while (i < len) {
			if (c >= 0x1100 && c <= 0x1112 && i < len) {
				cho = c - 0x1100;
				c = tripleArray[i++];
				if (c >= 0x1161 && c <= 0x1175 && i < len) {
					jung = c - 0x1161;
					c = tripleArray[i++];
					if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
						jong = c - 0x11A7;
						// 초성 + 중성 + 종성
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
						c = tripleArray[i++];
					} else {
						// 초성 + 중성
						result += (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
					}
				} else {
					// 초성 (홀로 쓰인 초성 자음은 "^자음"으로 표기)
					char tmp = CHOSEONG_LIST[cho];
					if (tmp == 'ㅃ' || tmp == 'ㅉ' || tmp == 'ㄸ') {
						result += CHOSEONG_LIST[cho];
					} else {
						result += "^" + CHOSEONG_LIST[cho];
					}
				}
			} else if (c >= 0x1161 && c <= 0x1175 && i < len) {
				jung = c - 0x1161;
				// 중성
				result += (char)(jung + 0x314F);
				c = tripleArray[i++];
			} else if (c >= 0x11A8 && c <= 0x11C2 && i < len) {
				jong = c - 0x11A7;
				// 종성
				result += JONGSEONG_LIST[jong];
				c = tripleArray[i++];
			} else {
				result += c;
				c = tripleArray[i++];
			}
		}
		return result;
	}
	
	public static char toSyllable(char cho, char jung, char jong) {
		if (cho >= 0x1100 && cho <= 0x1112) {
			cho -= 0x1100;
			if (jung >= 0x1161 && jung <= 0x1175) {
				jung -= 0x1161;
				if (jong >= 0x11A8 && jong <= 0x11C2) {
					jong -= 0x11A8;
					// 초성 + 중성 + 종성
					return (char)(0xAC00 + (cho * 21 * 28) + (jung * 28) + jong);
				} else {
					// 초성 + 중성
					return (char)(0xAC00 + (cho * 21 * 28) + (jung * 28));
				}
			} else {
				// 초성
				return CHOSEONG_LIST[cho];
			}
		} else if (jung >= 0x1161 && jung <= 0x1175) {
			jung -= 0x1161;
			// 중성
			return (char)(jung + 0x314F);
		} else if (jong >= 0x11A8 && jong <= 0x11C2) {
			jong -= 0x11A;
			// 종성
			return JONGSEONG_LIST[jong];
		}
		return HANGUL_FILLER;
	}
	
	public static char[] toTripleArray(String str) {
		char[] result = null;
		ArrayList<Character> charList = new ArrayList<Character>(); 
		char c = 0;
		char cho;
		char jung;
		char jong;
		
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			
			if(c >= 0xAC00 && c <= 0xD7AF) {
				int combined = c - 0xAC00;
				if ((cho = toJamo((char)(combined / (21 * 28)), JAMO_CHOSEONG)) != 0) {
					// 조합되어 있는 초성 자음 (발생하지 않는 경우 인코딩 문제가 있는 것)
					charList.add(cho);	
				}
				combined %= (21 * 28);
				if ((jung = toJamo((char)(combined / 28), JAMO_JUNGSEONG)) != 0) {
					// 조합되어 있는 중성 모음 (발생하지 않는 경우 인코딩 문제가 있는 것)
					charList.add(jung);
				}
				if ((jong = toJamo((char)(combined % 28), JAMO_JONGSEONG)) != 0) {
					// 조합되어 있는 종성 자음
					charList.add(jong);
				}
			} else if (c >= 0x3131 && c <= 0x314E) {
				c -= 0x3131;
				if (JONGSEONG_LIST_REV[c] != -1) {
					// 홀로 쓰인 자음은 종성 자음으로 변환
					charList.add((char)(JONGSEONG_LIST_REV[c] + 0x11A7));
				} else if (CHOSEONG_LIST_REV[c] != -1) {
					// 종성이 될 수 없는 자음은 초성으로 변환
					charList.add((char)(CHOSEONG_LIST_REV[c] + 0x1100));
				} else {
					// 예외인 경우 그냥 입력 (발생하는 경우 변환 배열에 문제가 있는 것)
					charList.add((char)(c + 0x3131));
				}
			} else if (c >= 0x314F && c <= 0x3163) {
				// 홀로 쓰인 모음은 중성 모음으로 변환
				charList.add((char)(c - 0x314F + 0x1161));
			} else if (c == '^' && str.length() > i + 1 && str.charAt(i+1) >= 0x3131 && str.charAt(i+1) <= 0x314E) {
				// "^자음"은 초성 자음으로 변환
				c = (char)(str.charAt(i+1) - 0x3131);
				if (CHOSEONG_LIST_REV[c] != -1) {
					charList.add((char)(CHOSEONG_LIST_REV[c] + 0x1100));
					i++;
				} else {
					charList.add('^');
				}
			}
			else {
				// 나머지 유니코드 문자들 또는 조합 불가능한 한글 자음 모음은 그대로 입력
				charList.add(c);
			}
		}
		
		result = new char[charList.size()];
		Iterator<Character> iter = charList.iterator();
		for (int i = 0; i < result.length; i++) {
			result[i] = iter.next();
		}
		
		return result;
	}
	
	public static String toTripleString(String str) {
		return String.valueOf(toTripleArray(str));
	}
}