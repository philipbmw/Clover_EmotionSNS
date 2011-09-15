package kr.ac.kaist.swrc.jhannanum.demo;


	import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
	import java.util.regex.Matcher;
import java.util.regex.Pattern;

	public class StringUtils {
	
		 public static int countOccurrences(String input, String word) {
			  int count = 0;
			  Pattern pattern = Pattern.compile(word);
			  Matcher matcher = pattern.matcher(input);
			  while (matcher.find()) {
			   count++;
			  }
			  return count;
			  
			 }
			 
			 // txt파일로 이모티콘 처리하는 함수 
			 //input: string값, 
			 //output: txt에 이모티콘+갯수 출력 & 형태소에string 값으로 넘겨야함. 
			 //  넘기는 값에 이모티콘은 삭제해야함..형태소분석기에서 에러남.
			 public static String WriteEmoticon(String input)throws IOException
			 {
				 String temp;  // temp 파일 string
					File fileName = new File("c:\\Test\\Emoticon.txt");
					File outputFile= new File("c:\\Test\\word.txt");
					
					BufferedReader Em = new BufferedReader(new FileReader(fileName));

					BufferedWriter bw 
						=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
					
					
					String num;
					String del = null;
					
					
					while( (temp = Em.readLine()) != null ) 
					{
						int count = StringUtils.countOccurrences(input, temp);

						if(count>=1)
						{
							// System.out.println(temp);
							System.out.println("이모티콘 :"+temp+" 갯수 "+count);
							
							num=Integer.toString(count);//count를 문자로 변환
					       // 파일에 쓰기
							bw.write(temp+" ");
							bw.write(num);
							bw.newLine();
							
							//string에서 해당 문자열 삭제
							del=input.replace(temp," ");
							//input=del;
						}
						
					}
					
					 Em.close();
					 bw.close();
					 // del값이 안넘어홈..
					 System.out.println("string emo After :"+del); 
					 
				 return del;
			 }
			 public static String Spacing(String input)throws IOException
			 {
				 File fileName = new File("c:\\Test\\Space.txt");
				
					BufferedReader Em = new BufferedReader(new FileReader(fileName));

					String temp;
					String space = null;
					while( (temp = Em.readLine()) != null ) 
					{
						space=input.replace(temp,temp+" " );
						input=space;
					}
				 
					System.out.println("string space :"+space);
					
					return space;
			 }
			 
	}


