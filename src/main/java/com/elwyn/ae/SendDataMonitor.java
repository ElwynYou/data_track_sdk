package com.elwyn.ae;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Package com.elwyn.ae
 * @Description:发送url数据的监控者,用于启动一个单独的线程来发送数据
 * @Author elwyn
 * @Date 2017/8/14 21:53
 * @Email elonyong@163.com
 */
public class SendDataMonitor {
    private static final Logger LOGGER = Logger.getGlobal();
    //队列用于存储发送url
    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    //用于单列的一个类对象
    private static SendDataMonitor monitor = null;

    private SendDataMonitor() {
        //私有构造方法,进行单列模式的创建
    }

    public static SendDataMonitor getSendDataMonitor() {
        if (monitor == null) {
            synchronized (SendDataMonitor.class) {
                if (monitor == null) {
                    monitor = new SendDataMonitor();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SendDataMonitor.monitor.run();
                        }
                    });
                    //测试时不设置守护模式
                    //thread.setDaemon(true);
                    thread.start();
                }
            }
        }
        return monitor;
    }

    /**
     * 添加一个url到队列中
     *
     * @param url
     * @throws InterruptedException
     */
    public static void addSendUrl(String url) throws InterruptedException {
        getSendDataMonitor().queue.put(url);
    }

    private void run() {
        while (true) {
            try {
                String url = this.queue.take();
                //正式发送url
                HttpRequestUtil.sendData(url);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "发送url异常", e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用于发送数据的http工具类
     */
    public static class HttpRequestUtil {
        /**
         * 具体发送url的方法
         *
         * @param url
         */
        public static void sendData(String url) throws IOException {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try {
                URL url1 = new URL(url);
                connection = (HttpURLConnection) url1.openConnection();
                connection.setConnectTimeout(5000);//连接过期时间
                connection.setReadTimeout(50000);//读取数据过期时间
                connection.setRequestMethod("GET");
                System.out.println("发送" + url);
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedReader != null) {

                    bufferedReader.close();
                }
            }
        }
    }
}
