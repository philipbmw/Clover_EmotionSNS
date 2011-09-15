import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.demo.StringUtils;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor.NounExtractor;

public class WorkflowClover {

	public static void main(String[] args) throws IOException, SQLException {
		Workflow workflow = new Workflow();

		try {

			workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
			workflow.appendPlainTextProcessor(new InformalSentenceFilter(),
					null);

			workflow.setMorphAnalyzer(new ChartMorphAnalyzer(),
					"conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
			workflow.appendMorphemeProcessor(new UnknownProcessor(), null);

			workflow.setPosTagger(new HMMTagger(),
					"conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
			workflow.appendPosProcessor(new NounExtractor(), null);

			/* Activate the workflow in the thread mode */
			workflow.activateWorkflow(true);

			try {
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("드라이버가 정상 설치 되었습니다.");
			} catch (ClassNotFoundException ex) {
				System.out.println("드라이버가 없엉!!");
			}

			Connection conn = null;

			String url = "jdbc:mysql://127.0.0.1:3306/clover_db";
			String id = "root";
			String pass = "apmsetup";

			Statement stmt = null;

			ResultSet rs = null;

			String query = "select * from timeline_tbl order by textid desc";

			try {
				conn = DriverManager.getConnection(url, "root", "apmsetup");
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					// 파일 이어 쓰기
					BufferedWriter out = new BufferedWriter(new FileWriter(
							"c:\\Test\\word.txt", true));
					/*
					 * 파일 이어 쓰기로 안하면... 안써짐.. 매번 다른 내용의 디비를 돌리면 중복 없이..
					 * 괜찮...을까나? ㅋ ㅠㅠ 디비에서 글 읽어 올때.. 분석안된것만 불러왔음 해결될듯 ㅋ
					 */

					String spaceR = StringUtils.Spacing(rs.getString(7)); // 띄어쓰기
					// rs.getString(2)=spaceR;

					// String output = StringUtils.WriteEmoticon(spaceR);// 이모티콘
					// 이모티콘 추출후 값이 현재 안넘어감..

					// workflow.analyze(rs.getString(7));

					// 띄어쓰기하고, 이모티콘한후 분석
					workflow.analyze(spaceR);

					// 추출 단어 배열로 출력
					LinkedList<Sentence> resultList = workflow
							.getResultOfDocument(new Sentence(0, 0, false));
					for (Sentence s : resultList) {
						Eojeol[] eojeolArray = s.getEojeols();

						for (int i = 0; i < eojeolArray.length; i++) {
							if (eojeolArray[i].length > 0) {
								String[] morphemes = eojeolArray[i]
										.getMorphemes();
								for (int j = 0; j < morphemes.length; j++) {
									System.out.print(morphemes[j]);
									// Dbw.write(morphemes[j]);
									out.write(morphemes[j]); // 파일 쓰기
								}
								System.out.print(", ");
								out.write(" ");
								// out.newLine();
							}
						}
						System.out.println();
					}
					out.newLine(); // 한문장 끝나고 줄바꿈

					out.close();
				}
			} catch (SQLException ee) {

				System.out.println("Error = " + ee.toString());
			}

			rs.close();
			stmt.close();
			conn.close();

			// System.out.println(workflow.getResultOfDocument());

			// workflow.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		fileClover.FileInputOutput();
		
		
		svm_predict.main(args);
		
		/* Shutdown the workflow */
		workflow.close();
		System.exit(0);
		
		

	}
	

}
