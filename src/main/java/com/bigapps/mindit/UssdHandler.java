package com.bigapps.mindit;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * This class is a handles the ussd request from the general public.
 * It's handles the ussd session for FillApp.
 * Created by edem on 1/31/17.
 */
@CrossOrigin( "*")
@RestController()
@RequestMapping("/")
public class UssdHandler {

    @Autowired
    Schedular schedular;

    @Autowired
    CaseRepository ghanaCaseRepo;


    @Autowired
    WorldCaseRepository worldCaseRepository;


    @Autowired
    RedisTemplate<String,User> redisTemplate;


     @Autowired
     MessageTemplate messageTemplate;

     final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ArrayList<Integer> userAnswers;

    @Autowired
     RabbitTemplate rabbitTemplate;

    String[] protection = new String[]{
            "Wash your hands frequently",
            "Avoid touching your eyes, mouth and nose",
            "Cover your mouth and nose with your bent elbow or tissue when you cough or sneeze",
            "Avoid crowded places",
            "Stay at home if you feel unwell - even with a slight fever and cough",
            " If you have a fever, cough and difficulty breathing, seek medical care early - but call by phone first",
            "Thank you. Info source: WHO and GHS"
    };


    @GetMapping("/test")
    public String testApi() {
        return "MindIT Covid19 USSD Running";
    }

    @PutMapping("/updateGhana")
    public String updateGhana(@RequestBody Data data){
        GhanaCase ghanaCase =ghanaCaseRepo.findByDate(LocalDate.now());
        ghanaCase.setNumber(data.getConfirmedCount() == null ? ghanaCase.getNumber() : data.getConfirmedCount());
        ghanaCase.setDeathCount(data.getDeathCount() == null ? ghanaCase.getDeathCount() : data.getDeathCount());
        ghanaCase.setRecent(data.getRecentCount() == null ? ghanaCase.getRecoveredCount() : data.getRecentCount());
        ghanaCase.setRecoveredCount(data.getRecoveredCount() == null ? ghanaCase.getRecoveredCount() : data.getRecoveredCount());
        ghanaCaseRepo.save(ghanaCase);
        return "Success";
    }

    @PutMapping("/updateWorld")
    public String updateWorld(@RequestBody Data data){
        WorldCase worldCase =worldCaseRepository.findByDate(LocalDate.now());
        worldCase.setDeathCount(data.getDeathCount() == null ? worldCase.getDeathCount() : data.getDeathCount());
        worldCase.setNumber(data.getConfirmedCount() == null ? worldCase.getNumber() : data.getConfirmedCount());
        worldCase.setRecent(data.getRecentCount() == null ? worldCase.getRecoveredCount() : data.getRecentCount());
        worldCase.setRecoveredCount(data.getRecoveredCount() == null ? worldCase.getRecoveredCount() : data.getRecoveredCount());
        worldCaseRepository.save(worldCase);
        return "Success updated world stats";
    }

    @GetMapping("/setup")
    public String setup() {
        schedular.getDataFromWeb();
        return "Done";
    }

