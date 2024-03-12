package com.jingu.biz.zk;

import com.google.gson.*;
import com.jingu.biz.core.IScheduleDataManager;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScheduleDataManager4ZK implements IScheduleDataManager {

    public static final Logger logger = LoggerFactory.getLogger(ScheduleDataManager4ZK.class);

    private static final String NODE_SERVER = "server";
    private static final String NODE_TASK = "task";
    private ZKManager zkManager;
    private String serverCode;
    private Gson gson ;
    private String pathServer;
    private String pathTask;
    private long zkBaseTime = 0;
    private long localBaseTime = 0;

    public ScheduleDataManager4ZK(ZKManager manager) throws Exception {
        this.zkManager = manager;
        this.serverCode = manager.getServerCode();
        this.gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.pathServer = this.zkManager.getRootPath() +"/" + NODE_SERVER;
        this.pathTask = this.zkManager.getRootPath() +"/" + NODE_TASK;
        if (this.getZooKeeper().exists(this.pathServer, false) == null) {
            ZkTools.createPath(getZooKeeper(),this.pathServer, CreateMode.PERSISTENT, this.zkManager.getAcl());
        }
        this.localBaseTime = System.currentTimeMillis();
        // 这里一大圈就是为了获取zookeeper 服务区时间？
        String tempPath = this.zkManager.getZooKeeper().create(this.zkManager.getRootPath() + "/systemTime",null, this.zkManager.getAcl(), CreateMode.EPHEMERAL_SEQUENTIAL);
        Stat tempStat = this.zkManager.getZooKeeper().exists(tempPath, false);
        this.zkBaseTime = tempStat.getCtime();
        ZkTools.deleteTree(getZooKeeper(), tempPath);
        if(Math.abs(this.zkBaseTime - this.localBaseTime) > 5000){
            logger.error("请注意，Zookeeper服务器时间与本地时间相差 ： " + Math.abs(this.zkBaseTime - this.localBaseTime) +" ms");
        }
    }

    @Override
    public List<String> loadScheduleServerNames() throws Exception {
        String zkPath = this.pathServer;
        if (this.getZooKeeper().exists(zkPath, false) == null){
            return new ArrayList<>();
        }
        List<String> serverList = this.getZooKeeper()
                .getChildren(zkPath, false);
        Collections.sort(serverList, new Comparator<String>() {
            public int compare(String u1, String u2) {
                return u1.substring(u1.lastIndexOf("$") + 1).compareTo(
                        u2.substring(u2.lastIndexOf("$") + 1));
            }
        });
        return serverList;
    }

    @Override
    public boolean isLeader(String uuid, List<String> serverList) {
        return false;
    }









    public ZooKeeper getZooKeeper() throws Exception {
        return this.zkManager.getZooKeeper();
    }

    private static class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp>{

        @Override
        public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }

            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = (Date) format.parse(jsonElement.getAsString());
                return new Timestamp(date.getTime());
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateFormatAsString = format.format(new Date(timestamp.getTime()));
            return new JsonPrimitive(dateFormatAsString);
        }
    }
}
