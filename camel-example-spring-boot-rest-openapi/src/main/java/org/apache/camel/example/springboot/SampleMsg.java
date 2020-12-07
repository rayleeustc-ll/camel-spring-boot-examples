package org.apache.camel.example.springboot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SampleMsg {
    Integer order;
    String customer;
    Integer customerNo;
    ArrayList<Item> items;

}
