package com.zzq.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzq.entity.User;
import com.zzq.utils.*;
import com.zzq.utils.Base64;

import java.io.IOException;
import java.util.*;

/**
 * @author zhangzhiqiang
 * @date 2018-11-06 10:18
 * &Desc 单例流程
 */
public class SingleFlow {

    public static void main(String[] args) {
        User user = new User();
        user.setTel("15923584508");
        user.setPassword("475546259");
        SingleFlow s = new SingleFlow();
//        try {
//            s.start(user);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(new Random().nextInt(26));
        System.out.println( Base64.encodeToString(RSA.encrypt("475546259"), 0));
    }

    public void start(User user) throws IOException{
        OneTLSPool2 oneTLSPool2 = new OneTLSPool2();
        LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
        //1 登录
        map.put("appid","7");
        map.put("city","3101");
        map.put("method","userlogin.baalogin");
        map.put("password",user.getPassword());
        map.put("username",user.getTel());
        map.put("ver","9.0.1");
        String url = "http://msn.api.app.yiche.com/api.ashx";
        map = Sign.setSign(map);
        String  login_result = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        System.out.println("登录结果："+login_result);
        //获取UserToken
        JSONObject finaJson = JSONObject.parseObject(login_result);
        String userToken = finaJson.getJSONObject("Data").getString("UserToken");
        //签到
        map.clear();
        map.put("method","credit.sign");
        map.put("token",userToken);
        map = Sign.setSign(map);
        String sign_result = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        //获取话题列表
        map.clear();
        map.put("method","topic.list");
        map.put("isgood","1");
        map.put("ver","9.0.1");
        map.put("startindex","1");
        map.put("pagesize","10");
        map = Sign.setSign(map);
        String top_list_str = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject top_list_json = JSONObject.parseObject(top_list_str);
        JSONArray top_list_jsonarray= top_list_json.getJSONObject("Data").getJSONArray("List");
        List<String> topic_ids = new ArrayList<>();
        for (Object obj :top_list_jsonarray){
            JSONObject topic = (JSONObject) obj;
            topic_ids.add(topic.getString("TopicId"));
        }
        String favorite_topic = topic_ids.get(new Random().nextInt(topic_ids.size()));
        //收藏话题
        map.clear();
        map.put("appid","7");
        map.put("method","topic.favorite");
        map.put("token",userToken);
        map.put("topicid",favorite_topic);
        map.put("ver","9.0.1");
        map = Sign.setSign(map);
        String favorite_result  = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        //分享话题到朋友圈
        map.clear();
        map.put("method","credit.share");
        map.put("type","1");
        map.put("token",userToken);
        map.put("itemid",favorite_topic);
        map.put("token",userToken);
        map.put("ver","9.0.1");
        map.put("time",Calendar.getInstance().getTimeInMillis()/1000+"");
        map = Sign.setSign(map);
        String share_result = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);


        //点赞话题5次以上
        for(int i=0;i<6;i++){
            int random = new Random().nextInt(topic_ids.size());
            String like_topicid = topic_ids.get(random);
            map.clear();
            map.put("appid","7");
            map.put("method","topic.like");
            map.put("token",userToken);
            map.put("topicid",like_topicid);
            map.put("ver","9.0.1");
            map = Sign.setSign(map);
            String like_result  = oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
            topic_ids.remove(random);
        }
        //获取资讯列表
        url = "http://news.app.yiche.com/api.ashx";
        map.clear();
        map.put("appid","7");
        map.put("method","importantnews2017.list");
        map.put("pageindex","1");
        map.put("pagesize","25");
//        map = Sign.setSign(map);
        String news_list_str = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject news_list_json = JSONObject.parseObject(news_list_str);
        JSONArray news_list_jsonarray= news_list_json.getJSONArray("Data");
        List<String> news_ids = new ArrayList<>();
        for (Object obj :news_list_jsonarray){
            JSONObject news = (JSONObject) obj;
            news_ids.add(news.getString("newsid"));
        }

        //分享咨询到朋友圈
        url = "http://msn.api.app.yiche.com/api.ashx";
        map.clear();
        map.put("method","credit.share");
        map.put("type","2");
        map.put("itemid",news_ids.get(new Random().nextInt(news_ids.size())));
        map.put("token",userToken);
        map.put("ver","9.0.1");
        map.put("time",Calendar.getInstance().getTimeInMillis()/1000+"");
        map = Sign.setSign(map);
        oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        //查看社区焦点图
        url = "http://msn.api.app.yiche.com/api.ashx";
        map.clear();
        map.put("method","credit.lookadimage");
        map.put("time",Calendar.getInstance().getTimeInMillis()/1000+"");
        map.put("token",userToken);
        map.put("ver","9.0.1");
        map = Sign.setSign(map);
        oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        //查询最后的积分并算出当天获得的积分
        url = "http://msn.api.app.yiche.com/api.ashx";
        map.clear();
        map.put("method","credit.all");
        map.put("token",userToken);
        map = Sign.setSign(map);
        String last_result = oneTLSPool2.oneWayAuthorizationAcceptedGet(map,url);
        JSONObject last_result_json = JSONObject.parseObject(last_result);
        int last_score = last_result_json.getJSONObject("Data").getInteger("all");
        user.setEarn(last_score-user.getScore());
        user.setScore(last_score);
        OperateOracle operateOracle = new OperateOracle();
        operateOracle.updateResult(user);
    }
}
