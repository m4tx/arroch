package models;

import javax.persistence.*;

@Entity
@Table(name = "group_types")
public class GroupType {
@Id
@GeneratedValue
@Column(name = "group_type_id")
    private int id;

@Column
    private String type;

    public int getId() {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
