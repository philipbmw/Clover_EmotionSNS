package kr.ac.kaist.swrc.jhannanum.demo;
import java.sql.*;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class PredefinedWorkflow {

 public static void main(String[] args) throws Exception {
  Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_HMM_TAGGER);
 
  try {
   /* Activate the workflow in the thread mode */
   workflow.activateWorkflow(true);
   
   try{
    Class.forName("com.mysql.jdbc.Driver");
    System.out.println("드라이버가 정상 설치 되었습니다.");
   }catch(ClassNotFoundException ex){
    
    System.out.println("드라이버가 없엉!!");
   }
   
   Connection conn = null;
   
   String url = "jdbc:mysql://127.0.0.1:3306/clover_db";
   String id = "root";
   String pass = "apmsetup";
   
   Statement stmt = null; 
   ResultSet rs = null;
   String st =null;
   String test =null;
   
   String query = "select * from timeline_tbl order by textid desc";
   
   try{
     conn = DriverManager.getConnection(url,"root","apmsetup");
     stmt = conn.createStatement();
     rs = stmt.executeQuery(query);
     while(rs.next()){
     //rs.next(); 
      
     test = rs.getString(7);
     System.out.println(rs.getString(6)+":"+test);
     //}
     
     System.out.println("string :"+rs.getString(7));
     String spaceR=StringUtils.Spacing(rs.getString(7)); //띄어쓰기
     //rs.getString(2)=spaceR;
     //String output = StringUtils.WriteEmoticon(spaceR);//이모티콘
     //System.out.println("string :"+output);
     //spaceR=output;
     //System.out.println("string document :"+out);
     
     
     
     workflow.analyze(rs.getString(7));
     
     
     System.out.println(workflow.getResultOfDocument());
     
     //workflow.close();
     }
    } catch (Exception e) {
     e.printStackTrace();
     System.exit(0);
    }
 /* Shutdown the work flow */
   // workflow.close();   
   
    //workflow.close();
   
    rs.close();
    stmt.close();
    conn.close();
    }catch(SQLException ee){
     
    System.out.println("Error = "+ee.toString());
    
   }
   
   workflow.close();   
 }
 
}