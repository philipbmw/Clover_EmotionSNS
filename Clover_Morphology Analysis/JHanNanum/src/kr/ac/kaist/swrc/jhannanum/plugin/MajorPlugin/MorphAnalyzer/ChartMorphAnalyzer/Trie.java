package kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.share.Code;
import kr.ac.kaist.swrc.jhannanum.share.TagSet;

/**
 * Trie 자료구조.
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 */
public class Trie {
	public class FREE {	
		public int	size;
		public int next_idx;
	}
	public class INFO {
		public int tag;
		public int phoneme;		/* p_irr */
	}
	public class TNODE {
		public char	key;
		public short	child_size;
		public int		child_idx;
		public LinkedList<INFO> info_list = null;
		public FREE	free = new FREE();
	}
	final public static String TRIE_VERSION = "TRIE v0.1 (c) wjlee in KAIST\n";
	final public static int DEFAULT_TRIE_BUF_SIZE_SYS = 1060000;

	final public static int DEFAULT_TRIE_BUF_SIZE_USER = 106000;							
	final public static int FREE_NODE = 0;
	final public static int START_NODE = 1;

	public int		search_end;

	public int[]	search_idx = new int[256];		/* 이것은 검색된 path를 저장하는 공간 */
	public char[]	search_key = new char[256];		/* 사전의 내용을 listing할때 주로 사용 */

	public TNODE[]	trie_buf = null;

	public FREE free_head = null;

	public TNODE node_head = null;
	
	public Trie(int buf_size) {
		search_idx	= new int[256];
		search_key	= new char[256];
		search_end	= 0;

		trie_buf = new TNODE[buf_size];
		for (int i = 0; i < buf_size; i++) {
			trie_buf[i] = new TNODE();
		}

		free_head = trie_buf[FREE_NODE].free;
		node_head = trie_buf[FREE_NODE];
		
		node_head.key			= 0;
		node_head.child_size	= 0;
		node_head.info_list		= new LinkedList<INFO>();
		node_head.child_idx		= 0;

		free_head.size		= 0;
		free_head.next_idx	= 1;

		// 0번 노드 사용 안함 
		trie_buf[1].free.size 		= buf_size - 1;
		trie_buf[1].free.next_idx	= FREE_NODE;
	}

	public TNODE fetch(char[] word) {
		int idx;
		int x;

		x = search(word);
		if(x == 0){
			return null;
		}else{
			idx = this.search_idx[x - 1];
			return trie_buf[idx];
		}
	}

	public TNODE get_node(int idx) {
		return trie_buf[idx];
	}

	public int node_alloc(int size) {
		int idx;
		int pidx;

		if(size <= 0) {
			System.err.println("node alloc: wrong size");
			return 0;
		}

		pidx = FREE_NODE;

		for (idx = free_head.next_idx; idx != FREE_NODE; idx = trie_buf[idx].free.next_idx) {
			if (trie_buf[idx].free.size >= size) {
				break;
			}
			pidx = idx;
		}

		if(idx == 0) {
			System.err.println("node alloc: no space");
			return 0;
		}

		if(pidx == FREE_NODE) {
			if(size == trie_buf[idx].free.size) {
				free_head.next_idx = trie_buf[idx].free.next_idx;
			} else {
				trie_buf[idx + size].free.size = trie_buf[idx].free.size - size;
				trie_buf[idx + size].free.next_idx = trie_buf[idx].free.next_idx;
				free_head.next_idx = idx + size;
			}
		} else {
			if(size == trie_buf[idx].free.size) {
				trie_buf[pidx].free.next_idx = trie_buf[idx].free.next_idx;
			} else {
				trie_buf[idx + size].free.size = trie_buf[idx].free.size - size;
				trie_buf[idx + size].free.next_idx = trie_buf[idx].free.next_idx;
				trie_buf[pidx].free.next_idx = idx + size;
			}
		}
		
		return idx;
	}

	public void node_free(int fidx, int size)
	{
		int idx, pidx = 0;
		FREE start;

		if(size <= 0 || fidx <= FREE_NODE){
			System.err.println("node_free: wrong parameter");
			System.exit(0);
		}

		idx = free_head.next_idx;
		if (idx == FREE_NODE){
			// Free인 노드가 현재 존재하지 않는다.
			// Head의 포인터를 지금 막 free된 것으로 업데이트하고 종료.
			free_head.next_idx = fidx;					// 새로운 free node 덩어리의 시작 지점 --> fidx.
			trie_buf[fidx].free.size = size;			// 새로운 free node 덩어리의 크기 --> size.
			trie_buf[fidx].free.next_idx = FREE_NODE;	// 다음 free node 덩어리는 없다.
			return;
		}

		if (fidx < idx) {
			// 새 free node 덩어리가 현재 가장 앞에 있는 free node 덩어리의 앞에 존재.
			free_head.next_idx	= fidx;
			if (idx == fidx + size) {
				// 덩어리가 합쳐질 경우
				trie_buf[fidx].free.size	= size + trie_buf[idx].free.size;
				trie_buf[fidx].free.next_idx	= trie_buf[idx].free.next_idx;
			} else {
				// 덩어리가 분리될 경우				
				trie_buf[fidx].free.size = size;
				trie_buf[fidx].free.next_idx = idx;
			}
			return;
		}
		
		// 새 free node 덩어리와 현재 존재하는 free node 덩어리들간의 상대적 위치 파악
		while (idx != FREE_NODE && idx < fidx) {
			pidx	= idx;
			idx		= trie_buf[idx].free.next_idx;
		}
		start		= trie_buf[pidx].free;

		if (fidx + size == idx) {
			// 뒷쪽 덩어리와 합쳐지는 경우.
			// 기존에 있는 덩어리와 합쳐서 큰 덩어리를 만든 후, 그 큰 덩어리를 통째로 free하는 방식으로 해결.
			size				+= trie_buf[idx].free.size;
			start.next_idx		= trie_buf[idx].free.next_idx;
		}

		if (pidx + start.size == fidx) {
			// 앞쪽 덩어리와 합쳐지는 경우.
			// 그냥 기존 free 덩어리의 size 증가.
			start.size			+= size;
		} else {
			// 새 덩어리 생성 및 인덱스 업데이트.
			trie_buf[fidx].free.size		= size;
			trie_buf[fidx].free.next_idx	= start.next_idx;
			start.next_idx					= fidx;
		}
	}

	public int node_look(char key, int idx){
		TNODE parent;

		if (idx == 1) {
			parent			= node_head;
		} else {
			parent			= trie_buf[idx];
		}

		for (int i = parent.child_idx; i < parent.child_idx + parent.child_size; i++){
			if(trie_buf[i].key == key) {
				return i;
			}
		}
		return 0;
	}

	public void print_result(TagSet tagSet) {
		try {
			PrintWriter pw = new PrintWriter("data/kE/output.txt");
			for (int k = 0; k < node_head.child_size; k++) {
				print_trie(pw, node_head.child_idx + k, 0, tagSet);
			}
			for (int ii = free_head.next_idx; ii != 0; ii = trie_buf[ii].free.next_idx) {
				pw.print("[n:" + ii + " s:" + trie_buf[ii].free.size + "] ");
			}
			pw.println();
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void print_trie(PrintWriter pw, int idx, int depth, TagSet tagSet) {
		for (int i = 0; i < depth; i++) {
			pw.print("\t");
		}
		pw.print(idx + ":" + Code.toCompatibilityJamo(trie_buf[idx].key) + " ");
		if (trie_buf[idx].info_list != null) {
			for (int k = 0; k < trie_buf[idx].info_list.size(); k++) {
				pw.print("t:" + tagSet.getTagName(trie_buf[idx].info_list.get(k).tag) + " ");
			}
		}
		pw.println();
		for (int i = 0; i < trie_buf[idx].child_size; i++) {
			print_trie(pw, trie_buf[idx].child_idx + i, depth + 1, tagSet);
		}
	}

	public void read_dic(String dictionaryFileName, TagSet tagSet) throws IOException{
		String str			= "";

		BufferedReader in	= new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName)));
		INFO[] info_list		= new INFO[255];
		for (int i = 0; i < 255; i++) {
			info_list[i] = new INFO();
		}

