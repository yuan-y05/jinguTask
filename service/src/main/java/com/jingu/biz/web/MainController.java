package com.jingu.biz.web;

import com.jingu.biz.ConsoleManager;
import com.jingu.biz.ZkScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * 主页面
     */
    @RequestMapping("/ttm")
    public String mPage(){
        return "manage";
    }

    @RequestMapping(value = "/ttm/servers", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public String servers(){
        ZkScheduleManager manager = null;
        try {
            manager = ConsoleManager.getScheduleManager();
        }catch (Exception e){
            logger.info(">>> get zk manager fail:{}", e.getMessage(), e);
        }
        List<String> servers = null;
        try{
            servers = manager.getScheduleDataManager().loadScheduleServerNames();
        }catch (Exception e){
            logger.info(">>> get zookeeper node fail:{}", e.getMessage(), e);
        }
        logger.info("server nodes：{}", servers);
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {
                String ser = servers.get(i);
                Map<String, String> server = new HashMap<String, String>();
                server.put("serverName", ser);
                if (manager.getScheduleDataManager().isLeader(ser, servers)) { // 是调度节点
                    server.put("isLeader", "1");
                } else {
                    server.put("isLeader", "0");
                }
                result.add(server);
            }
        }
        return result.toString();
    }
}
