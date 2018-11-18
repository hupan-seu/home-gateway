package com.hupan.home.smartgateway.task;

import com.hupan.home.smartgateway.hardware.PiHardware;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class OpenNightlightTask {
    private static final int DELAY_SECONDS = 90;
    private static String ON_URL = "http://192.168.88.103/v1/switch1/on";
    private static String OFF_URL = "http://192.168.88.103/v1/switch1/off";

    private final PiHardware piHardware;
    private long lastActionTime = 0;
    private long lightOnTime = 0;
    private int taskState = 0;

    public OpenNightlightTask(){
        piHardware = new PiHardware(false);
    }

    public void start() {
        piHardware.addKeyListener(new LightSeneorListener());
        while (true){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                continue;
            }
            if(taskState == 1){
                long period = (System.currentTimeMillis() - lightOnTime)/1000;
                if(period > DELAY_SECONDS){
                    System.out.println("light off");
                    setLightState(OFF_URL);
                    taskState = 0;
                }
            }
        }
    }

    private boolean setLightState(String url){
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost postOn = new HttpPost(url);
            httpClient.execute(postOn);
            httpClient.close();
        } catch (Exception e){
            System.out.println(e);
            return false;
        }

        return true;
    }

    /**
     *
     */
    public class LightSeneorListener implements GpioPinListenerDigital {

        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            long nowTime = System.currentTimeMillis();
            if((nowTime - lastActionTime) < 1000){
                return;
            }
            if(taskState != 0){
                return;
            }

            lastActionTime = nowTime;
            if(event.getState() == PinState.HIGH){  //
                System.out.println("high");
                //
                lightOnTime = System.currentTimeMillis();
                setLightState(ON_URL);
                taskState = 1;
            }
        }
    }
}
