package org.vision.eventquery.monitor;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController {

    @Autowired
    MonitorService monitorService;

    @RequestMapping(method = RequestMethod.GET, value = "/monitor")
    public String getMonitor(){

        MonitorInfo monitorInfo = monitorService.getMonitorInfo();
        String monitorInfoString = JSON.toJSONString(monitorInfo);
//        Object parse = JSONObject.parse(monitorInfoString);
        return monitorInfoString;
    }
}
