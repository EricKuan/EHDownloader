package tw.com.kuan.clamber;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.util.StringUtils;
import tw.com.kuan.util.GetEHUtil;

@Log4j2
public class GetEHThread implements Runnable {
    private String url = null;

    public GetEHThread(String url) {
        this.url = url;
    }

    public void getPhoto() throws IOException {
        GetEHUtil http = new GetEHUtil();
        String Path = null;
        String title;
        try {
            title = http.getTitle(url);
            title = title.replace("?", "");
        } catch (IOException e) {
            log.info("Can't get Title, please check url again!");
            return;
        }
        Path = "photo\\" + title;
        log.info("Path: {}", Path);
        //ptt
        Connection conn = http.getConnection(url);
        Document doc = conn.get();
        Elements elem = doc.select(".ptt").select("tr").select("td");
        Set<String> result = new HashSet<String>();


        Integer maxPage = elem.stream().filter(item -> Pattern.matches("\\d{1,3}", item.text())).map(Element::text).map(Integer::valueOf).max(Integer::compareTo).orElse(0);

        log.info("maxPage: {}", maxPage);
//		https://exhentai.org/g/2059599/2ca850abab/?p=8

        result.add(url);
        for (int i = 1; i < maxPage; i++) {
            StringBuilder urlBuilder = new StringBuilder().append(url).append("?p=").append(i);
            result.add(urlBuilder.toString());
        }


        log.info("targetList: {}", new Gson().toJson(result));

        for (String target : result) {
            log.info("target: {}", target);
            try {
                ArrayList<String> urlSet = http.getPhotoUrl(target);
                for (String targetUrl : urlSet) {
                    log.info("title: {}", targetUrl);
                    try {
                        http.getPhotoJsoup(targetUrl, Path);
                    } catch (Exception e) {
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
