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
				System.out.println("����̹��� ���� ��ġ �Ǿ����ϴ�.");
			} catch (ClassNotFoundException ex) {
				System.out.println("����̹��� ����!!");
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
					// ���� �̾� ����
					BufferedWriter out = new BufferedWriter(new FileWriter(
							"c:\\Test\\word.txt", true));
					/*
					 * ���� �̾� ����� ���ϸ�... �Ƚ���.. �Ź� �ٸ� ������ ��� ������ �ߺ� ����..
					 * ����...���? �� �Ф� ��񿡼� �� �о� �ö�.. �м��ȵȰ͸� �ҷ����� �ذ�ɵ� ��
					 */

					String spaceR = StringUtils.Spacing(rs.getString(7)); // ����
					// rs.getString(2)=spaceR;

					// String output = StringUtils.WriteEmoticon(spaceR);// �̸�Ƽ��
					// �̸�Ƽ�� ������ ���� ���� �ȳѾ..

					// workflow.analyze(rs.getString(7));

					// �����ϰ�, �̸�Ƽ������ �м�
					workflow.analyze(spaceR);

					// ���� �ܾ� �迭�� ���
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
									out.write(morphemes[j]); // ���� ����
								}
								System.out.print(", ");
								out.write(" ");
								// out.newLine();
							}
						}
						System.out.println();
					}
					out.newLine(); // �ѹ��� ������ �ٹٲ�

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
