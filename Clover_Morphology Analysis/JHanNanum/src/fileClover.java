import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


class text{

	  public String name;     //멤버 변수
	  public int index;                      //멤버 변수
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
	 
	                       //생성자

	  public void print(){         //멤버 함수
	   System.out.println("단어 : " +name);
	   System.out.println("번호 : " +index);
	   System.out.println("값 : " +value);
	 }  
}

public class fileClover {


	
		// 
		 public static void FileInputOutput()throws IOException
 {
		String filePath = "c:\\Test\\word.txt";
		File file = new File(filePath);

		String filePath1 = "c:\\Test\\단어.txt";
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

				// text 클래스 단어와 ArrayList 단어 비교

				text[] t = new text[10];

				for (int i = 0; i < t.length; i++) {
					t[i] = new text();

				}
				// 단어 디비.. 넣기
				 BufferedReader inWord = new BufferedReader(new FileReader(file1)); 
				 String temp =null;
				
				 
				// System.out.println("파일: "+temp);
			    for(int i =0;i<10;i++)
			    { 
			     while( (temp = inWord.readLine()) != null )
			     {
			      t[i].name=temp;
			     // System.out.println(i+" 단어: "+t[i].name);
			      break;
			     }
			    
			    } 
			    /*
				t[0].name = "예쁘"; // 1
				t[1].name = "귀여"; // 2
				t[2].name = "좋아"; // 3
				t[3].name = "재밌"; // 4
				t[4].name = "사랑"; // 5
				t[5].name = "바보"; // 6
				t[6].name = "싫어"; // 7
				t[7].name = "멍청이"; // 8
				t[8].name = "아프"; // 9
				t[9].name = "나쁘"; // 10
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
