package tw.com.kuan.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Log4j2
public  class GetEHUtil {
	public static String[] REPLACECHAR = { "~", "\\*", "\\#", "\\%", "\\&", ":", ">", "<", "/", "\\}", "\\{", "\\|", "\\?" };

	public static Properties p = new Properties();
	public static int fileCount = 0;

	public GetEHUtil() {
		FileInputStream input = null;
		try {
			input = new FileInputStream("./conf/config.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			p.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void getPhotoJsoup(String target, String Path) throws IOException {
		Connection conn = getConnection(target).timeout(10000);
		Document doc = conn.get();
		Elements elem = doc.select("#i3");

		for (Element obj : elem) {
			String jpgUrl = obj.childNode(0).childNode(0).attr("src");
			String saveNum = target.split("-")[1];
			StringBuilder saveNameBuilder = new StringBuilder().append(saveNum).append(".jpg");
			// String name = formatAns + ".jpg";
			savePhoto(jpgUrl, saveNameBuilder.toString(), Path);
		}
	}

	public synchronized ArrayList<String> getPhotoUrl(String target) throws IOException {
		Connection conn = getConnection(target);
		Document doc = conn.get();
		Elements elem = doc.select("#gdt").select(".gdtm").select("a");
		ArrayList<String> result = new ArrayList<String>();
		for (Element obj : elem) {
			String tagetUrl = obj.attr("href");
			result.add(tagetUrl);
		}
		return result;
	}

	public synchronized String getTitle(String target) throws IOException {
		String title = null;
		System.out.println(target);
		Connection conn = getConnection(target);
		Document doc = conn.get();
		title = doc.select("#gj").text();
		System.out.println(doc.select("#gj").text());
		if (title.length() < 1) {
			title = doc.select("#gn").text();
			
		}
		for(String str:REPLACECHAR) {
			title = title.replaceAll(str, " ");
		}
		System.out.println(title);
		return title;
	}

	public boolean savePhoto(String sURL, String fileName, String path) {

		boolean doSuccess = true;
		BufferedReader in = null;
		try {
			URL url = new URL(sURL);
			HttpURLConnection URLConn = null;
			URLConn = (HttpURLConnection) url.openConnection();
			// 要求的標頭header
			URLConn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) "
					+ "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)");
			URLConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			URLConn.setRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
			URLConn.setRequestProperty("Accept-Charse", "Big5,utf-8;q=0.7,*;q=0.7");
			URLConn.setConnectTimeout(60000);
			// URLConn.setDoInput(true);
			// URLConn.setDoOutput(true);
			// 建立連線
			URLConn.connect();
			// URLConn.getOutputStream().flush();
			log.debug("URL: {}", sURL );
			log.debug("savePath: {}", path );
			log.debug("fileName: {}", fileName);
			// 取得下載inputstream連線
			java.io.BufferedInputStream rd = new java.io.BufferedInputStream(URLConn.getInputStream());

//			String saveFileName = fileName;

			java.io.File f = new java.io.File(path);
			f = new java.io.File(f.getAbsolutePath());
			if (!f.exists())
				f.mkdirs();
			f = new java.io.File(f.getAbsolutePath() + java.io.File.separator + fileName);
			if (f.exists()) {
				return true;
			}
//			fileName = saveFileName;
			// 取得路徑
			// this.filePath = f.getAbsolutePath();

			java.io.BufferedOutputStream fos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(f));

			byte[] tmp = new byte[1024];
			int len;
			// 讀取httpConection的input,寫出檔案的output
			while ((len = rd.read(tmp)) != -1) {
				// System.out.print(tmp);
				fos.write(tmp, 0, len);
			}
			fos.flush();
			fos.close();
			rd.close();

		} catch (IOException e) {
			doSuccess = false;
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				in = null;
			}
		}
		return doSuccess;
	}

	public synchronized Connection getConnection(String baseUrl) {
		Connection conn = Jsoup.connect(baseUrl);
		String user = p.getProperty("USERNUMBER");
		String pass = p.getProperty("PASSWD_HASH");
		if (user != null && pass != null && !"".equalsIgnoreCase(user) && !"".equalsIgnoreCase(pass)) {
			conn.cookie("ipb_member_id", p.getProperty("USERNUMBER"));
			conn.cookie("ipb_pass_hash", p.getProperty("PASSWD_HASH"));
//			conn.cookie("igneous", "afe62975f");
//			conn.cookie("sl", "dm_1");
//			conn.cookie("s", "c54e14dd7");
			conn.cookie("sk", p.getProperty("SK"));
//			conn.cookie("lv", "1552487455-1552952712");
//			conn.cookie("hath_perks", "m1.t1.t2.t3.p1-16313ecdd6");
		}
		return conn;
	}

}
