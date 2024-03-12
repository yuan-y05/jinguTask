package com.jingu.biz.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * zk 工具类
 */
public class ZkTools {

    static  void createPath(ZooKeeper zk, String path, CreateMode createMode, List<ACL> acl) throws InterruptedException, KeeperException {
        String[] list = path.split("/");
        String zkPath = "";
        for (String str : list) {
            if (str != null && !str.isEmpty()){
                zkPath = zkPath + "/" + str;
                if (zk.exists(zkPath, false) == null){
                    zk.create(zkPath, null ,acl, createMode);
                }
            }
        }
    }

    public static void deleteTree(ZooKeeper zk,String path) throws Exception{
        String[] list = getTree(zk,path);
        for(int i= list.length -1;i>=0; i--){
            zk.delete(list[i],-1);
        }
    }

    private static String[] getTree(ZooKeeper zk, String path) throws Exception {
        if (zk.exists(path, null) == null){
            return new String[0];
        }
        List<String> deleteList = new ArrayList<>();
        deleteList.add(path);
        int index = 0;
        while (index < deleteList.size()){
            String temp = deleteList.get(index);
            List<String> children = zk.getChildren(temp, false);
            if (!temp.equalsIgnoreCase("/")){
                temp = temp + "/";
            }
            Collections.sort(children);
            for (int i = children.size() - 1; i >= 0; i--) {
                deleteList.add(index + 1, temp + children.get(i));
            }
            index++;
        }
        return deleteList.toArray(new String[0]);
    }
}
