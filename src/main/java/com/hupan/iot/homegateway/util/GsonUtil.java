package com.hupan.iot.homegateway.util;

import com.google.gson.JsonParser;
import lombok.Getter;

public class GsonUtil {

    @Getter
    private static final JsonParser PARSER = new JsonParser();
}
