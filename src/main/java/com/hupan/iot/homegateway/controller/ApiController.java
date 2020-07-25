package com.hupan.iot.homegateway.controller;

//import com.hupan.iot.homegateway.entity.LightState;
//import com.hupan.iot.homegateway.service.NightLightService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@RequestMapping("/v1")
//@ConditionalOnWebApplication
public class ApiController {

//    @Autowired
//    private NightLightService nightLightService;
//
//    /** 心跳发送*/
//    @RequestMapping(value = "/switch1/{status}", method = RequestMethod.POST, produces = "application/json")
//    public DeferredResult<ResponseEntity> postLightStatus(@PathVariable("status") String status, HttpServletRequest httpRequest) {
//
//        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<ResponseEntity>();
//
//        if(status.equals("on")){
//            nightLightService.changeState(LightState.LIGHT_ON, 0);
//        } else if(status.equals("off")){
//            nightLightService.changeState(LightState.LIGHT_OFF, 0);
//        } else {
//            responseWriter.setResult(new ResponseEntity<>("", HttpStatus.BAD_REQUEST));
//            return responseWriter;
//        }
//
//        responseWriter.setResult(new ResponseEntity<>("", HttpStatus.OK));
//        return responseWriter;
//    }
}
