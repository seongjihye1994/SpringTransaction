package hello.springtx.propagation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    // JPA 스팩상 디폴트 생성자는 필수
    public Member() {

    }

    // id 는 DB가 생성해주니 생성자에서 생성 할 필요 x (@GeneratedValue)
    public Member(String username) {
        this.username = username;
    }
}
