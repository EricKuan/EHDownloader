package tw.com.kuan;

import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class captuerMain {
	private static int threadcount = 0;
	
	public static void main(String[] args) throws Exception {

		

		int i=0;
		
		int lastVoteCount=0;
		
		while(true){
			i++;
			Document doc = Jsoup.connect("https://www.cec.gov.tw/pc/zh_TW/P1/n00000000000000000.html").validateTLSCertificates(false).get();
			Elements ko = doc.select(".trT").eq(2).select(".tdAlignRight").eq(0);
			Elements din = doc.select(".trT").eq(1).select(".tdAlignRight").eq(0);
			String ticketBoxElement =  doc.select(".trFooterT").eq(0).select("td").text();
			String[] ticketBoxs =  ticketBoxElement.split(" ")[2].split("/");
			
			String koVoteTicket = ko.text().replaceAll(",", "");
			String dinVoteTicket = din.text().replaceAll(",", "");;
			
			System.out.println(new Date());
			System.out.println("柯的票: " + koVoteTicket + "\t丁的票: " + dinVoteTicket);
			int countVote = Integer.valueOf(koVoteTicket) - Integer.valueOf(dinVoteTicket);

			System.out.println("柯目前贏: " + countVote + "(" + (countVote - lastVoteCount) + ")");
			if(lastVoteCount!=countVote) {
				lastVoteCount = countVote;
			}
			System.out.println("剩餘Box: " + (Integer.valueOf(ticketBoxs[1].substring(0, 4)) - Integer.valueOf(ticketBoxs[0].trim())) + "\n");
			
			System.out.println("心存善念，盡力而為\n");
			Thread.sleep(30000L);
		}
		
	}

	
}
