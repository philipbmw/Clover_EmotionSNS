package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

/**
 * 
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */ 
public class NumberDic {
	/* 숫자를 찾아내기 위한 오토마타 */
	private byte[][] num_automata = {
			/* ACC, +, -, ., ,, n, etc.     */
			{ 0, 0, 0, 0, 0, 0, 0}, /* 0 */
			{ 0, 9, 9, 0, 0, 2, 0}, /* 1 */
			{ 1, 0, 0,11, 5, 3, 0}, /* 2 */
			{ 1, 0, 0,11, 5, 4, 0}, /* 3 */
			{ 1, 0, 0,11, 5,10, 0}, /* 4 */
			{ 0, 0, 0, 0, 0, 6, 0}, /* 5 */
			{ 0, 0, 0, 0, 0, 7, 0}, /* 6 */
			{ 0, 0, 0, 0, 0, 8, 0}, /* 7 */
			{ 1, 0, 0, 0, 5, 0, 0}, /* 8 */
			{ 0, 0, 0, 0, 0,10, 0}, /* 9 */
			{ 1, 0, 0,11, 0,10, 0}, /* 10 */
			{ 1, 0, 0, 0, 0,12, 0}, /* 11 */
			{ 1, 0, 0, 0, 0,12, 0}  /* 12 */
	};

	public boolean isNum(int idx) {
		if (num_automata[idx][0] == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 숫자를 찾는 오토마타를 검색하는 함수 
	 */
	public int node_look(int c, int nidx)
	{
		int inp;
		switch(c)
		{
		case '+' : inp=1; break;
		case '-' : inp=2; break;
		case '.' : inp=3; break;
		case ',' : inp=4; break;
		default  : 
			if(Character.isDigit(c))
				inp=5; 
			else
				inp=6;
		}
		return num_automata[nidx][inp];
	}
}
