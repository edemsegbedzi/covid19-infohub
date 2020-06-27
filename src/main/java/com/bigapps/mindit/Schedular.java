package com.bigapps.mindit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class Schedular {

    @Autowired
    CaseRepository ghanaCaseRepo;

    @Autowired
    WorldCaseRepository worldCaseRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void run(){
        getDataFromWeb();

    }

    public void getDataFromWeb() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/ghana/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(doc != null || ! doc.toString().trim().equals("")){
            Elements node = doc.select("div#maincounter-wrap");
            Integer cases = format(node.get(0).select("span[style='color:#aaa']").text());
            Integer deaths = format(node.get(1).getElementsByTag("span").get(0).text());
            Integer recovered = format(node.get(2).getElementsByTag("span").get(0).text());

            GhanaCase ghanaCase = ghanaCaseRepo.findByDate(LocalDate.now());
            if(ghanaCase != null){

                ghanaCase.setNumber(cases);
                ghanaCase.setRecent(cases - (ghanaCase.getNumber() == null ? 0 : ghanaCase.getNumber()));
                ghanaCase.setDeathCount(deaths);
                ghanaCase.setRecoveredCount(recovered);
            }else{
                GhanaCase oldCase = ghanaCaseRepo.findTopByOrderByIdDesc();
                ghanaCase = new GhanaCase();
                ghanaCase.setNumber(cases);
                ghanaCase.setRecent(cases - oldCase.getNumber());
                ghanaCase.setDeathCount(deaths);
                ghanaCase.setRecoveredCount(recovered);
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

    public Integer format(String value) {

        return Integer.valueOf(value.replaceAll(",",""));
    }
}
