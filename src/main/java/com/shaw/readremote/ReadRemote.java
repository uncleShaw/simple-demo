package com.shaw.readremote;

import java.io.*;
import java.nio.charset.Charset;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;



public class ReadRemote {
    public static void main(String[] args) {
        String remotePath = "/logs/note/note_info.log";//远程文件地址
        String localPath = "D:/logs/test.log";//本地文件地址
        String host = "127.0.0.1";//服务器地址
        int port = 22;//端口
        String username = "root";//用户名
        String password = "root";//密码
        //登录
        Connection ss = getConnect(host, username, password, port);
        if (fileExist(remotePath, ss)) {
            readLogFile(remotePath, ss, localPath);
        }
    }

    /**
     * 登录
     * @param host
     * @param username
     * @param password
     * @param port
     * @return
     */
    public static Connection getConnect(String host, String username, String password, int port) {
        Connection conn = new Connection(host, port);
        try {
            // 连接到主机
            conn.connect();
            // 使用用户名和密码校验
            boolean isconn = conn.authenticateWithPassword(username, password);
            if (!isconn) {
                System.out.println("用户名称或者是密码不正确");
            } else {
                System.out.println("服务器连接成功.");
                return conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean fileExist(String path, Connection conn) {
        if (conn != null) {
            Session ss = null;
            try {
                ss = conn.openSession();
                ss.execCommand("ls -l ".concat(path));//执行Linux命令
                InputStream is = new StreamGobbler(ss.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while (true) {
                    String lineInfo = brs.readLine();

                    if (lineInfo != null) {
                        line = line + lineInfo;
                    } else {
                        break;
                    }
                }
                brs.close();
                if (line != null && line.length() > 0 && line.startsWith("-")) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 连接的Session和Connection对象都需要关闭
                if (ss != null) {
                    ss.close();
                }
            }
        }
        return false;
    }


    public static void readLogFile(String remotePath, Connection conn,String localPath) {
        if (conn != null) {
            Session ss = null;
            File file = new File(localPath);
            BufferedWriter bw = null;
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(localPath), Charset.forName("UTF-8"));
                bw = new BufferedWriter(writer);


            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ss = conn.openSession();
                ss.execCommand("tail -n 10 -f ".concat(remotePath));
                InputStream is = new StreamGobbler(ss.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = brs.readLine();
                    if (line == null) {
                        break;
                    } else {
                        bw.write(line+"\n");
                        bw.flush();
                    }
                }
                brs.close();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 连接的Session和Connection对象都需要关闭
                if (ss != null) {
                    ss.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}
