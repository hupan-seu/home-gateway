package com.hupan.iot.homegateway.service.impl;

import com.hupan.iot.homegateway.entity.LightState;
import com.hupan.iot.homegateway.service.NightLightService;
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
    private final String SERVER_ADDR = "http://192.168.88.103";
    private final String ON_URL = SERVER_ADDR + "/v1/switch1/on";
    private final String OFF_URL = SERVER_ADDR + "/v1/switch1/off";
    private final int DEFAULT_CONTINUE_SECONDS = 5 * 60;
    private final int MAX_CONTINUE_SECONDS = 24 * 60 * 60;

    private boolean lightOn = false;
//    private boolean isWaitTaskOn = false;
    private boolean interrupted = true;
//    private long waitStopTime = 0;

    @PostConstruct
    public void init(){
        // 获取小夜灯当前状态
    }

    @Override
    public void changeState(LightState lightState, int continuedSeconds){
        changeLightState(lightState, continuedSeconds, false);
    }

    @Override
    public boolean isLightOn(){
        return lightOn;
    }

    private synchronized void changeLightState(LightState lightState, int continuedSeconds, boolean withWaiting){
        if(withWaiting && interrupted){
            log.debug("timer interrupted");
            return;
        }

        if(lightState == LightState.LIGHT_ON){
            interrupted = true;
            lightOn = true;
            postHttpRequest(ON_URL);
        } else if(lightState == LightState.LIGHT_OFF){
            interrupted = true;
            lightOn = false;
            postHttpRequest(OFF_URL);
        } else if ((lightState == LightState.LIGHT_ON_A_WHILE)&&(!lightOn)){
            lightOn = true;
            postHttpRequest(ON_URL);

            int seconds = ((continuedSeconds > 0)&&(continuedSeconds <= MAX_CONTINUE_SECONDS))?continuedSeconds:DEFAULT_CONTINUE_SECONDS;
            long stopTime = System.currentTimeMillis() + seconds * 1000;
            interrupted = false;
            waitOff(stopTime);
        }
    }

    private boolean postHttpRequest(String url){
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e){
            log.error("exception when post http: {}, {}", e.getMessage(), e.getStackTrace());
            return false;
        }
        return true;
    }

    private void waitOff(long stopTime){
        new Thread(()->{
            while ((!interrupted) && (System.currentTimeMillis() <= stopTime)){
                log.trace("wait off ...");
                try {
                    Thread.sleep(1 * 1000);
                } catch (Exception e){
                    continue;
                }
            }
            changeLightState(LightState.LIGHT_OFF, 0, true);
        }).run();
    }
}
