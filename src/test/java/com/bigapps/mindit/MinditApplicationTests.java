package com.bigapps.mindit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinditApplicationTests {

	@Autowired
	CaseRepository ghanaCaseRepo;

	@Autowired
	WorldCaseRepository worldCaseRepository;

	@Test
	public void contextLoads() {
	}


	@Test
	public void scrapWebforGhana() {
		Document doc = null;
		try {
			doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/ghana/").get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(doc != null || ! doc.toString().trim().equals("")){
			Elements node = doc.select("div#maincounter-wrap");
			String cases = node.get(0).select("span[style='color:#aaa']").text();
			String deaths = node.get(1).getElementsByTag("span").get(0).text();
			String recovered = node.get(2).getElementsByTag("span").get(0).text();

			System.out.println("cases --- "+ format(cases));
			System.out.println("deaths --- "+ format(deaths));
			System.out.println("recovered --- "+ format(recovered));

		}

	}

	 public Integer format(String value) {

		return Integer.valueOf(value.replaceAll(",",""));
	 }


	@Test
	public void shouldScrapWeb() {

		Document doc = null;
		try {
			doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/ghana/").get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(doc != null || ! doc.toString().trim().equals("")){
			Elements node = doc.select("div#maincounter-wrap");
			String cases = node.get(0).select("span[style='color:#aaa']").text();
			String deaths = node.get(1).getElementsByTag("span").get(0).text();
			String recovered = node.get(2).getElementsByTag("span").get(0).text();

			GhanaCase ghanaCase = ghanaCaseRepo.findByDate(LocalDate.now());
			if(ghanaCase != null && deaths != null){
				ghanaCase.setNumber(format(cases));
				ghanaCase.setDeathCount(format(deaths));
				ghanaCase.setRecoveredCount(format(recovered));
			}else{
				ghanaCase = new GhanaCase();
				ghanaCase.setNumber(format((cases)));
				ghanaCase.setRecent(0);
				ghanaCase.setDeathCount(5);
				ghanaCase.setRecoveredCount(31);
				ghanaCase.setDate(LocalDate.now());
			}
			ghanaCaseRepo.save(ghanaCase);
		}

		try {
			doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if ( doc != null){
			Elements mainCounters = doc.select("div.maincounter-number");
			String total =mainCounters.get(0).children().first().text().replaceAll(",","");
			String death = mainCounters.get(1).children().first().text().replaceAll(",","");
			String recovered = mainCounters.get(2).children().first().text().replaceAll(",","");

			WorldCase worldCase = worldCaseRepository.findByDate(LocalDate.now());
			if(worldCase != null){
				worldCase.setRecent(Integer.valueOf(total)-worldCase.getNumber());
				worldCase.setNumber(Integer.valueOf(total));
				worldCase.setRecoveredCount(Integer.valueOf(recovered));
				worldCase.setDeathCount(Integer.valueOf(death));
			}else {
				worldCase = new WorldCase();
				worldCase.setNumber(Integer.valueOf(total));
				worldCase.setRecoveredCount(Integer.valueOf(recovered));
				worldCase.setDeathCount(Integer.valueOf(death));
				worldCase.setRecent(0);
				worldCase.setDate(LocalDate.now());
			}

			worldCaseRepository.save(worldCase);
		}

	}

}
