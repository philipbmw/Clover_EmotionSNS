import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


class text{

	  public String name;     //��� ����
	  public int index;                      //��� ����
	  public int value;
	  
	  public text()
	  {
		  name = "";
		  index = 0;
		  value = 0;
	  } 
	  
	  public void setName(String name)
	  {
		  this.name = name;
		  
	  }
	  
	  public void setIndex(int index)
	  {
		  this.index = index;
	  }
	  
	  public void setValue(int value)
	  {
		  this.value = value;
	  }
	 
	                       //������

	  public void print(){         //��� �Լ�
	   System.out.println("�ܾ� : " +name);
	   System.out.println("��ȣ : " +index);
	   System.out.println("�� : " +value);
	 }  
}

public class fileClover {


	
		// 
		 public static void FileInputOutput()throws IOException
 {
		String filePath = "c:\\Test\\word.txt";
		File file = new File(filePath);

		String filePath1 = "c:\\Test\\�ܾ�.txt";
		File file1 = new File(filePath1);
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
		new File(new File("c:\\Test"), "test"))));
		
	
		// ArrayList<String> ar = new ArrayList<String>();
		String s;
		String[] ar;
		if (file.exists()) {
			BufferedReader inFile = new BufferedReader(new FileReader(file));
			String sLine = null;

			while ((sLine = inFile.readLine()) != null) {
				s = sLine;
				ar = s.split("\\s");

				//System.out.println(sLine);

			//	for (int i = 0; i < ar.length; i++) {
					//System.out.println(ar[i]);
			//	}

				// text Ŭ���� �ܾ�� ArrayList �ܾ� ��

				text[] t = new text[10];

				for (int i = 0; i < t.length; i++) {
					t[i] = new text();

				}
				// �ܾ� ���.. �ֱ�
				 BufferedReader inWord = new BufferedReader(new FileReader(file1)); 
				 String temp =null;
				
				 
				// System.out.println("����: "+temp);
			    for(int i =0;i<10;i++)
			    { 
			     while( (temp = inWord.readLine()) != null )
			     {
			      t[i].name=temp;
			     // System.out.println(i+" �ܾ�: "+t[i].name);
			      break;
			     }
			    
			    } 
			    /*
				t[0].name = "����"; // 1
				t[1].name = "�Ϳ�"; // 2
				t[2].name = "����"; // 3
				t[3].name = "���"; // 4
				t[4].name = "���"; // 5
				t[5].name = "�ٺ�"; // 6
				t[6].name = "�Ⱦ�"; // 7
				t[7].name = "��û��"; // 8
				t[8].name = "����"; // 9
				t[9].name = "����"; // 10
*/
			    
				for (int i = 0; i < ar.length; i++) {
					for (int j = 0; j < t.length; j++) {
						// System.out.println(t[j].name);
						// System.out.println(ar[i]);
						// System.out.println(t[i].name);
						// System.out.println("\n");
						if (ar[i].equals(t[j].name)) {
							t[j].value = 1;
						}
						// System.out.println(t[j].value);
					}

				}
				for (int k = 0; k < t.length; k++) {
					t[k].index = k;
					out.print("0 ");
					out.print(t[k].index+1 +":");
					out.print(t[k].value+" ");
					
				}
				out.print("\n");
			}
		}
		out.close();

	}
}
