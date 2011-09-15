/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class Simti {
	public class HEADI {
		String version = null;
		ST_FREE s_free = new ST_FREE();
		ST_NODE s_node = new ST_NODE();
		int n_size;
		int f_size;
	}
	public class ST_FREE {
		int size;	/* this free size : always not 0 */
		int next;	/* 0:no next, other : next pointer*/
	}
	public class ST_NF {
		ST_NODE node = new ST_NODE();
		ST_FREE free = new ST_FREE();
	}
	public class ST_NODE {
		char K;		/* key */
		byte CS;	/* cihld size - 0:no child */
		int I;		/* information */
		int child;	/* child idx */
	}

	final public static String ST_VER = "simti v0.0, 1997, (C) KAIST CS Lab,\nwjlee, hspaik, yuntan, mywork\n";
	final public static int ST_NF_DEFAULT = 300000;
	final public static int ST_MAX_WORD = 1024;
	final public static int DEFAULT_FMODE = 0644; 
	public char flag;
	public char change;
	public int search_end; /* global array and variables search(), firstkey(), nextkey()에서 값을 조정 */
	public char[] search_word = new char[ST_MAX_WORD];

	public int[] search_idx = new int[ST_MAX_WORD];

	public String filename = null;

	public HEADI head = null;

	public ST_NF[] nf = null;
	
	public Simti() {
		head = new HEADI();
		nf = new ST_NF[ST_NF_DEFAULT];
		for (int i = 0; i < ST_NF_DEFAULT; i++) {
			nf[i] = new ST_NF();
		}
	}

	/**
	 * 노드를 확보하여 인덱스를 반환한다. 보통 sibling을 묶어서 free 또는 alloc 한다.
	 * @param size	확보할 노드의 수
	 * @return	확보한 노드의 인덱스, 0: Free Node가 없음
	 */
	public int alloc(int size) {
		int i,prev_i=0;

		for (i=this.head.s_free.next ; i!=0 ; i=this.nf[i].free.next ) {
			if ( this.nf[i].free.size >= size ) break;
			prev_i=i;
		}

		if (i==0) {
			System.err.format("alloc:NO FREE NODE\n");
			return 0;		/* no free node */
		}
		if (prev_i==0)		/* head node */
			if (size == this.nf[i].free.size) 
				this.head.s_free.next=this.nf[i].free.next;
			else 	{	/* size < free node size */
				this.nf[i+size].free.size = this.nf[i].free.size - size;
				this.head.s_free.next = i+size;
				this.nf[i+size].free.next = this.nf[i].free.next;
			}
		else
			if (size == this.nf[i].free.size) 
				this.nf[prev_i].free.next = this.nf[i].free.next;
			else	{
				this.nf[i+size].free.size = this.nf[i].free.size - size;
				this.nf[prev_i].free.next = i+size;
				this.nf[i+size].free.next = this.nf[i].free.next;
			}
		this.head.f_size -= size;
		return i;
	}

	public int binary_search(int idx, char size,char key) {
		int left=0,right,middle;
		ST_NODE	node;

		right=(int) size -1;
		while(left<=right)
		{
			middle=(left+right)/2;
			node=this.nf[middle+idx].node;
			if (key > node.K)
				left=middle+1;
			else if (key < node.K)
				right=middle-1;
			else
				return(idx+middle);
		}
		return 0;
	}

	public void clean()	{
		flag = 0;
		change = 0; 
		search_end=0;

		head.n_size = ST_NF_DEFAULT;
		head.f_size = ST_NF_DEFAULT-1;

		head.s_node.K = 0;
		head.s_node.CS = 0;
		head.s_node.I = 0;
		head.s_node.child = 0;

		head.s_free.size = 0;
		head.s_free.next = 1; 

		/* 0번 노드 사용 안함 */
		nf[1].free.size = ST_NF_DEFAULT-1; 
		nf[1].free.next = 0; 
	}

	/**
	 * 
	 * @param word
	 * @return	-1: fail, 0: not exist, 1: success
	 */
	public int delete(char[] word) {
		int	i,d,j;
		int	idx,newidx;
		byte size;
		ST_NODE	temp;
		ST_NODE node = new ST_NODE();

		search(word);
		if (this.search_end < word.length || word.length == 0 )	
			return 0;	

		temp= this.nf[this.search_idx[this.search_end-1]].node;

		if (temp.I == 0)
			return 0;

		node_copy(node, temp);

		for(i=this.search_end-1;i>0 && node.CS==0 && node.I == 0;i--)
		{
			this.search_end--;
			if (i==1) 
				node_copy(node, this.head.s_node);
			else
				node_copy(node, this.nf[this.search_idx[i-1]].node);

			if (node.CS == 1)
			{
				free(node.child,1);
				node.CS= 0;
				node.child=0;
			}
			else {
				idx= node.child;
				d = this.search_idx[i] - idx;	/* distance */
				size = node.CS;

				newidx=alloc(size-1);
				for(j=0;j<d;j++) {
					ST_NODE tmp = this.nf[newidx+j].node;
					this.nf[newidx+j].node = this.nf[idx+j].node;
					this.nf[idx+j].node = tmp;
				}
				for(j=0;j<size-d-1;j++) {
					ST_NODE tmp = this.nf[newidx+j].node;
					this.nf[newidx+j].node=this.nf[idx+j].node;
					this.nf[idx+j].node = tmp;
				}
				free(idx,size);
				node.CS--;
				node.child=newidx;
			}
			if (i==1) 
				node_copy(this.head.s_node, node);
			else
				node_copy(this.nf[this.search_idx[i-1]].node, node);
		}
		return 1;
	}

	/**
	 * 
	 * @param word
	 * @return	information I(태그 정보), 0: not exist
	 */
	public int fetch(char[] word) {
		search(word);
		if (this.search_end != word.length || word.length == 0)
			return 0;
		else return this.nf[this.search_idx[this.search_end-1]].node.I;
	}

	/**
	 * 첫 자식을 따라가 처음 정보가 있는 곳까지 단어를 만들어낸다.
	 * @param word
	 * @return	찾은 문자열 수, 0: not found
	 */
	public int firstkey(char[] word) {
		int i;
		int index;
		byte cs;

		index = this.head.s_node.child;
		cs = this.head.s_node.CS;

		i=0;
		while(cs != 0) {
			word[i] = this.search_word[i] = this.nf[index].node.K;
			this.search_idx[i]=index;
			i++;
			if(this.nf[index].node.I != 0) 
				break;
			cs = this.nf[index].node.CS;	/* 아래 문장과 순서 유지 */
			index = this.nf[index].node.child;
		}
		word[i]=0;
		return this.search_end=i;
	}
	
	/**
	 * 노드들을 free 시킨다. 보통 sibling을 묶어서 free 또는 alloc 한다.
	 * @param idx	free 시키려는 노드들의 시작 인덱스
	 * @param size	free 시키려는 노드의 갯수
	 * @return
	 */
	public int free(int idx, int size) {

		int i, prev_i=0;
		ST_FREE	start;

		if(size <=0)
			return -1;
		if(idx <=0 || idx+size>=this.head.n_size)
			return -1;

		i=this.head.s_free.next;

		if (i==0)			/* no free node */
		{
			this.head.s_free.next=idx;
			this.nf[idx].free.size=size;
			this.nf[idx].free.next=0;
			return 0;
		}

		if ( idx < i )		/* idx is the smallest in free */
		{
			this.head.s_free.next=idx;
			if (i == idx + size ) 
			{
				this.nf[idx].free.size=size + this.nf[i].free.size;
				this.nf[idx].free.next=this.nf[i].free.next;
			}
			else 	{
				this.nf[idx].free.size=size;
				this.nf[idx].free.next=i;
			}
			this.head.f_size += size;
			return 0;
		}

		while (i != 0 && i<idx)			/* other wise */
		{
			prev_i=i;
			i=this.nf[i].free.next;
		}
		start = this.nf[prev_i].free;	/* prev_i != 0 */

		if (idx+size == i)				/* 바로 다음 노드가 free노드인 경우*/
		{		
			size += this.nf[i].free.size;
			start.next = this.nf[i].free.next;
			this.head.f_size -= this.nf[i].free.size;
		}

		if (prev_i + start.size == idx )
			start.size += size;
		else 	{
			this.nf[idx].free.size = size;
			this.nf[idx].free.next = start.next;
			start.next = idx;
		}
		this.head.f_size += size;
		return	0;
	}

	public void init() {
		filename = "noname";
		flag = 0;
		change = 0; 
		search_end=0;

		head.version = ST_VER;
		head.n_size = ST_NF_DEFAULT;
		head.f_size = ST_NF_DEFAULT-1;

		head.s_node.K = 0;
		head.s_node.CS = 0;
		head.s_node.I = 0;
		head.s_node.child = 0;

		head.s_free.size = 0;
		head.s_free.next = 1; 

		/* 0번 노드 사용 안함 */
		nf[1].free.size = ST_NF_DEFAULT-1; 
		nf[1].free.next = 0; 
	}

	/**
	 * 
	 * @param word
	 * @param I
	 * @return	-1: fail, 0: duplicated, 1: success
	 */
	public int insert(char[] word, int I) {
		int child_index, new_index;
		int i,j,k;
		byte cs;
		ST_NODE parent;
		ST_NODE tmp_node = new ST_NODE();

		tmp_node.child = 0;
		tmp_node.CS = 0;
		tmp_node.I = 0;
		tmp_node.K = 0;

		k = 0;
		if(word.length == 0) 
			return -1;

		search(word);
		k += this.search_end;

		if(this.search_end == 0) 
			parent = this.head.s_node;
		else 
			parent = this.nf[this.search_idx[this.search_end - 1]].node;

		while(k < word.length) {
			cs = parent.CS;
			if(cs == 0) { 			/* child가 없는 경우 */
				new_index = alloc(1);
				node_copy(this.nf[new_index].node, tmp_node);
				this.nf[new_index].node.K = word[k];
				
				parent.CS = 1;
				parent.child = new_index;
				this.search_idx[this.search_end] = new_index;
				this.search_word[this.search_end] = word[k];
				this.search_end++;
				k++;
				parent = this.nf[new_index].node;
			}
			else {
				new_index = alloc(cs + 1);
				child_index = parent.child;
				for(i = 0; i < cs; i++) {
					if(this.nf[child_index + i].node.K < word[k]) {
						ST_NODE node = this.nf[new_index + i].node;
						this.nf[new_index + i].node = this.nf[child_index + i].node;
						this.nf[child_index + i].node = node;
					}
					else 
						break;
				}

				node_copy(this.nf[new_index + i].node, tmp_node);
				this.nf[new_index + i].node.K = word[k];

				this.search_idx[this.search_end] = new_index + i;
				this.search_word[this.search_end] = word[k];
				this.search_end++;
				k++;

				for(j = i; j < cs; j++) {
					ST_NODE node = this.nf[new_index + j + 1].node;
					this.nf[new_index + j + 1].node = this.nf[child_index + j].node;
					this.nf[child_index + j].node = node;
				}
					
				parent.child = new_index;
				parent.CS = (byte)(cs + 1);
				free(child_index, cs);

				parent = this.nf[new_index + i].node; 
			}
		}
		if(parent.I==0) {
			parent.I = I;
			return 1;
		}
		else
			return 0;
	}

	public int kcomp(ST_NODE a, ST_NODE b) {
		return (int)a.K - (int)b.K;
	}

	/**
	 * simti_search()를 활용하여 찾은 정보를 I_buffer에 저장한다.
	 * @param word
	 * @param I_buffer
	 * @return	찾은 문자열의 수, 0: not found
	 */
	public int lookup(char[] word, int[] I_buffer) {
		int	i;
		if (search(word)==0) 
			return 0;
		else
			for(i=0 ; i<this.search_end ; i++)
				I_buffer[i]=this.nf[this.search_idx[i]].node.I;
		return this.search_end;
	}
	
	/**
	 * Child, Sibling, Parent를 따라가며 다음 단어를 찾는다.
	 * @param word
	 * @return	찾은 문자열 수, 0: not found
	 */
	public int nextkey(char[] word) {
		int i;
		int index;
		byte cs;
		ST_NODE parent;

		if(this.search_end <= 0) 
			return 0;
		for(i = 0; i < this.search_end; i++) 
			word[i] = this.search_word[i];

		index = this.search_idx[i - 1]; 		/* i는 search_end와 값이 같다. */
		if(i == 1)
			parent = this.head.s_node;
		else
			parent = this.nf[this.search_idx[i - 2]].node;

		cs = this.nf[index].node.CS;

		/* parent -. index -. child */
		/*          sibling         */
		while(i > 0) {
			if(cs != 0) { 				/* child가 있는 경우 */
				parent = this.nf[index].node;
				index=this.nf[index].node.child;
				cs = this.nf[index].node.CS;

				word[i] = this.search_word[i] = this.nf[index].node.K;
				this.search_idx[i] = index;
				i++;
				if(this.nf[index].node.I != 0) 
					break;
			}
			else if( index < parent.child + parent.CS - 1) { 	/* sibling이 있는 경우; -1 에 주의 */
				index++;
				cs = this.nf[index].node.CS;

				word[i-1] = this.search_word[i-1] = this.nf[index].node.K;
				this.search_idx[i-1] = index;
				if(this.nf[index].node.I != 0) 
					break;
			}
			else { 						/* child도 sibling도 없는 경우 */
				i--;
				if(i<=0)
				{
					i=0;
					break;
				}
				index = this.search_idx[i-1];
				if(i == 1)
					parent = this.head.s_node;
				else
					parent = this.nf[this.search_idx[i-2]].node;
				cs = 0;
			}
		}
		word[i] = 0;
		return this.search_end=i;
	}
	
	private void node_copy(ST_NODE n1, ST_NODE n2) {
		n1.child = n2.child;
		n1.CS = n2.CS;
		n1.I = n2.I;
		n1.K = n2.K;
	}

	/**
	 * 
	 * @param word
	 * @param I
	 * @return	-1: fail, 1: success
	 */
	public int replace(char[] word, short I) {
		int i = 0;

		if(word.length == 0) 
			return -1;

		search(word);
		i += this.search_end;

		if(this.search_end == 0 || i < word.length) 
			return -1;
		else 
		{
			this.nf[this.search_idx[this.search_end - 1]].node.I = I;
			return 1;
		}
	}

	/**
	 * 
	 * @param word
	 * @return	찾은 문자열 수, 0: not found
	 */
	public int search(char[] word)	{
		int i, j, k;
		ST_NODE tmpnode = new ST_NODE();
		ST_NODE rnode = null;
		int child;
		byte cs;

		for(i = 0, j = 0; j < word.length && i < this.search_end; i++) {
			if(word[j] == this.search_word[i])
				j++;
			else break;
		}

		this.search_end = i;
		if(this.search_end == 0 ) {
			cs = this.head.s_node.CS;
			child = this.head.s_node.child;
		}
		else {
			child = this.search_idx[this.search_end-1];
			cs = this.nf[child].node.CS;
			child = this.nf[child].node.child;
		}
		while(j < word.length && cs != 0) {
			tmpnode.K=word[j];
			rnode = null;

			for(k = child; k < child + cs; k++){
				if(tmpnode.K == this.nf[k].node.K){
					rnode = this.nf[k].node;
					break;
				}
			}

			if(rnode == null) break;
			else {
				this.search_word[this.search_end] = word[j];
				this.search_idx[this.search_end] = k;
				this.search_end++;
				j++;
				child = this.nf[k].node.child;
				cs = this.nf[k].node.CS;
			}
		}
		return this.search_end;
	}

	public void state() {
		System.err.format("SIMTI state\n");
		System.err.format("\tname:%s\n", filename);
		System.err.format("\tversion:%s\n", head.version);
		System.err.format("\tflag:%c\n", flag);
		System.err.format("\tchange:%c\n", change);
		System.err.format("\tnsize:%d\n", head.n_size);
		System.err.format("\tfsize:%d\n", head.f_size);
	}
}
