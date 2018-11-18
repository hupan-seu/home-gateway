package com.hupan.home.smartgateway;

import com.hupan.home.smartgateway.task.OpenNightlightTask;

public class StartMain {
    public static void main(String[] args){
        OpenNightlightTask openNightlightTask = new OpenNightlightTask();
        openNightlightTask.start();
    }
}
