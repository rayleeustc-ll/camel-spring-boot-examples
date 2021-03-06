package org.apache.camel.example.springboot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResultMsg {
    Integer order;
    String customer;
    Integer customerNo;
    Item item;
}
