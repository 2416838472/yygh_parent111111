package com.atguigu.yygh.cmn;


import com.atguigu.yygh.hosp.testmongo.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/mongo1")
public class TestMongo1 {

    @Autowired
    private MongoTemplate mongoTemplate;

    //添加
    @RequestMapping("/add")
    public void add() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setEmail("2416838472@qq,com");
        user.setCreateDate("2021-09-01");
        User insert = mongoTemplate.insert(user);
        System.out.println(insert);
    }

    //查询所有
    @RequestMapping("/findAll")
    public void findAll() {
        List<User> all = mongoTemplate.findAll(User.class);
        System.out.println(all);
    }

    //根据id查询
    @RequestMapping("/findById")
    public void getById() {
        User byId = mongoTemplate.findById("6157b1b2b8d1b8b2c8e1b1b1", User.class);
        System.out.println(byId);
    }

    //条件查询
    @RequestMapping("/findUser")
    public void findUser() {
        Query  query = new Query();
        Criteria.where("name").is("张三").and("age")
                .is(18);
        List<User> all = mongoTemplate.find(query, User.class);
        System.out.println(all);
    }

   //模糊查询
   @GetMapping("/findLike")
    public void findLike(){
        String name = "est";
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("name").regex(pattern);
        query.addCriteria(criteria);
        List<User> all = mongoTemplate.find(query, User.class);
        System.out.println(all);
    }


    //分页查询
    @RequestMapping("/findPage")
    public void findPage() {
        Query query = new Query();
        query.skip(0).limit(2);
        List<User> all = mongoTemplate.find(query, User.class);
        System.out.println(all);
    }

    //修改
    @RequestMapping("/update")
    public void updateUser(){
        User byId = mongoTemplate.findById("6157b1b2b8d1b8b2c8e1b1b1", User.class);
        byId.setName("李四");
        mongoTemplate.save(byId);
        User user = new User();
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set("name",user.getName());
        update.set("age",user.getAge());
        update.set("email",user.getEmail());
        update.set("createDate",user.getCreateDate());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
        System.out.println(updateResult);
    }

    //删除
    @RequestMapping("/delete")
    public void deleteUser(){
        User byId = mongoTemplate.findById("6157b1b2b8d1b8b2c8e1b1b1", User.class);
        mongoTemplate.remove(byId);
    }
}
