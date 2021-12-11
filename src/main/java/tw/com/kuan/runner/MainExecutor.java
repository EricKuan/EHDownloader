package tw.com.kuan.runner;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tw.com.kuan.clamber.GetEHThread;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Log4j2
public class MainExecutor implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor  = Executors.newFixedThreadPool(5);
        FileInputStream fileInStreamObj = new FileInputStream("./conf/downliadList.txt");
        InputStream inStreamObject = fileInStreamObj;
        Scanner sc = new Scanner(inStreamObject);
        List<CompletableFuture> jobList = new ArrayList<>();
        while (sc.hasNext()) {
            String url = sc.nextLine();
            GetEHThread getThread = new GetEHThread(url);
            CompletableFuture item = CompletableFuture.runAsync(getThread, executor);
            jobList.add(item);
            Thread.sleep(3000L);
        }
        sc.close();
        inStreamObject.close();
        fileInStreamObj.close();

        CompletableFuture[] jobs = new CompletableFuture[jobList.size()];
        jobList.toArray(jobs);
        CompletableFuture.allOf(jobs).whenComplete((b, r) ->{
            log.info("Mission Completed");
        }).join();

        delCompleteFile();
        System.exit(0);
    }
    private static void delCompleteFile() throws IOException {
        String filePath = "./conf/downliadList.txt";
        File myFile = new File(filePath);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String backPath = filePath.substring(0, filePath.length() - 4) + "." + sdf.format(cal.getTime()) + ".txt";
        log.info(backPath);
        myFile.renameTo(new File(backPath));
        myFile = new File(filePath);
        if ((myFile.createNewFile())) {
            log.info("downloadList.txt reset Completed!");
        }
    }
}
