package tw.com.kuan;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.kuan.util.getEHUtil;

class getEHThread implements Runnable {
	private String url = null;

	public getEHThread(String url) {
		this.url = url;
	}

	public void getPhoto() throws IOException {
		getEHUtil http = new getEHUtil();
		String Path = null;
		String title;
		try {
			title = http.getTitle(url);
			title = title.replace("?","");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can't get Title, please check url again!");

			return;
		}
		Path = "photo\\" + title;
		System.out.println(Path);
		//ptt
		Connection conn = http.getConnection(url);
		Document doc = conn.get();
		Elements elem = doc.select(".ptt").select("tr").select("td");
		Set<String> result  = new HashSet<String>();
		for (Element obj : elem) {
			String tagetUrl = obj.select("a").attr("href");
			if(tagetUrl!=null && tagetUrl.length()>1){
				result.add(tagetUrl);
			}
		}
		for (String target: result) {
//			System.out.println("target= " + target);
			try {
				ArrayList<String> urlSet = http.getPhotoUrl(target);
				for (String targetUrl : urlSet) {
//					System.out.println(title + ", " + targetUrl);
					try{
					http.getPhotoJsoup(targetUrl, Path);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
		System.out.println(title + " Download End!");
	}

	public void run() {
		try {
			getPhoto();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
