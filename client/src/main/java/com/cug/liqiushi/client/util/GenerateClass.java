package com.cug.liqiushi.client.util;


import java.io.File;
import java.io.IOException;

/**
 * Created by lqs on 2018/5/2.
 */
public class GenerateClass {
    public static void main(String[] args) {
        String protoFile = "BrokerMessage.proto";
        String currentPath = System.getProperty("user.dir") + File.separator + "client\\src\\main\\resources";
        System.out.println(currentPath);
        System.out.println(currentPath + File.separator + protoFile);
        String out = "E:\\IdeaProjects\\BRMQ\\client\\src\\main\\java\\com\\cug\\liqiushi\\client\\vo";
        String strCmd = "protoc -I=" + currentPath + " --java_out= "+out +" "+ currentPath + File.separator + protoFile;
        try {
            Runtime.getRuntime().exec(strCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }//通过执行cmd命令调用protoc.exe程序
    }
/*    C:\Users\lqs>protoc -I E:\IdeaProjects\BRMQ\client\src\main\resources --java_out
=E:\IdeaProjects\BRMQ\client\src\main\java E:\IdeaProjects\BRMQ\client\src\main\
    resources\BrokerMessage.proto*/
}
