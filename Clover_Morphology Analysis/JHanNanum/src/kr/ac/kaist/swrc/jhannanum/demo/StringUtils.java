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
			 
			 // txt���Ϸ� �̸�Ƽ�� ó���ϴ� �Լ� 
			 //input: string��, 
			 //output: txt�� �̸�Ƽ��+���� ��� & ���¼ҿ�string ������ �Ѱܾ���. 
			 //  �ѱ�� ���� �̸�Ƽ���� �����ؾ���..���¼Һм��⿡�� ������.
			 public static String WriteEmoticon(String input)throws IOException
			 {
				 String temp;  // temp ���� string
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
							System.out.println("�̸�Ƽ�� :"+temp+" ���� "+count);
							
							num=Integer.toString(count);//count�� ���ڷ� ��ȯ
					       // ���Ͽ� ����
							bw.write(temp+" ");
							bw.write(num);
							bw.newLine();
							
							//string���� �ش� ���ڿ� ����
							del=input.replace(temp," ");
							//input=del;
						}
						
					}
					
					 Em.close();
					 bw.close();
					 // del���� �ȳѾ�Ȩ..
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


