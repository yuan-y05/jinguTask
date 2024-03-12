#!/bin/bash

jar_name='mtop-service.jar'
host_name='mtop-service'

#拷贝jar包
ansible ${host_name} -m shell -a "cp /opt/app/${host_name}/${jar_name} /opt/app/${host_name}/${jar_name}.bak " -s
if [ $? != 0 ]
then
    echo "Backup failed"
    exit 1
fi

sudo cp /home/haixiang/${jar_name} /home/xiazl/package
if [ $? != 0 ]
then
    echo "copy to package directory failed"
    exit 2
fi

for s in {1..2}
do
    # 将文件的所有者（owner）和所属组（group）都设置为 tomcat
    ansible ${host_name}-${s} -m copy -a "src=/home/xiazl/package/${jar_name} dest=/opt/app/${host_name} owner=tomcat group=tomcat" -s
    if [ $? != 0 ]
    then
        echo "copy to remote directory failed"
        exit 3
    fi
    # 启动jar包
    ansible ${host_name}-${s} -m service -a "name=${host_name} state=restarted" -s
    if [ $? != 0 ]
    then
        echo "service restart failed"
        exit 4
    fi
    #echo "mtop-service第${s}组正在重启，大约需等待60秒,请查看应用日志确认状态"
    #sleep 60
    if [ $s = 2 ];then
        echo "mtop-service发布完成，请查看业务日志确认状态"
        break;
    fi
    read -p "请确认是否继续发布，确认输入Y/y，任意键退出发布: " deploy
    if [ $deploy = Y ] || [ $deploy = y ];then
        echo "五秒钟后继续发布"
        sleep 5
    else
        echo "发布中断"
        exit 250
    fi
done