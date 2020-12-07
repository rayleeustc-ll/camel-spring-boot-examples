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
public class SupUser extends User  {
    String tag;
    String level;
    ArrayList<Skills> SkillList = new ArrayList<>();

    {
        Skills s1 = new Skills("java",7);
        Skills s2 = new Skills("python",5);
        SkillList.add(s1);
        SkillList.add(s1);
    }

    SupUser(Integer id, String name, String tag, String level) {
        super(id,name);
        this.tag=tag;
        this.level= level;
    }

}
