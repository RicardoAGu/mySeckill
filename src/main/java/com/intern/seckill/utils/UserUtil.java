package com.intern.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intern.seckill.pojo.User;
import com.intern.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于在MySQL数据库里生成用户，登录他们、并获取对应uid的工具类。所获得的uid将用于秒杀功能的压力测试。
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L+i);
            user.setNickname("user"+i);
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            user.setPassword(MD5Util.inputPassToDBPass("123456", "1a2b3c4d"));
            user.setSalt("1a2b3c4d");
            users.add(user);
        }
        System.out.println("创建用户中...");
//        // 插入数据库
//        Connection conn = getConn();
//        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for (int i = 0; i < count; i++) {
//            User user = users.get(i);
//            pstmt.setInt(1, user.getLoginCount());
//            pstmt.setString(2, user.getNickname());
//            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(4, user.getSalt());
//            pstmt.setString(5, user.getPassword());
//            pstmt.setLong(6, user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.clearParameters();
//        conn.close();
//        System.out.println("已插入数据库");
        // 登录，生成userTicket
        String loginUrl = "http://localhost:8080/login/doLogin";
        File file = new File("C:/Users/23748/Desktop/config.txt");
        if (file.exists()) file.delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(loginUrl);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println("创建UserTicket:"+user.getId());
            String row = user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("写入文件："+userTicket);
        }
        raf.close();
        System.out.println("over");
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&ServerTimezone=Asia/Shanghai";
        String username = "root";
        String password = "056981_Gu";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
