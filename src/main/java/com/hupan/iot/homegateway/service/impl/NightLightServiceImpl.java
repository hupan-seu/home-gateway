package com.hupan.iot.homegateway.service.impl;

import com.hupan.iot.homegateway.entity.LightState;
import com.hupan.iot.homegateway.service.NightLightService;
import com.hupan.iot.homegateway.service.PiGpioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;

@Slf4j
@Service
public class NightLightServiceImpl implements NightLightService {
    private final int DEFAULT_CONTINUE_SECONDS = 5 * 60;
    private final int MAX_CONTINUE_SECONDS = 24 * 60 * 60;
    private final int TOO_CLOSE_TIME = 5 * 1000;

    private final int LIGHT_ON_TIMES = 90;          // 灯点亮持续时间，单位：秒

    @Value("${homegateway.switch.url}")
    private String serverAddr;

    @Autowired
    private PiGpioService piGpioService;

    private String onUrl;
    private String offUrl;

    private boolean lightOn = false;
    private boolean interrupted = true;
    private long lastWaitFinishTime = 0;

    @PostConstruct
    public void init() {
        piGpioService.addLightSensorListener(this::sensorToDark);

        onUrl = serverAddr + "/v1/switch1/on";
        offUrl = serverAddr + "/v1/switch1/off";

        // 获取小夜灯当前状态
    }

    @Override
    public void changeState(LightState lightState, int continuedSeconds) {
        changeLightState(lightState, continuedSeconds, false);
    }

    @Override
    public boolean isLightOn() {
        return lightOn;
    }

    private void sensorToDark() {
        if (isRightTime()) {
            changeLightState(LightState.LIGHT_ON_A_WHILE, LIGHT_ON_TIMES, false);
        }
    }

    private synchronized void changeLightState(LightState lightState, int continuedSeconds, boolean withWaiting) {
        if (withWaiting && interrupted) {
            log.debug("timer interrupted");
            return;
        }

        if (lightState == LightState.LIGHT_ON) {
            interrupted = true;
            postHttpRequest(onUrl);
            lightOn = true;
        } else if (lightState == LightState.LIGHT_OFF) {
            interrupted = true;
            postHttpRequest(offUrl);
            lightOn = false;
        } else if ((lightState == LightState.LIGHT_ON_A_WHILE) && (!lightOn) && (!tooClose())) {
            postHttpRequest(onUrl);
            lightOn = true;

            int seconds = ((continuedSeconds > 0) && (continuedSeconds <= MAX_CONTINUE_SECONDS)) ? continuedSeconds : DEFAULT_CONTINUE_SECONDS;
            long stopTime = System.currentTimeMillis() + seconds * 1000;
            interrupted = false;
            waitOff(stopTime);
        }
    }

    private void postHttpRequest(String url) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e) {
            log.error("exception when post http: {}, {}", e.getMessage(), e.getStackTrace());
            // throw exception
        }
    }

    private boolean tooClose() {
        if (System.currentTimeMillis() - lastWaitFinishTime <= TOO_CLOSE_TIME) {
            return true;
        } else {
            return false;
        }
    }

    private void waitOff(long stopTime) {
        new Thread(() -> {
            while ((!interrupted) && (System.currentTimeMillis() <= stopTime)) {
                log.trace("wait off ...");
                try {
                    Thread.sleep(1 * 1000);
                } catch (Exception e) {
                    continue;
                }
            }
            changeLightState(LightState.LIGHT_OFF, 0, true);
            lastWaitFinishTime = System.currentTimeMillis();
        }).run();
    }

    private boolean isRightTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if ((hours >= 21) || ((hours >= 0) && (hours <= 2))) {
            return true;
        } else {
            return false;
        }
    }
}
