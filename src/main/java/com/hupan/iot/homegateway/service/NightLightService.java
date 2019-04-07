package com.hupan.iot.homegateway.service;

import com.hupan.iot.homegateway.entity.LightState;

public interface NightLightService {
    // 点亮一小会儿优先级低于点亮和熄灭，点亮一小会儿过程中调用了点亮和熄灭，打断点亮和熄灭。
    // 灯亮的状态下调用点亮一小会儿，不做任何事情
    void changeState(LightState lightState, int continuedSeconds);

    // 查询当前状态
    boolean isLightOn();
}
