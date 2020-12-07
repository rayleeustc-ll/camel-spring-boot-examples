package org.apache.camel.example.springboot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MyRouter extends RouteBuilder {


    @Autowired
    UserProcessor processor;

    @Autowired
    UserService userService;

   @Override
    public void configure() throws Exception {



       rest().post("/addUser").consumes(MediaType.APPLICATION_JSON_VALUE).type(User.class).outType(User.class)
               .route().process(processor).endRest();
       rest().get("/getUserToMQ/{id}").produces(MediaType.APPLICATION_JSON_VALUE).route().process(exchange -> {
           Integer id = Integer.parseInt((String) exchange.getIn().getHeader("id"));
           User user = userService.findUser(id);
        //   String jsonStr = JSONObject.toJSONString(user);
           SupUser spuser=null;
           if(null != user){
               spuser =new SupUser(user.getId(),user.getName(),"hero","top");
           }
           Object jobj = JSONObject.toJSON(spuser);
           exchange.getIn().setBody(jobj);
       }).marshal().json().log("the getUser successfully ${body}").to("activemq:PEOSONEQUEUE?exchangePattern=InOnly").endRest();

       /*
       test choice functionality
       */

       rest().get("/testChoice/{testID}").route()
               .choice()
               .when(header("testID").isEqualTo("hello")).log("hello world").setBody(constant("Welcome to java techie"))
               .when(header("testID").isEqualTo("welcome")).log("welcome world").setBody(constant("Welcome welcome again"))
               .when(header("testID").isEqualTo("demo")).log("demo world").setBody(constant("Welcome demo")).endRest();


       /*
        * homework practise Example1&&Example2
        * 1.sent orginal json file with rest get with http://localhost:8080/api/SendMessage to log and activemq SAMPLEQUEUE
        * 2.from activemq SAMPLEQUEUE recieve the original json, and process the json string, and use fastjson to parse to java object SampleMsg
        * and split the java object SampleMsg into new java object ResultMsg and add this ResultMsg into List and send to endpoint "direct:testSplit"
        * 3.from the "direct:testSplit" iterator the list collection contain the items by split()
        */
       rest().get("/SendMessage").consumes(MediaType.APPLICATION_JSON_VALUE).to("direct:a");
        from ("direct:a").setBody(constant("{\"items\":[{\"description\":\"java\",\"itemNo\":7},{\"description\":\"python\",\"itemNo\":2},{\"description\":\"js\",\"itemNo\":11}],\"order\":10101,\"customer\":\"lelewang\",\"customerNo\":11101}"))
                .marshal().json()
                .log("sent message body is ${body}")
                .to("activemq:SAMPLEQUEUE?exchangePattern=InOnly").endRest();


       ArrayList<ResultMsg> resList= new ArrayList<>();
       //test active mq recieve json message
       from("activemq:SAMPLEQUEUE").unmarshal().json().log("Recieved message ${body}").process(exchange -> {

           JSONObject userJson = JSONObject.parseObject((String)exchange.getIn().getBody());
           SampleMsg sampleMsg = JSON.toJavaObject(userJson, SampleMsg.class);
           System.out.println(sampleMsg);
           ArrayList<Item> items = sampleMsg.getItems();
           items.forEach(e ->{
               ResultMsg resultMsg= new ResultMsg();
               resultMsg.setCustomer(sampleMsg.getCustomer());
               resultMsg.setCustomerNo(sampleMsg.getCustomerNo());
               resultMsg.setOrder(sampleMsg.getOrder());
               resultMsg.setItem(e);
               resList.add(resultMsg);
               System.out.println(resultMsg);
           });
           exchange.getIn().setBody(resList);
        }
       ).to("direct:testSplit");

       from("direct:testSplit").split(body()).log("test split ${body}");
    }
}