		while((str = in.readLine()) != null){
			str.trim();
			if(str.equals("")){
				continue;
			}

			StringTokenizer tok	= new StringTokenizer(str, "\t ");
			String word			= tok.nextToken();
			int isize			= 0;

			while (tok.hasMoreTokens()) {
				String data				= tok.nextToken();
				StringTokenizer tok2	= new StringTokenizer(data, ".");
				String curt				= tok2.nextToken();
				int x					= tagSet.getTagID(curt);
				if (x == -1) {
					System.err.println("read_dic:tag error");
					continue;
				}

				if(tok2.hasMoreTokens()){
					info_list[isize].phoneme	= (short)tagSet.getIrregularID(tok2.nextToken());
				}else{
					info_list[isize].phoneme	= TagSet.PHONEME_TYPE_ALL;
				}

				info_list[isize].tag	= x;
				isize++;
			}
			info_list[isize].tag		= 0;
			info_list[isize].phoneme	= 0;

			char[] word3 = Code.toTripleArray(word);
			for(int i = 0; i < isize; i++){
				store(word3, info_list[i]);
			}
		}
	}
	
	/**
	 * 
	 * @param word
	 * @return search_end, 0: 검색결과 없음, 
	 */
	public int search(char[] word){
		TNODE rnode;
		int child;
		short cs;
		char key;

		int widx = 0;
		int nidx = 0;
		int i = 0;

		// cache 개념: 이전에 검색했던 결과를 그대로 갖다 써와도 된다면 그냥 가져온다.
		for (i = 0; widx < word.length && i < this.search_end; i++) {
			if (word[i] == this.search_key[i]) {
				widx++;
			} else {
				break;
			}
		}

		this.search_end	= i;
		if (this.search_end == 0) {
			// 이전 정보가 없어 처음부터 다 검사해야 하는 경우
			cs		= node_head.child_size;
			child	= node_head.child_idx;
			nidx = 0;
		} else {
			// 이전 정보가 있어서 거기서부터 시작.
			child	= search_idx[search_end - 1];
			cs		= trie_buf[child].child_size;
			child	= trie_buf[child].child_idx;
			nidx = search_idx[search_end - 1];
		}
		
		while (widx < word.length) {
			if (cs == 0) {
				return 0;
			}
			
			// 자식들을 뒤지며 현재 보고 있는 단어의 문자와 일치한 것이 있는지 찾는다.
			key = word[widx];
			rnode = null;
			nidx = 0;
			for (int j = child; j < child + cs; j++) {
				if (key	== trie_buf[j].key) {
					rnode = trie_buf[j];
					nidx = j;
					break;
				}
			}

			if (rnode == null) {
				// 단어와 tree가 매칭되는 부분 종료.
				break;
			} else {
				// 단어와 tree가 아직 계속 매칭되고 있음. 
				search_key[search_end]	= key;
				search_idx[search_end]	= nidx;
				search_end++;
				widx++;
				child = trie_buf[nidx].child_idx;
				cs = trie_buf[nidx].child_size;
			}
		}
		
		if (trie_buf[nidx].info_list == null || trie_buf[nidx].info_list.size() == 0) {
			return 0;
		} else {
			return search_end;
		}
	}
	
	public int store(char[] word, INFO inode) {
		int child_index, new_index;
		int i, j;
		int widx;

		TNODE parent;

		if(word.length == 0) {
			return -1;
		}

		// 주어진 단어와 기존 단어 검색 트리와의 비교.
		search(word);

		// 일치되는 부분을 제외한 나머지 부분을 트리에 삽입.
		widx = search_end;
		if (search_end == 0) {
			parent	= node_head;
		} else {
			parent	= trie_buf[search_idx[search_end - 1]];
		}

		while (widx < word.length) {
			char c = word[widx];
			short cs = parent.child_size;
			if (cs == 0) {
				// 현재 보고 있는 트리에 자식이 없을 경우: 자식을 하나 생성하고 거기에 주어진 단어의 정보 삽입.
				new_index							= node_alloc(1);
				trie_buf[new_index].key				= c;
				trie_buf[new_index].child_idx		= 0;
				trie_buf[new_index].child_size		= 0;
				parent.child_size					= 1;
				parent.child_idx					= new_index;
				search_idx[search_end]	= new_index;
				search_key[search_end]	= c;
				search_end++;
				widx++;
				parent								= trie_buf[new_index];
			} else {
				// 현재 보고 있는 트리에 자식이 있을 경우: 새로운 공간 (cs + 1) 개를 생성하고, 거기에 자식들을 모두 copy & 새로운 자식 삽입.
				new_index							= node_alloc(cs + 1);
				child_index							= parent.child_idx;
				for (i = 0; i < cs; i++) {
					if (trie_buf[child_index + i].key < c) {
						TNODE tmp = trie_buf[new_index + i]; 
						trie_buf[new_index + i] = trie_buf[child_index + i];
						trie_buf[child_index + i] = tmp;
					} else {
						break;
					}
				}
				trie_buf[new_index + i].key			= c;
				trie_buf[new_index + i].child_idx	= 0;
				trie_buf[new_index + i].child_size	= 0;
				search_idx[search_end]	= new_index + i;
				search_key[search_end]	= c;
				search_end++;
				widx++;

				for(j = i; j < cs; j++){
					TNODE tmp = trie_buf[new_index + j + 1];
					trie_buf[new_index + j + 1]	= trie_buf[child_index + j];
					trie_buf[child_index + j] = tmp;
				}

				parent.child_idx		= new_index;
				parent.child_size		= (short)(cs + 1);

				node_free(child_index, cs);
				parent = trie_buf[new_index + i];
			}
		}

		// 주어진 정보를 대상 단어에 삽입.
		if(parent.info_list == null) {
			parent.info_list = new LinkedList<INFO>();
		}
		
		INFO in = new INFO();
		in.phoneme = inode.phoneme;
		in.tag = inode.tag;

		parent.info_list.add(in);
		
		return 0;
	}

	//TRIE * trie_open(char *fname, int flag, int itype)
	//{
	//	TRIE *tf;
	//
	//	tf = (TRIE *)malloc(sizeof(TRIE));
	//	tf.info_type = itype;
	//	tf.cache = dcache_open(fname, flag, sizeof(TNODE));
	//
	//	if(tf.cache.fend==0) /* new file */
	//	{
	//	    /* initialize header & starting information */
	//		TNODE *tn;
	//		FREE *fn;
	//		char *p;
	//
	//		p=(char *)dc_add_w(tf.cache,0);
	//		memcpy(p,TRIE_VERSION, TRIE_HEAD_SYSTEM);
	//
	//		fn=getfaddr(tf,FREE_NODE);
	//		fn.rchild = 2;		/* 2 is the first free node */
	//
	//		tn=getnaddr(tf,START_NODE);
	//		tn.child_size = 0;	/* child_size == 0 means no child */
	//		tn.child_idx = 0;	/* child_idx ==0, no child */
	//
	//		fn=getfaddr(tf,2); 	/* real free node */
	//		fn.size = 0;		/* size == 0 , infinitive size */
	//		fn.lchild = 0;		/* 0 means no child*/
	//		fn.rchild = 0;		/* 0 means no child */
	//		
	//		tf.state=TRIE_STATE_NEW;
	//	}
	//	else /* 기존의 사전 */
	//		tf.state=TRIE_STATE_OLD;
	//
	//	tf.search_end = 0;
	//	return tf;
	//}
	//
	///*
	//	trie_close()는 trie을 정리하고 닫는다.
	//	PUBLIC
	//*/
	//
	//void trie_close(TRIE *tf)
	//{
	//#ifndef _MSC_VER
	//	int file_size;
	//
	//	file_size=(getfnode(tf,FREE_NODE).rchild + 1 + DC_PAGESIZE) * sizeof(TNODE);
	//	dcache_truncate(tf.cache,file_size);
	//#endif
	//	dcache_close(tf.cache);
	//	free(tf);
	//}
	//
	///* 
	//   low leve node copy 
	//   :hidden
	//*/
	//void buf_n_cpy(TRIE *tf, int des, int src, int n)
	//{
	//	while(n-.0)
	//		putnode(tf,des++,getnode(tf,src++));
	//}
	//
	///* 
	//   trie node의 한 sibling안에 char k가 있는지를 검사하는 함수
	//   :hidden
	//*/
	//TNODE *node_search(TRIE *tf, char k, int idx, int size, int *found)
	//{
	//	long l,m,u;	/* 여기는 반드시 long으로 해야함 */
	//	TNODE *node;
	//
	//	l=0;
	//	u=size-1;
	//	while(l<=u) 
	//	{
	//		m = (l+u)/2;
	//		
	//		node = getnode(tf,idx+m);
	//		if(k  > node.key)  
	//			l = m+1;
	//		else if(k == node.key)  
	//		{
	//			*found = m;
	//			return node;
	//		}
	//		else /* if(k  < node.key)  */
	//			u = m-1;
	//	}
	//	*found = l;/* key에 가까운(하나 위) 인덱스를 리턴 */
	//	return NULL;
	//}
	//
	//int inpcmp(INFO *ip1,INFO *ip2)
	//{
	//	int i,j;
	//	char *p,*q;
	//	j=sizeof(INFO);
	//	p = (char *)ip1; q = (char *)ip2;
	//	for(i=0;i<j;i++,p++,q++)
	//		if(*p>*q) return 1;
	//		else if(*p<*q) return -1;
	//	return 0;
	//}
	///* 
	//   info node의 한 sibling안에 *ip가 있는지를 검사하는 함수
	//   :hidden
	//*/
	//INFO *info_search(TRIE *tf, INFO *ip, int idx, int size, int *found)
	//{
	//	long l,m,u;	/* 여기는 반드시 long으로 해야함 */
	//	INFO *inode;
	//	int re;
	//
	//	l=0;
	//	u=size-1;
	//	while(l<=u) 
	//	{
	//		m = (l+u)/2;
	//		
	//		inode = getinode(tf,idx+m);
	//		re = inpcmp(ip,inode);
	//		if(re > 0) 
	//			l = m+1;
	//		else if(re == 0)  
	//		{
	//			*found = m;
	//			return inode;
	//		}
	//		else /* if(re  < 0)  */
	//			u = m-1;
	//	}
	//	*found = l;/* ip에 가까운(하나 위) 인덱스를 리턴 */
	//	return NULL;
	//}
	//
	///*
	//   trie node 즉, str에 해당하는 path node 만을 만드는 함수 
	//   : hidden
	//*/
	//int trie_insert(TRIE *tf, char *str)
	//{
	//	TNODE *pn; 		/* previous node*/
	//	int idx, pidx;
	//	short size;
	//	int i;
	//
	//	if(str == NULL|| *str == 0)
	//		return 0;
	//
	//	for(i=0;i<tf.search_end && tf.search_key[i] == str[i];i++)
	//		;
	//	if(i==0)
	//	{
	//		tf.search_end = 0;
	//		tf.search_idx[0] = START_NODE;
	//		pidx = START_NODE;
	//	}
	//	else
	//	{
	//		pidx = tf.search_idx[i];
	//		tf.search_end = i;
	//		str += i;
	//	}
	//
	//	while(*str)
	//	{
	//		pn = getnode(tf,pidx);
	//		idx = pn.child_idx;
	//		size = pn.child_size;
	//		if(size == 0)			/* child가 없으면 */
	//		{
	//			TNODE tn={0,0,0,0,0,0};
	//			idx = node_alloc(tf,1);
	//			tn.key = *str;
	//			putnode(tf,idx,&tn);
	//			pn=getnaddr(tf,pidx);
	//			pn.child_size = 1;
	//			pn.child_idx = idx;
	//		}
	//		else
	//		{
	//
	//			int i_pos;
	//			if(node_search(tf,*str,idx,size,&i_pos) == NULL)
	//			{
	//				int newidx;
	//				TNODE tn={0,0,0,0,0,0};
	//
	//				newidx = node_alloc(tf,size+1);
	//
	//				pn=getnaddr(tf,pidx);
	//				pn.child_size++;
	//				pn.child_idx = newidx;
	//
	//				buf_n_cpy(tf,newidx,idx,i_pos);
	//				tn.key = *str;
	//				putnode(tf,newidx+i_pos,&tn);
	//				buf_n_cpy(tf,newidx+i_pos+1,idx+i_pos,size-i_pos);
	//
	//				node_free(tf,idx,size);
	//				idx = newidx+i_pos;
	//			}
	//			else
	//				idx = idx+i_pos;
	//		}
	//		tf.search_key[tf.search_end++] = *str++;
	//		tf.search_idx[tf.search_end] = idx;
	//		pidx = idx;
	//	}
	//	return pidx;
	//}
	///* 
	//   :hidden
	//	trie_search()는 내부적으로만 쓰이는 함수 입니다.
	//	return 값에 문제가 있다.
	//*/
	//
	//int trie_search(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	int idx,tidx;
	//	short size;
	//	int i;
	//
	//	if(str == NULL|| *str == 0)
	//		return -1;
	//
	//	for(i=0;i<tf.search_end && tf.search_key[i] == str[i];i++)
	//		;
	//	if(i==0)
	//	{
	//		tf.search_end = 0;
	//		tf.search_idx[0] = START_NODE;
	//		node = getnode(tf,START_NODE);
	//	}
	//	else
	//	{
	//		node = getnode(tf,tf.search_idx[i]);
	//		tf.search_end = i;
	//		str += i;
	//	}
	//
	//	while(*str)
	//	{
	//		size = node.child_size;
	//		if(size == 0)
	//			return 0;
	//		else
	//		{
	//			idx = node.child_idx;
	//			node = node_search(tf,*str,idx,size,&tidx);
	//			if(node == NULL)
	//				return 0;
	//			idx += tidx;
	//			tf.search_key[tf.search_end++] = *str++;
	//			tf.search_idx[tf.search_end] = idx;
	//			node = getnode(tf,idx);
	//		}
	//	}
	///*
	//prn_tnode(node);
	//*/
	//	if(node.info_size == 0)
	//		return 0;
	//	else
	//		return tf.search_end;
	//}
	//
	//
	///*
	//   : hidden
	//	path node들을 지운다.... info가 없으면
	//*/
	//int trie_remove_path(TRIE *tf)
	//{
	//	TNODE *node;
	//	TNODE pnode;
	//	int idx, pidx, d_pos,size, tidx, newidx;
	//
	//	while(tf.search_end)
	//	{
	//		idx=tf.search_idx[tf.search_end];
	//		node=getnode(tf,idx);
	//		if(node.child_size != 0 || node.info_size != 0)
	//		    break;
	//
	//		/* child도 없고 data도 없는 경우에 계속 지운다.*/
	//		pidx=tf.search_idx[tf.search_end-1];
	//		pnode = *getnode(tf,pidx);
	//		if(pnode.child_size == 1)
	//		{
	//			node_free(tf,pnode.child_idx,1);
	//			pnode.child_size = 0;
	//			pnode.child_idx = 0;
	//		}
	//		else
	//		{
	//			tidx = pnode.child_idx;
	//			d_pos = idx - tidx;
	//			size = pnode.child_size;
	//
	//			newidx = node_alloc(tf,size-1);
	//			buf_n_cpy(tf,newidx,tidx,d_pos);
	//			buf_n_cpy(tf,newidx+d_pos,idx+1,size-d_pos-1);
	//			node_free(tf,tidx,size);
	//			pnode.child_size--;
	//			pnode.child_idx = newidx;
	//		}
	//		putnode(tf,pidx,&pnode);
	//		tf.search_end--;
	//	}
	//	return 1;
	//}
	//
	//
	///*----------------------------------------
	//  <section> trie basic interface
	//  :public, trie를 기본으로 하는 함수들의 공통 public 함수들
	//  
	//    void trie_write_user_head(TRIE *tf, char *thead);
	//    void trie_write_user_head(TRIE *tf, char *thead);
	//    int trie_firstkey(TRIE *tf, char *str); 
	//    int trie_lastkey(TRIE *tf, char *str);  
	//    int trie_nextkey(TRIE *tf, char *str); 
	//    int trie_prevkey(TRIE *tf, char *str); 
	//    TRIE *trie_pack(TRIE *tf); 
	//----------------------------------------------------------------------*/
	//void trie_write_user_head(TRIE *tf, char *thead)
	//{
	//    char *p;
	//    p=(char *)dc_add_w(tf.cache,TRIE_HEAD_SYSTEM/sizeof(TNODE));
	//    memcpy(p,thead,TRIE_HEAD_USER);
	//}
	//
	//void trie_read_user_head(TRIE *tf, char *thead)
	//{
	//    char *p;
	//    p=(char *)dc_add_r(tf.cache,TRIE_HEAD_SYSTEM/sizeof(TNODE));
	//    memcpy(thead,p,TRIE_HEAD_USER);
	//
	//}
	//int trie_firstkey(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	int idx;
	//	int i=0;
	//
	//	tf.search_end = 0;
	//	tf.search_idx[0] = START_NODE;
	//	node = getnode(tf,START_NODE);
	//
	//	while(node.child_size)
	//	{
	//		idx = node.child_idx;
	//		node=getnode(tf,idx);
	//		str[i++] = tf.search_key[tf.search_end++] = node.key;
	//		tf.search_idx[tf.search_end] = idx;
	//		if(node.info_size)
	//			break;
	//	}
	//	str[i] = 0;
	//	return i ;
	//
	//}
	//int trie_nextkey(TRIE *tf, char *str)
	//{
	//	TNODE *node,*pnode;
	//	int idx,pidx;
	//	int i;
	//	int children, younger_brothers;
	//
	//	if(tf.search_end > 0)
	//	{
	//		idx = tf.search_idx[tf.search_end];
	//		node = getnode(tf,idx);
	//		children = node.child_size;
	//
	//		pidx = tf.search_idx[tf.search_end-1];
	//		pnode = getnode(tf,pidx);
	//		younger_brothers = pnode.child_idx + pnode.child_size - idx - 1;
	//	}
	//
	//	while(tf.search_end > 0)
	//	{
	//
	//		if(children)
	//		{
	//			/* DOWN */
	//			idx = node.child_idx;
	//			node=getnode(tf,idx);
	//			tf.search_key[tf.search_end++] = node.key;
	//			tf.search_idx[tf.search_end] = idx;
	//			if(node.info_size > 0)
	//			    	break;
	//			younger_brothers = children-1;
	//			children = node.child_size;
	//		}
	//		else if(younger_brothers) /* no children && younger_brothers */
	//		{
	//			/* RIGHT */
	//			idx++;
	//			node = getnode(tf,idx);
	//			tf.search_key[tf.search_end-1] = node.key;
	//			tf.search_idx[tf.search_end] = idx;
	//			if(node.info_size > 0)
	//			    	break;
	//			younger_brothers--;
	//			children = node.child_size;
	//		}
	//		else /* no children && no younger_brothers */
	//		{
	//			/* UP */
	//			tf.search_end--;
	//			if(tf.search_end == 0)
	//				break;
	//
	//			idx = tf.search_idx[tf.search_end];
	//			children = 0;
	//			pidx = tf.search_idx[tf.search_end-1];
	//			pnode = getnode(tf,pidx);
	//			younger_brothers = pnode.child_idx+pnode.child_size - idx - 1;
	//		}
	//	}
	//	for(i=0;i<tf.search_end;i++)
	//		str[i] = tf.search_key[i];
	//	str[i] = 0;
	//	return i ;
	//}
	//int trie_lastkey(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	int idx;
	//	int i=0;
	//
	//	tf.search_end = 0;
	//	tf.search_idx[0] = START_NODE;
	//	node = getnode(tf,START_NODE);
	//
	//	while(node.child_size)
	//	{
	//		idx = node.child_idx + node.child_size - 1;
	//		node=getnode(tf,idx);
	//		str[i++] = tf.search_key[tf.search_end++] = node.key;
	//		tf.search_idx[tf.search_end] = idx;
	//	}
	//	str[i] = 0;
	//	return i ;
	//}
	//
	//int trie_prevkey(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	int idx,pidx;
	//	int i;
	//
	//	idx = tf.search_idx[tf.search_end];
	//
	//	while(tf.search_end > 0)
	//	{
	//		pidx = tf.search_idx[tf.search_end-1];
	//		if(getnode(tf,pidx).child_idx < idx) /* elder_brothers */
	//		{
	//			/* LEFT */
	//			idx--;
	//			node = getnode(tf,idx);
	//			tf.search_key[tf.search_end-1] = node.key;
	//			tf.search_idx[tf.search_end] = idx;
	//
	//			/* DOWN */
	//			while(node.child_size)
	//			{
	//			    idx = node.child_idx+node.child_size-1;
	//			    node=getnode(tf,idx);
	//			    tf.search_key[tf.search_end++] = node.key;
	//			    tf.search_idx[tf.search_end] = idx;
	//			}
	//			if(node.info_size > 0)
	//				break;
	//		}
	//		else /* parent */
	//		{
	//			/* UP */
	//			tf.search_end--;
	//			if(tf.search_end == 0)
	//				break;
	//
	//			idx = tf.search_idx[tf.search_end];
	//			node = getnode(tf,idx);
	//			if(node.info_size > 0)
	//				break;
	//		}
	//	}
	//	for(i=0;i<tf.search_end;i++)
	//		str[i] = tf.search_key[i];
	//	str[i] = 0;
	//	return i ;
	//}
	//
	///* trie_pack에는 아직 문제가 있어서 사용할 수 없다. */
	//int tnode_pack(TRIE *tf, TRIE *tt, short size, int idx)
	//{
	//	int tidx;
	//	int i,j;
	//	TNODE tnode, *node;
	//	INFO *inodep;
	//
	//	if(size == 0)
	//		return 0;
	//
	//	tidx = node_alloc(tt,size);
	//	for(i=0;i<size;i++)
	//	{
	//		node = getnode(tf,idx+i);
	//		tnode.key = node.key;
	//		tnode.info_size = node.info_size;
	//		tnode.child_size = node.child_size;
	//		tnode.child_idx=node.child_idx;
	//		if(tnode.info_size > 0)
	//		{
	//			tnode.info_idx = node_alloc(tt,tnode.info_size);
	//			for(j=0;j<tnode.info_size;j++)
	//			{
	//				inodep = getinode(tf,node.info_idx+j);
	//				putinode(tt,tnode.info_idx+j,inodep);
	//			}
	//		}
	//		else
	//			tnode.info_idx = 0;
	//		tnode.child_idx = tnode_pack(tf,tt,tnode.child_size, tnode.child_idx);
	//		putnode(tt,tidx+i,&tnode);
	//	}
	//	return tidx;
	//}
	//
	//final public static String TRIE_FILE_SUFFIX ".tri"
	//final public static String OLD_FILE_SUFFIX ".old"
	//final public static String TMP_FILE_SUFFIX ".tmp"
	//
	//TRIE *trie_pack(TRIE *tf)
	//{
	//	char newfilename[256];
	//	char oldfilename[256];
	//	char triefilename[256];
	//	char headbuff[TRIE_HEAD_SIZE];
	//	TRIE *tt;
	//	TNODE tnode,*node;
	//	int oldflag;
	//	int olditype;
	//
	//	oldflag=tf.cache.flag;
	//	olditype=tf.info_type;
	//
	//	strcpy(triefilename,tf.cache.fname);
	//	strcpy(newfilename,tf.cache.fname);
	//	strcat(newfilename,TMP_FILE_SUFFIX);
	//	strcpy(oldfilename,tf.cache.fname);
	//	strcat(oldfilename,OLD_FILE_SUFFIX);
	//
	//	unlink(newfilename);
	//	tt = trie_open(newfilename, FILE_FLAG_W, olditype);
	//	trie_read_user_head(tf,headbuff);
	//	trie_write_user_head(tt,headbuff);
	//
	//	node = getnode(tf,START_NODE);
	//	tnode.child_size = node.child_size;
	//	tnode.child_idx = tnode_pack(tf,tt,node.child_size,node.child_idx);
	//	putnode(tt,START_NODE,&tnode);
	//
	//	fprintf(stderr,"trie packed %s!!\n",newfilename);
	//	fprintf(stderr,"closing %s\n",newfilename);
	//	fprintf(stderr,"closing %s\n",tf.cache.fname);
	//	trie_close(tt);
	//	trie_close(tf);
	//	unlink(oldfilename);
	//	rename(triefilename,oldfilename);
	//	fprintf(stderr,"rename %s -. %s\n",triefilename,oldfilename);
	//	rename(newfilename,triefilename);
	//	fprintf(stderr,"rename %s -. %s\n",newfilename,triefilename);
	//	fprintf(stderr,"opening new trie %s",triefilename);
	//	return trie_open(triefilename, oldflag, olditype);
	//}
	//
	///*----------------------------------------
	//  <section> application I -- TRIE
	//  : public
	//
	//  형태소 분석기를 위한 사전구조, INFO 노드를 충분히 활용한다.
	//----------------------------------------------------------------------*/
	//#ifdef DEBUG_LOOK
	//long wjdsearch=0;
	//#endif
	//
	//final public static String TRIE_FILE_SUFFIX ".mdc"
	//
	//TRIE * mdic_open(char *fname, char type)
	//{
	//	int flags;
	//	char filename[256];
	//
	//	if(type == 'r')
	//		flags = FILE_FLAG_R ;
	//	else if(type == 'w')
	//		flags = FILE_FLAG_W ;
	//	else
	//	{
	//		fprintf(stderr,"mdic_open:open_type unknown '%c'\n",type);
	//		return NULL;
	//	}
	//
	//	strcpy(filename,fname);
	//	strcat(filename,TRIE_FILE_SUFFIX);
	//	return (TRIE *)trie_open(filename,flags,ITYPE_TRIE);
	//}
	//void mdic_close(TRIE *tf)
	//{
	//#ifdef DEBUG_LOOK
	//	fprintf(stderr,"mdic lookup %d\n",wjdsearch);
	//#endif
	//
	//	trie_close((TRIE *)tf);
	//}
	//
	///* 
	//   한 단어 입력
	//*/
	//
	//int mdic_store(TRIE *tf, char *word, INFO *inode)
	//{
	//	TNODE node; /* previous node and current node */
	//	INFO *i_node;
	//	int idx, info;
	//
	//	idx=trie_insert(tf,word);
	//	if(idx==0)
	//		return 0;
	//
	//	node = *getnode(tf,idx);
	//
	//	if(node.info_size == 0)
	//	{
	//		info = node_alloc(tf,1); /* wjlee bug fix . size+1 -. 1 */
	//		node.info_idx = info;
	//		node.info_size = 1;
	//		putnode(tf,idx,&node);
	//		putinode(tf,info,inode);
	//	}
	//	else
	//	{
	//		int i_pos;
	//
	//		i_node = info_search(tf,inode,node.info_idx,node.info_size,&i_pos);
	//		if(i_node == NULL)
	//		{
	//			info = node_alloc(tf,node.info_size+1);
	//			buf_n_cpy(tf,info,node.info_idx,i_pos);
	//
	//			putinode(tf,info+i_pos,inode);
	//			buf_n_cpy(tf,info+i_pos+1,node.info_idx+i_pos,node.info_size-i_pos);
	//			node_free(tf,node.info_idx,node.info_size);
	//			node.info_size++;
	//			node.info_idx = info;
	//			putnode(tf,idx,&node);
	//		}
	//		else
	//		{
	//			fprintf(stderr,"%s:warning:'%s' infomation duplicated!\n",__FILE__,word);
	//		}
	//	}
	//	return 0;
	//}
	//
	///*
	//	look up substring
	//	PUBLIC
	//*/
	//int mdic_lookup(TRIE *tf, char *str, TNODE tpath[], int tp_size)
	//{
	//	int i;
	//
	//#ifdef DEBUG_LOOK
	//	wjdsearch++;
	//#endif
	//
	//	if(trie_search(tf,str) < 0)
	//		return -1; 		/* 전혀 못찾은 경우 */
	//	if(tp_size < tf.search_end -1)
	//		return -2; 		/* tp_size를 키워야 한다 */
	//	else
	//	{
	//		for(i=0;i<tf.search_end;i++)
	//			tpath[i] = *getnode(tf,tf.search_idx[i+1]);
	//		return i; /* path length 0..n */
	//	}
	//}
	//
	///*
	//   new 990830
	//   한 노드씩 전진하기 위하여
	//*/
	//
	///* start from idx = 1*/
	//int mdic_node_look(TRIE *tf, char key, int idx)
	//{
	//	int t;
	//	TNODE *node;
	//	node = getnode(tf,idx);
	//
	//	if(node.child_size != 0 && 
	//		node_search(tf,key,node.child_idx,node.child_size,&t) != NULL)
	//		    return node.child_idx+t;
	//	return 0;
	//}
	//
	///*
	//exact match
	//return value means
	//	-1 : error input
	//	0 : not exist
	//	other : the idx of TNODE that includes info_idx
	//	PUBLIC
	//*/
	//int mdic_fetch(TRIE *tf, char *str, INFO *inode)
	//{
	//	TNODE *node;
	//	int idx;
	//	int isize, iidx;
	//	int i;
	//	int x;
	//
	//	x = trie_search(tf,str);
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//	    	idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		isize=node.info_size;
	//		iidx=node.info_idx;
	//		for(i=0;i<isize;i++)
	//			inode[i] = *getinode(tf,iidx+i);
	//		return isize;
	//	}
	//}
	///*
	//	PUBLIC
	//*/
	//int mdic_delete(TRIE *tf, char *str, INFO *ip)
	//{
	//	TNODE node;
	//	int idx;
	//	int x;
	//
	//	x = trie_search(tf,str); /* 일단 찾아 본다.*/
	//
	//	if(x <= 0 ) /* 이 부분 고쳤음 .... delete문제가 있었어 */
	//		return x;
	//
	//	idx=tf.search_idx[x];
	//	node = *getnode(tf,idx);
	//	if(node.info_size != 0)
	//	{
	//		int i_pos;
	//		int info;
	//		INFO *i_node;
	//
	//		i_node = info_search(tf,ip,node.info_idx,node.info_size,&i_pos);
	//		if(i_node != NULL)
	//		{
	//			if(node.info_size > 1)
	//			{
	//				info = node_alloc(tf,node.info_size-1);
	//				buf_n_cpy(tf,info,node.info_idx,i_pos);
	//				buf_n_cpy(tf,info+i_pos,node.info_idx+i_pos+1,node.info_size-i_pos-1);
	//			}
	//			else
	//				info=0;
	//			node_free(tf,node.info_idx,node.info_size);
	//			node.info_size--;
	//			node.info_idx = info;
	//			putnode(tf,idx,&node);
	//		}
	//		else
	//			return 0;
	//	}
	//	trie_remove_path(tf);
	//	return 1;
	//}
	//
	///*----------------------------------------
	//  <section> application II -- TRIE
	//  : public
	//
	//    TRIE : trie based dbm like file system
	//    이 부분은 dbm이 단어수 10만을 넘어가면 속도가 너무 느려지는
	//    문제로(5만에서 30초, 25만에서 50분), trie를 이용하여 dbm과
	//    유사하게 만들었다.
	//    결과로 속도가 25만 단어에 5분정도 걸린다.
	//    단 이름은 비슷하지만 사용 방법에(parameter) 약간의 차이가 있다.
	//----------------------------------------------------------------------*/
	//
	//final public static String TRIE_FILE_SUFFIX ".tdb"
	//
	//TRIE * tdbm_open(char *fname, char type)
	//{
	//	int flags;
	//	char filename[256];
	//
	//	if(type == 'r')
	//		flags = FILE_FLAG_R ;
	//	else if(type == 'w')
	//		flags = FILE_FLAG_W ;
	//	else
	//	{
	//		fprintf(stderr,"tdbm_open:open_type unknown '%c'\n",type);
	//		return NULL;
	//	}
	//
	//	strcpy(filename,fname);
	//	strcat(filename,TRIE_FILE_SUFFIX);
	//	return (TRIE *)trie_open(filename,flags,ITYPE_TRIE);
	//}
	//
	//
	//void tdbm_close(TRIE *tf)
	//{
	//    trie_close((TRIE *)tf);
	//}
	//
	//int tdbm_store(TRIE *tf, char *word, char *data, int store_mode)
	//{
	//	TNODE node; /* previous node and current node */
	//	int idx;
	//	INFO *inode;
	//	INFO_HEAD head; 	/* data size를 크게하기 위해 head를 둔다.*/
	//	int i;
	//
	//	idx=trie_insert(tf,word);
	//	if(idx==0)
	//		return -1;
	//
	//	node = *getnode(tf,idx);
	//
	//	if(node.info_size != 0)
	//		if(store_mode == TRIE_INSERT)
	//			return 1; /* exist */
	//		else
	//		{
	//			head=*(INFO_HEAD*)getnode(tf,node.info_idx);
	//			node_free(tf,head.info,head.size);
	//		}
	//	else
	//	{
	//		node.info_idx=node_alloc(tf,1);
	//		node.info_size=1;
	//		putnode(tf,idx,&node);
	//	}
	//
	//	head.size=strlen(data)/12+1; /* sizeof INFO +1 */
	//	head.info=node_alloc(tf,head.size);
	//	putinode(tf,node.info_idx,&head);
	//
	//	inode=(INFO *)data;
	//	for(i=0;i<head.size;i++,inode++)
	//		putinode(tf,head.info+i,inode);
	//
	//	return 0;
	//}
	//
	//int tdbm_fetch(TRIE *tf, char *str, char *data)
	//{
	//	TNODE *node;
	//	INFO *inode;
	//	INFO_HEAD head;
	//	int idx;
	//	int i;
	//	int x;
	//
	//	inode=(INFO *)data;
	//	data[0]=0;
	//
	//	x = trie_search(tf,str);
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//		idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		if(node.info_size)
	//		    head = *(INFO_HEAD *)getnode(tf,node.info_idx);
	//		for(i=0;i<head.size;i++)
	//			inode[i] = *getinode(tf,head.info+i);
	//		return head.size;
	//	}
	//}
	//
	//int tdbm_delete(TRIE *tf, char *str)
	//{
	//	TNODE node;
	//	INFO_HEAD head;
	//	int idx;
	//	int x;
	//
	//	x = trie_search(tf,str); /* 일단 찾아 본다.*/
	//
	//	if(x <= 0 ) 
	//		return x;
	//
	//	idx=tf.search_idx[x];
	//	node = *getnode(tf,idx);
	//
	//	/* trie_search를 통과했다면 node.info_size는 1 이상이다. */
	//	head=*(INFO_HEAD*)getnode(tf,node.info_idx);
	//	node_free(tf,head.info,head.size);
	//	node_free(tf,node.info_idx,1);
	//	node.info_size = 0;
	//	node.info_idx = 0;
	//	putnode(tf,idx,&node);
	//	trie_remove_path(tf);
	//	return 1;
	//}
	//
	//
	///*----------------------------------------
	//  <section> application III -- TRIE
	//  : public
	//
	//    TRIE : trie based indexing system
	//    단어 : 위치(파일번호, 파일내 위치), 위치, 위치 ....
	//    이러한 저장을 위한 구조이다.
	//    위치 정보의 갯수 제한은 없고 하나의 위치를 위한 저장 구조의 크기는
	//    12바이트이다.
	//    firstkery, lastkery, nextkery는 trie_firstkey 등을 사용하면된다.
	//----------------------------------------------------------------------*/
	//
	//final public static String TRIE_FILE_SUFFIX ".tx"
	//
	//TRIE * tx_open(char *fname, char type)
	//{
	//	int flags;
	//	char filename[256];
	//
	//	if(type == 'r')
	//		flags = FILE_FLAG_R ;
	//	else if(type == 'w')
	//		flags = FILE_FLAG_W ;
	//	else
	//	{
	//		fprintf(stderr,"tx_open:open_type unknown '%c'\n",type);
	//		return NULL;
	//	}
	//
	//	strcpy(filename,fname);
	//	strcat(filename,TRIE_FILE_SUFFIX);
	//	return (TRIE *)trie_open(filename,flags,ITYPE_TRIE);
	//}
	//
	//
	//void tx_close(TRIE *tf)
	//{
	//    trie_close((TRIE *)tf);
	//}
	//
	//int tx_store(TRIE *tf, char *word, TX_TYPE *p)
	//{
	//	TNODE node; /* previous node and current node */
	//	TX_TYPE *txinfo;
	//	int idx, info;
	//	int newidx;
	//	int newword;
	//
	//	idx=trie_insert(tf,word);
	//	if(idx == 0)
	//		return -1;
	//
	//	node = *getnode(tf,idx);
	//	info = node.info_idx;
	//
	//	if(node.info_size != 0)
	//	{
	//		newidx = node_alloc(tf,1);
	//		txinfo=(TX_TYPE *)getiaddr(tf,info);
	//		p.next=txinfo.next;
	//		txinfo.next=newidx;
	//		txinfo.posx++;
	//		putnode(tf,newidx,(TNODE *)p);
	//		newword=0;
	//	}
	//	else
	//	{
	//		newidx = node_alloc(tf,2);
	//		putnode(tf,newidx+1,(TNODE *)p);
	//		p.next=0;
	//		txinfo=(TX_TYPE *)getiaddr(tf,newidx);
	//		txinfo.posx=1;
	//		txinfo.next=newidx+1;
	//		node.info_idx=newidx;
	//		node.info_size=1;
	//		putnode(tf,idx,&node);
	//		newword=1;
	//	}
	//	return newword;
	//}
	//
	//int tx_fetch(TRIE *tf, char *str, TX_TYPE *p)
	//{
	//	TNODE *node;
	//	TX_TYPE *txinfo;
	//	int idx,next;
	//	int size;
	//	int i;
	//	int x;
	//
	//	x = trie_search(tf,str);
	//
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//		idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		txinfo = (TX_TYPE *)getnode(tf,node.info_idx);
	//		size=txinfo.posx;
	//		next=txinfo.next;
	//		for(i=0;i<size;i++)
	//		{
	//			p[i] = *(TX_TYPE *)getnode(tf,next);
	//			next=p[i].next;
	//		}
	//		return size;
	//	}
	//}
	//
	//int tx_count(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	TX_TYPE *txinfo;
	//	int idx;
	//	int x;
	//
	//	x = trie_search(tf,str);
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//		idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		txinfo = (TX_TYPE *)getnode(tf,node.info_idx);
	//		return txinfo.posx;
	//	}
	//}
	//
	//int txcmp(TX_TYPE *p, TX_TYPE *q)
	//{
	//	if(p.posx - q.posx)
	//		return p.posx - q.posx;
	//	if(p.posy - q.posy)
	//		return p.posy - q.posy;
	//	return 0;
	//}
	//
	//int tx_delete(TRIE *tf, char *str, TX_TYPE *p)
	//{
	//	int i;
	//	int x;
	//	TNODE node;
	//	TX_TYPE txinfo,txkill;
	//	int idx,size,next,kill,pidx;
	//
	//	x = trie_search(tf,str); /* 일단 찾아 본다.*/
	//
	//	if(x <= 0 ) 
	//		return x;
	//
	//	idx=tf.search_idx[x];
	//	node = *getnode(tf,idx);
	//	if(node.info_size != 0)
	//	{
	//		txinfo=*(TX_TYPE *)getnode(tf,node.info_idx);
	//		next=txinfo.next;
	//		size=txinfo.posx;
	//		if(p==NULL)
	//		{
	//			node_free(tf,node.info_idx,1);
	//			for(i=0;i<size;i++)
	//			{
	//				txkill = *(TX_TYPE *)getnode(tf,next);
	//				node_free(tf,next,1);
	//				next=txkill.next;
	//			}
	//			node.info_size = 0;
	//			node.info_idx = 0;
	//			putnode(tf,idx,&node);
	//		}
	//		else
	//		{
	//			pidx=node.info_idx;
	//			for(i=0;i<size;i++)
	//			{
	//				txkill = *(TX_TYPE *)getnode(tf,next);
	//				if(txcmp(p,&txkill)==0)
	//					break;
	//				pidx=next;
	//				next=txkill.next;
	//			}
	//			if(i<size)
	//			{
	//				kill=next;
	//				next=txkill.next;
	//				node_free(tf,kill,1);
	//				txinfo.posx--;
	//				if(txinfo.posx==0)
	//				{
	//					node_free(tf,node.info_idx,1);
	//					node.info_idx=0;
	//					node.info_size=0;
	//					putnode(tf,idx,&node);
	//				}
	//				else
	//				{
	//					txkill = *(TX_TYPE *)getnode(tf,pidx);
	//					txkill.next=next;
	//					putnode(tf,pidx,(TNODE *)&txkill);
	//					if(pidx!=node.info_idx)
	//						putnode(tf,node.info_idx,(TNODE *)&txinfo);
	//				}
	//			}
	//		}
	//	}
	//	trie_remove_path(tf);
	//	return 1;
	//}
	//
	///*----------------------------------------
	//  <section> application IV -- TRIE
	//  :public
	//
	//----------------------------------------------------------------------*/
	//final public static String TY_FILE_SUFFIX ".ty"
	//
	//TRIE * ty_open(char *fname, char type)
	//{
	//	int flags;
	//	char filename[256];
	//
	//	if(type == 'r')
	//		flags = FILE_FLAG_R ;
	//	else if(type == 'w')
	//		flags = FILE_FLAG_W ;
	//	else
	//	{
	//		fprintf(stderr,"ty_open:open_type unknown '%c'\n",type);
	//		return NULL;
	//	}
	//
	//	strcpy(filename,fname);
	//	strcat(filename,TY_FILE_SUFFIX);
	//	return (TRIE *) trie_open(filename,flags,ITYPE_TY);
	//}
	//
	//
	//void ty_close(TRIE *tf)
	//{
	//    trie_close((TRIE *)tf);
	//}
	//
	//int ty_store(TRIE *tf, char *word, char *p, int ysize, int plen)
	//{
	//	TNODE node; /* previous node and current node */
	//	INFO *inode;
	//	TX_TYPE txinfo;
	//	int idx;
	//	int newidx;
	//	int plen_size;
	//	int i;
	//
	//	idx=trie_insert(tf,word);
	//	if(idx == 0)
	//		return -1;
	//
	//	node = *getnode(tf,idx);
	//
	//	if(node.info_size != 0)
	//	{
	//		txinfo=*(TX_TYPE *)getnode(tf,node.info_idx);
	//		node_free(tf,txinfo.next,txinfo.posx/12+1);
	//	}
	//	else
	//	{
	//		newidx = node_alloc(tf,1);
	//		node.info_idx=newidx;
	//		node.info_size=1;
	//	}
	//	plen_size=plen/12+1;
	//
	//	txinfo.next = node_alloc(tf,plen_size);
	//	txinfo.posx=plen;
	//	txinfo.posy=ysize;
	//	putnode(tf,node.info_idx,(TNODE *)&txinfo);
	//	putnode(tf,idx,&node);
	//	inode=(INFO *)p;
	//	for(i=0;i<plen_size;i++,inode++)
	//		putinode(tf,txinfo.next+i,inode);
	//	return 0;
	//}
	//
	//int ty_fetch(TRIE *tf, char *str, char *p)
	//{
	//	TNODE *node;
	//	INFO *inode;
	//	TX_TYPE *txinfo;
	//	int idx;
	//	int size,next,rsize;
	//	int i;
	//	int x;
	//
	//	x = trie_search(tf,str);
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//		idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		txinfo = (TX_TYPE *)getnode(tf,node.info_idx);
	//
	//		size=txinfo.posx/12+1; /* node size */
	//		next=txinfo.next;
	//		rsize=txinfo.posy;
	//		inode=(INFO *)p;
	//		for(i=0;i<size;i++)
	//			inode[i] = *getinode(tf,next+i);
	//		return rsize; /* real data size */
	//	}
	//}
	//
	//int ty_count(TRIE *tf, char *str)
	//{
	//	TNODE *node;
	//	TX_TYPE *txinfo;
	//	int idx;
	//	int x;
	//
	//	x = trie_search(tf,str);
	//	if(x <= 0)
	//		return x;
	//	else
	//	{
	//		idx=tf.search_idx[x];
	//		node = getnode(tf,idx);
	//		txinfo = (TX_TYPE *)getnode(tf,node.info_idx);
	//		return txinfo.posy;
	//	}
	//}
	//
	///*----------------------------------------
	//  <section> debugging & tracing
	//  : public
	//----------------------------------------------------------------------*/
	//void prn_info(INFO *inode)
	//{
	//        fprintf(stderr,"INFO\tword_id = %ld,\n",inode.word_id);
	//        fprintf(stderr,"\tmtag = %d,\n",inode.mtag);
	//        fprintf(stderr,"\tstag = %d,\n",inode.stag);
	//        fprintf(stderr,"\tphone = %d,\n",inode.phone);
	//        fprintf(stderr,"\tgram = %d,\n",inode.gram);
	//        fprintf(stderr,"\tsem = %d,\n",inode.sem);
	//        fprintf(stderr,"\tdomain = %d,\n",inode.domain);
	//        fprintf(stderr,"\tdummy = %d,\n",inode.dummy);
	//}
	//void prn_tnode(TNODE *tnode)
	//{
	//        fprintf(stderr,"TNODE\tkey = %ld,\n",tnode.key);
	//        fprintf(stderr,"\tdummy = %d,\n",tnode.dummy);
	//        fprintf(stderr,"\tinfo_size = %d,\n",tnode.info_size);
	//        fprintf(stderr,"\tchild_size = %d,\n",tnode.child_size);
	//        fprintf(stderr,"\tinfo_idx = %ld,\n",tnode.info_idx);
	//        fprintf(stderr,"\tchild_idx = %ld,\n",tnode.child_idx);
	//}
	//void prn_free(FREE *fnode)
	//{
	//        fprintf(stderr,"FREE\tsize = %ld,\n",fnode.size);
	//        fprintf(stderr,"\tlchild = %ld,\n",fnode.lchild);
	//        fprintf(stderr,"\trchild = %ld,\n",fnode.rchild);
	//}
	//
	//
	//int trace_free(TRIE *tf, int idx)
	//{
	//    char xline[1024];
	//    FREE fn;
	//    int x=0;
	//    fn=*getfnode(tf,idx);
	//
	//    fprintf(stderr,"\nidx=%ld\n",idx);
	//    prn_free(&fn);
	//
	//    while(1)
	//    {
	//	printf("l,r,u,q>");
	//	fgets(xline,1024,stdin);
	//	if(strcmp(xline,"q")==0)
	//	    return 1;
	//	if(strcmp(xline,"u")==0)
	//	    break;
	//	else if(strcmp(xline,"l")==0)
	//	    x=trace_free(tf,fn.lchild);
	//	else if(strcmp(xline,"r")==0)
	//	    x=trace_free(tf,fn.rchild);
	//	if(x==1)
	//	    return x;
	//	prn_free(&fn);
	//    }
	//    return 0;
	//}
	//
	//
	//int trace_tnode(TRIE *tf, int idx)
	//{
	//    char xline[1024];
	//    TNODE tn;
	//    int x=0;
	//
	//    if(idx==0)
	//	return 0;
	//    tn=*getnode(tf,idx);
	//    fprintf(stderr,"\nidx=%ld\n",idx);
	//    prn_tnode(&tn);
	//
	//    while(1)
	//    {
	//	printf("s,c,u,i,q>");
	//	fgets(xline,1024,stdin);
	//	if(strcmp(xline,"q")==0)
	//	    return 1;
	//	if(strcmp(xline,"u")==0)
	//	    break;
	//	else if(strcmp(xline,"s")==0)
	//	    x=trace_tnode(tf,idx+1);
	//	else if(strcmp(xline,"c")==0)
	//	    x=trace_tnode(tf,tn.child_idx);
	//	else if(strcmp(xline,"i")==0)
	//	{
	//	    INFO *inode;
	//	    inode=getinode(tf,tn.info_idx);
	//	    prn_info(inode);
	//	}
	//	if(x==1)
	//	    return x;
	//	prn_tnode(&tn);
	//    }
	//    return 0;
	//}
	//
	//int count_free(TRIE *tf, int idx)
	//{
	//    FREE fn;
	//    int x=1;
	//    fn=*getfnode(tf,idx);
	//
	//    if(fn.lchild)
	//	x+=count_free(tf,fn.lchild);
	//    if(fn.rchild)
	//	x+=count_free(tf,fn.rchild);
	//    return x;
	//}
	//
	//int do_trace(TRIE *tf)
	//{
	//    char xline[1024];
	//    int x=0;
	//    while(1)
	//    {
	//	printf("f,t,fc,q>");
	//	fgets(xline,1024,stdin);
	//	if(strcmp(xline,"q")==0)
	//	    break;
	//	else if(strcmp(xline,"f")==0)
	//	    trace_free(tf,FREE_NODE);
	//	else if(strcmp(xline,"t")==0)
	//	    trace_tnode(tf,START_NODE);
	//	else if(strcmp(xline,"fc")==0)
	//	{
	//	    x=count_free(tf,FREE_NODE);
	//	    fprintf(stderr,"<fcount> n of fnode =%d\n",x);
	//
	//	}
	//    }
	//}
	/*++++++++++++++++++++++++*/
	/* debugging */
	/*#define DEBUG_NODE_ALLOC	/* node alloc의 상황 점검 */
	/*#define DEBUG_NODE_TRACE	/* node를 보여주기 위해 사용 */
	/*#define DEBUG_LOOK		/* mdic_lookup count */

	//#ifdef _MSC_VER
	//#define FILE_FLAG_R (O_RDONLY|O_BINARY)
	//#define FILE_FLAG_W (O_RDWR|O_CREAT|O_BINARY)
	//#else
	//#define FILE_FLAG_R (O_RDONLY)
	//#define FILE_FLAG_W (O_RDWR|O_CREAT)
	//#endif

	///*----------------------------------------
	//  <section> dcache interface
	//  :hidden, user는 가능하면 사용하지 않는 것이 좋다.
	//  
	//  TNODE,      FREE        INFO
	//  getnode(),  getfnode(), getinode()	: idx에 대한 memory pointer
	//  getnaddr(), getfaddr(), getiaddr()	: ... + dirty_set
	//----------------------------------------------------------------------*/
	//#define getnode(tf,idx) ((TNODE *)dc_add_r((tf)->cache,(idx)+DC_PAGESIZE))
	//#define getfnode(tf,idx) ((FREE *)dc_add_r((tf)->cache,(idx)+DC_PAGESIZE))
	//#define getinode(tf,idx) ((INFO *)dc_add_r((tf)->cache,(idx)+DC_PAGESIZE))
	//
	///* 아래 define은 주어진 address의 내용이 변경되는 경우 사용한다. */
	//#define getnaddr(tf,idx) ((TNODE *)dc_add_w((tf)->cache,(idx)+DC_PAGESIZE))
	//#define getfaddr(tf,idx) ((FREE *)dc_add_w((tf)->cache,(idx)+DC_PAGESIZE))
	//#define getiaddr(tf,idx) ((INFO *)dc_add_w((tf)->cache,(idx)+DC_PAGESIZE))

	///* 주어진 address에 대한 dirty_set만 한다. */
	//#define reflect_change(tf,p) dc_dirty_set((tf)->cache,(void *)p)
	//
	///* wjlee #define putnode(tf,idx,node) (*getnaddr(tf,idx) = *(TNODE *)(node)) */
	//

	//#define putfnode(tf,idx,node) putnode(tf,idx,(TNODE *)node)
	//#define putinode(tf,idx,node) putnode(tf,idx,(TNODE *)node)
	//
	//
	///*----------------------------------------
	//<section> free node management 
	//
	//mergefreetree(), splitfreetree()
	//:hidden++
	//
	//node_alloc(), node_free()
	//:hidden
	//----------------------------------------------------------------------*/
	///* 
	// mergefreetree()
	// 두개의 free node tree를 하나로 결합하기 위하여 사용된다.
	// size가 큰 노드가 항상 위에 있게 조정된다.
	// node_alloc()과 node_free()에서만 사용된다.
	//*/
	//
	//int mergefreetree(TRIE *tf, int lidx, int ridx)
	//{
	//	FREE lfn, rfn;
	//
	//#ifdef DEBUG_NODE_ALLOC
	//fprintf(stderr,"m");
	//#endif
	//	if(lidx == 0)
	//		return ridx;
	//	if(ridx == 0)
	//		return lidx;
	//
	//	lfn = *getfnode(tf,lidx);
	//	rfn = *getfnode(tf,ridx);
	//	
	//	/* rfn.size==0이면 이것은 free의 root이다. */
	//	/* lfn.size > rfn.size : 비교에 >= 인 경우 우경,
	//	  	 > 인 경우 좌경, 좌경이 빠르다. */
	//	if(rfn.size != 0 && lfn.size > rfn.size)
	//	{
	//		lfn.rchild = mergefreetree(tf,lfn.rchild,ridx);
	//		putfnode(tf,lidx,&lfn);
	//		return lidx;
	//	}
	//	else
	//	{
	//		rfn.lchild = mergefreetree(tf,lidx,rfn.lchild);
	//		putfnode(tf,ridx,&rfn);
	//		return ridx;
	//	}
	//}
	//
	///* 
	// splitfreetree()
	// 하나의 free node tree를 둘로 갈라 놓는 역할을 한다.
	// 가르는 기준은 위치(address) 정보인 key이다.
	// node_free()에서만 사용된다.
	//*/
	//int splitfreetree(TRIE *tf, int idx, int key, int *x, int *y)
	//{
	//	FREE fn;
	//#ifdef DEBUG_NODE_ALLOC
	//fprintf(stderr,"s");
	//#endif
	//
	//	if(key == 0)
	//		return 0;
	//	if(idx == 0)
	//	{
	//	    *x=0;*y=0;
	//	    return 1;
	//	}
	//
	//	fn = *getfnode(tf,idx);
	//	
	//	if(key < idx)
	//	{
	//	    *y=idx;
	//	    splitfreetree(tf,fn.lchild,key,x,&(fn.lchild));
	//	    putfnode(tf,idx,&fn);
	//	    return 1;
	//	}
	//	else /* if(key > idx) */
	//	{
	//	    *x=idx;
	//	    splitfreetree(tf,fn.rchild,key,&(fn.rchild),y);
	//	    putfnode(tf,idx,&fn);
	//	    return 1;
	//	}
	//}
	//
	//final public static int F_DIR_L 0
	//final public static int F_DIR_R 1
	///*----------------------------------------------------------------------
	//node_alloc()
	//	trie node manager
	//	
	//----------------------------------------------------------------------*/
}
