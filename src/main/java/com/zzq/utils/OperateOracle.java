package com.zzq.utils;


import com.zzq.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OperateOracle {

    // 定义连接所需的字符串
    // 192.168.0.X是本机地址(要改成自己的IP地址)，1521端口号，XE是精简版Oracle的默认数据库名
    private static String USERNAMR = "ZZQ";
    private static String PASSWORD = "ZZQZZQ";
    private static String DRVIER = "oracle.jdbc.driver.OracleDriver";
    private static String URL = "jdbc:oracle:thin:@120.76.132.101:1521:orcl2";

    // 创建一个数据库连接
    Connection connection = null;
    // 创建预编译语句对象，一般都是用这个而不用Statement
    PreparedStatement pstm = null;
    // 创建一个结果集对象
    ResultSet rs = null;


//    /**
//     * 更新寻常生活 app薅羊毛 结果
//     *
//     */
//    public void updateUserData(String appName,User user) {
//        connection = getConnection();
//        String sqlStr = "UPDATE APP_AUTODO_RESULT set app_userscore=? , device_id=? , user_agent=? ,earn=?,app_userid=? ,code_mine=? where app_name= ? and app_usertel =?";
//
//        try {
//            // 执行插入数据操作,
//            pstm = connection.prepareStatement(sqlStr);
//            pstm.setInt(1, user.getScore());
//            pstm.setString(2, user.getDeviceId());
//            pstm.setString(3, user.getUserAgent());
//            pstm.setInt(4, user.getEarn());
//            pstm.setString(5, user.getUserId());
//            pstm.setString(6,"");
//            pstm.setString(7, appName);
//            pstm.setString(8, user.getTel());
//
//            pstm.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            ReleaseResource();
//        }
//    }

    


    /**
     * 获取Connection对象
     *
     * @return
     */
    public Connection getConnection() {
        try {
            Class.forName(DRVIER);
            connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
            System.out.println("成功连接数据库");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not find !", e);
        } catch (SQLException e) {
            throw new RuntimeException("get connection error!", e);
        }

        return connection;
    }

    /**
     * 释放资源
     */
    public void ReleaseResource() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * 从数据库取现成的user
     */
    public List<User> getUsers(){
    	List<User> cnUsers = new ArrayList<User>();
    	 connection = getConnection();
         String sql = "select * from app_autodo_result where is_del = 0 and app_name='汽车报价大全' ";
         try {
             pstm = connection.prepareStatement(sql);
             rs = pstm.executeQuery();
             while (rs.next()) {
            	 User user =  new User();
            	 user.setTel(rs.getString("APP_USERPASSWORD"));
            	 user.setPassword(rs.getString("PASSWORD"));
            	 user.setScore(rs.getInt("APP_USERSCORE"));
            	 if(rs.getString("DEVICE_ID")==""||rs.getString("DEVICE_ID")==null) {
            		 user.setDeviceId(Utils.randomHexString(16));
            	 }else {
            		 user.setDeviceId(rs.getString("DEVICE_ID"));
            	 }
            	 
            	 if(rs.getString("USER_AGENT")==""||rs.getString("USER_AGENT")==null) {
            		 Random random = new Random();
            	     int s = random.nextInt(Utils.user_agents.length);
            		 user.setUserAgent(Utils.user_agents[s]);
            	 }else {
            		 user.setUserAgent(rs.getString("USER_AGENT"));
            	 }
                 cnUsers.add(user);
             }
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             ReleaseResource();
         }
    	return cnUsers;
    }
    
    
    
    /**
     * 从数据库取出需要修改密码的user
     */
    public List<User> getChangePwdUsers(int blankNum){
    	List<User> cnUsers = new ArrayList<User>();
    	 connection = getConnection();
         String sql = "select t1.*"+
        		 		" from (select rownum rn, a.* from APP_AUTODO_RESULT a order by id asc) t1,"+
        		 		" (select rownum rn, b.* from APP_AUTODO_RESULT b order by id asc) t2"+
        		 		" where t1.app_userpassword = t2.app_userpassword"+
        		 		" and abs(t1.app_userid - t2.app_userid) < ?"+
        		 		" and mod(t1.rn, 2) = 1"+
        		 		" and t1.rn = t2.rn - 1";
         try {
             pstm = connection.prepareStatement(sql);
             pstm.setInt(1, blankNum);
             System.out.println(sql);
             rs = pstm.executeQuery();
             while (rs.next()) {
            	 User user =  new User();
            	 user.setTel(rs.getString("APP_USERTEL"));
            	 user.setPassword(rs.getString("APP_USERPASSWORD"));
            	 user.setPassword(rs.getString("PASSWORD"));
            	 if(rs.getString("DEVICE_ID")==""||rs.getString("DEVICE_ID")==null) {
            		 user.setDeviceId(Utils.randomHexString(16));
            	 }else {
            		 user.setDeviceId(rs.getString("DEVICE_ID"));
            	 }
            	 
            	 if(rs.getString("USER_AGENT")==""||rs.getString("USER_AGENT")==null) {
            		 Random random = new Random();
            	     int s = random.nextInt(Utils.user_agents.length);
            		 user.setUserAgent(Utils.user_agents[s]);
            	 }else {
            		 user.setUserAgent(rs.getString("USER_AGENT"));
            	 }
                 cnUsers.add(user);
             }
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             ReleaseResource();
         }
    	return cnUsers;
    }
    
    
    /**
     * 更新菜鸟理财 app薅羊毛 密码
     *
     */
    public boolean updateResult(User user) {
    	Boolean flag = false;
        connection = getConnection();
        // String sql =
        // "insert into student values('1','王小军','1','17','北京市和平里七区30号楼7门102')";
//        String sql = "select count(*) from student where 1 = 1";
        String sqlStr = "UPDATE APP_AUTODO_RESULT set app_userscore=? , earn =? where app_name= '汽车报价大全' and app_userpassword =?";

        try {
            // 执行插入数据操作,
            pstm = connection.prepareStatement(sqlStr);
            pstm.setInt(1, user.getScore());
            pstm.setInt(2, user.getEarn());
            pstm.setString(3, user.getTel());
           
            pstm.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
            
        } finally {
            ReleaseResource();
        }
        return flag;
    }
    
}