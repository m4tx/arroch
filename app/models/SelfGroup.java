package models;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@DiscriminatorValue("selfGroup")
public class SelfGroup extends Group {
    @OneToOne(mappedBy = "selfGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Person member;

    @Override
    public List<Person> getMembers() {
        return Collections.singletonList(member);
    }

    public Person getMember() {
        return member;
    }

    public void setMember(Person member) {
        this.member = member;
    }
}
