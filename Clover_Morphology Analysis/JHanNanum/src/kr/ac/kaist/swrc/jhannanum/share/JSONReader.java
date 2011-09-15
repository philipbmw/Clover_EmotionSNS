/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.share;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class JSONReader {
	private String filePath = null;
	JSONObject json = null;
	
	public static void main(String[] args) {
		try {
			File dir = new File("conf");
			String[] files = dir.list();
			
			for (int i = 0; i < files.length; i++) {
				JSONReader reader = new JSONReader("conf/" + files[i]);
				System.out.println(reader.getName());
				System.out.println(reader.getVersion());
				System.out.println(reader.getAuthor());
				System.out.println(reader.getDescription());
				System.out.println(reader.getType());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JSONReader(String filePath) throws JSONException, IOException {
		FileReader reader = new FileReader(filePath);
		StringBuffer buf = new StringBuffer();
		char[] cbuf = new char[4096];
		int idx = 0;
		while ((idx = reader.read(cbuf)) != -1) {
			buf.append(cbuf, 0, idx);
		}
		json = new JSONObject(buf.toString());
		
		reader.close();
	}
	
	public String getName() throws JSONException {
		return json.getString("name");
	}
	
	public String getVersion() throws JSONException {
		return json.getString("version");
	}
	
	public String getAuthor() throws JSONException {
		String res = "";
		
		JSONArray array = json.getJSONArray("author");
		JSONObject obj = null;
		for (int i = 0; i < array.length(); i++) {
			if (i > 0) {
				res += ", ";
			}
			obj = array.getJSONObject(i);
			if (!obj.getString("email").equals("null")) {
				res += obj.getString("name") + "<" + obj.getString("email") + ">";
			} else {
				res += obj.getString("name");
			}
		}
		return res;
	}
	
	public String getDescription() throws JSONException {
		return json.getString("description");
	}
	
	public String getType() throws JSONException {
		return json.getString("type");
	}
	
	public String getValue(String key) throws JSONException {
		return json.getString(key);
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