    @PostMapping("ussd")
    public UssdResponse doPost(@RequestBody UssdRequest ussdRequest) {
        ussdRequest = configureRequest(ussdRequest.getSessionId(),ussdRequest);
        UssdResponse ussdResponse = new UssdResponse();
        if (ussdRequest.getMenu() == 0) {
            userAnswers = new ArrayList<>();
            ussdResponse.setContinueSession(true);
            mainMenu(ussdRequest,ussdResponse);
        } else if (ussdRequest.getMenu() > 0) {

            if (ussdRequest.getClientState().equals("welcome")) {
                if (ussdRequest.getMessage().trim().equals("1")) {
                    ussdResponse.setMessage("Emergency Contact for COVID-19.\n 0552222004,0552222005. You will receive the emergency contact via SMS. Select" +
                            "\n1. Main menu" +
                            "\n2. Exit");
                    sendSms(ussdRequest.getMsisdn());
                    setClientState(ussdRequest.getSessionId(),"endnote");
                    ussdResponse.setContinueSession(true);
                } else if (ussdRequest.getMessage().trim().equals("2")) {

                    GhanaCase ghanaCase = getGhanaCase();

                    ussdResponse.setMessage("Cases : " +formatNumber(ghanaCase.getNumber()) +
                            "\nDeaths : " +formatNumber(ghanaCase.getDeathCount()) +
                            "\nRecovered Patients: " +formatNumber(ghanaCase.getRecoveredCount() )+
//                            "\nRecent Cases: " +formatNumber(ghanaCase.getRecent()) +
                            "\n Last Updated - " + ghanaCase.getDate().getDayOfMonth() +"/" +ghanaCase.getDate().getMonth() +"/"+ghanaCase.getDate().getYear()+
                            "\n Enter 1. Main menu 2. Exit"
                    );
                    ussdResponse.setContinueSession(true);
                    setClientState(ussdRequest.getSessionId(),"endnote");
                } else if (ussdRequest.getMessage().trim().equals("3")) {
                    WorldCase worldCase = getWorldCase();
                    ussdResponse.setMessage("Cases : " +formatNumber(worldCase.getNumber()) +
                            "\nDeath : " +formatNumber(worldCase.getDeathCount()) +
                            "\nRecovered Patients: " +formatNumber(worldCase.getRecoveredCount() )+
                            "\nRecent Cases: " +formatNumber(worldCase.getRecent()) +
                            "\n Last Updated - " + worldCase.getDate().getDayOfMonth() +"/" +worldCase.getDate().getMonth() +"/"+worldCase.getDate().getYear()+
                            "\n Enter 1. Main menu 2. Exit"
                    );

                    ussdResponse.setContinueSession(true);
                    setClientState(ussdRequest.getSessionId(),"endnote");
                }
                else if (ussdRequest.getMessage().trim().equals("4")) {
                    ussdResponse.setMessage(""
                            +protection[0]
                            +"\n\n Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect1");
                    ussdResponse.setContinueSession(true);
                }

            }else  if(ussdRequest.getClientState().trim().equals("endnote")){
                endnote(ussdRequest,ussdResponse);

            }else if(ussdRequest.getClientState().equals("protect1")){
                if(ussdRequest.getMessage().trim().equals("1")){
                    ussdResponse.setMessage(
                            protection[1]+"\n"
                            +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect2");
                    ussdResponse.setContinueSession(true);
                }else if (ussdRequest.getMessage().trim().equals("2") || ussdRequest.getMessage().trim().equals("#")){
                    mainMenu(ussdRequest,ussdResponse);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }else if(ussdRequest.getClientState().equals("protect2")){
                if(ussdRequest.getMessage().trim().equals("1")){
                    ussdResponse.setMessage(
                            protection[2]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect3");
                    ussdResponse.setContinueSession(true);
                }else if (ussdRequest.getMessage().trim().equals("2")){
                    ussdResponse.setMessage(
                            protection[0]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect1");
                    ussdResponse.setContinueSession(true);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }
            else if(ussdRequest.getClientState().equals("protect3")){
                if(ussdRequest.getMessage().trim().equals("1")){
                    ussdResponse.setMessage(
                            protection[3]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect4");
                    ussdResponse.setContinueSession(true);
                }else if (ussdRequest.getMessage().trim().equals("2")){
                    ussdResponse.setMessage(
                            protection[1]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect2");
                    ussdResponse.setContinueSession(true);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }else if(ussdRequest.getClientState().equals("protect4")){
                if(ussdRequest.getMessage().trim().equals("1")){
                    ussdResponse.setMessage(
                            protection[4]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect5");
                    ussdResponse.setContinueSession(true);
                }else if (ussdRequest.getMessage().trim().equals("2")){
                    ussdResponse.setMessage(
                            protection[2]+"\n"
                                    +" Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect3");
                    ussdResponse.setContinueSession(true);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }else if(ussdRequest.getClientState().equals("protect5")) {
                if (ussdRequest.getMessage().trim().equals("1")) {
                    ussdResponse.setMessage(
                            protection[5] + "\n\n"
                                    + " Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect6");
                    ussdResponse.setContinueSession(true);
                } else if (ussdRequest.getMessage().trim().equals("2")) {
                    ussdResponse.setMessage(
                            protection[3] + "\n\n"
                                    + " Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect4");
                    ussdResponse.setContinueSession(true);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }else if(ussdRequest.getClientState().equals("protect6")) {
                if (ussdRequest.getMessage().trim().equals("1")) {
                    ussdResponse.setMessage(
                            protection[6] + "\n\n"
                                    + " Enter 2 to go back");
                    setClientState(ussdRequest.getSessionId(),"protect7");
                    ussdResponse.setContinueSession(true);
                } else if(ussdRequest.getMessage().trim().equals("2")) {
                    ussdResponse.setMessage(
                            protection[4] + "\n\n"
                                    + " Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect5");
                    ussdResponse.setContinueSession(true);
                }else {
                    mainMenu(ussdRequest,ussdResponse);
                }
            }else if(ussdRequest.getClientState().equals("protect7")) {
                if (ussdRequest.getMessage().trim().equals("2")) {
                    mainMenu(ussdRequest,ussdResponse);
                } else {
                    ussdResponse.setMessage(
                            protection[5] + "\n\n"
                                    + " Enter 1 for next tip, 2 to go back and # for main menu");
                    setClientState(ussdRequest.getSessionId(),"protect6");
                    ussdResponse.setContinueSession(true);
                }
            }
        }
        logger.info("Ussd Request" + ussdRequest + "Ussd Response" + ussdResponse);
        return ussdResponse;
    }

    private UssdResponse endnote(UssdRequest ussdRequest,UssdResponse ussdResponse) {
        if(ussdRequest.getMessage().trim().equals("1")){
           return mainMenu(ussdRequest,ussdResponse);
        }else{
            ussdResponse.setMessage("Stay safe and Keep calm. We would get through this.");
            ussdResponse.setContinueSession(false);
            setClientState(ussdRequest.getSessionId(),"bye");
            clearSession(ussdRequest.getSessionId());
            return ussdResponse;
        }
    }

    private UssdResponse mainMenu(UssdRequest ussdRequest,UssdResponse ussdResponse) {
        ussdResponse.setMessage("Welcome to i-Mind Covid-19 InfoHub. Select" +
                "\n1. Emergency Contacts\n2. Cases in Ghana\n3. Cases Worldwide  \n4. How to Protect yourself");
        setClientState(ussdRequest.getSessionId(),"welcome");
        return  ussdResponse;
    }

    private GhanaCase getGhanaCase(){
        return ghanaCaseRepo.findByDate(LocalDate.now());
    }

    private WorldCase getWorldCase(){
        return worldCaseRepository.findByDate(LocalDate.now());
    }
    private String formatNumber(Integer number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        return myFormat.format(number);
    }

    private void sendSms(String mobile){
        rabbitTemplate.convertAndSend("mindit_corona-exchange","case",mobile);
    }

    private void setClientState(String sessionId,String clientState){
        User user = redisTemplate.opsForValue().get(sessionId);
        user.setClientState(clientState);
        redisTemplate.opsForValue().set(sessionId,user);
    }
    private void clearSession(String sessionId){
        redisTemplate.delete(sessionId);
    }

    private UssdRequest configureRequest(String sessionId, UssdRequest ussdRequest){
        User user = redisTemplate.opsForValue().get(sessionId);
        if(user == null){
            user = new User("",0);
            redisTemplate.opsForValue().set(sessionId,user);
        }
        ussdRequest.setClientState(user.clientState);
        return ussdRequest;
    }


}
