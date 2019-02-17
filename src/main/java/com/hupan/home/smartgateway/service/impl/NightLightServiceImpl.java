package com.hupan.home.smartgateway.service.impl;

import com.hupan.home.smartgateway.entity.LightState;
import com.hupan.home.smartgateway.service.NightLightService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class NightLightServiceImpl implements NightLightService {
    private static String SERVER_ADDR = "http://192.168.88.103";
    private static String ON_URL = SERVER_ADDR + "/v1/switch1/on";
    private static String OFF_URL = SERVER_ADDR + "/v1/switch1/off";
    private static int DEFAULT_CONTINUE_SECONDS = 90;
    private static int MAX_CONTINUE_SECONDS = 24 * 60 * 60;

    private boolean isLightOn = false;
    private boolean isWaitTaskOn = false;
    private long waitStopTime = 0;

    @PostConstruct
    public void init(){
        // 获取小夜灯当前状态
    }

    @Override
    public synchronized void changeLightState(LightState lightState, int continuedSeconds){
        if(lightState == LightState.LIGHT_ON){
            isWaitTaskOn = false;
            isLightOn = true;
            postHttpRequest(ON_URL);
        } else if(lightState == LightState.LIGHT_OFF){
            isWaitTaskOn = false;
            isLightOn = false;
            postHttpRequest(OFF_URL);
        } else if ((lightState == LightState.LIGHT_ON_A_WHILE)&&(!isLightOn)){
            int seconds = ((continuedSeconds > 0)&&(continuedSeconds <= MAX_CONTINUE_SECONDS))?continuedSeconds:DEFAULT_CONTINUE_SECONDS;
            waitStopTime = System.currentTimeMillis() + seconds * 1000;
            isWaitTaskOn = true;
            postHttpRequest(ON_URL);
        }
    }

    @Override
    public boolean isLightOn(){
        return true;
    }

    private boolean postHttpRequest(String url){
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    // 定时5秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")
    private void checkPeriod(){
        if(!isWaitTaskOn){
            return;
        }
        long nowTime = System.currentTimeMillis();
        if(nowTime > waitStopTime){
            changeLightState(LightState.LIGHT_OFF, 0);
        }
    }
}
