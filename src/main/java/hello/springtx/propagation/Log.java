package hello.springtx.propagation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * DB에 남기는 로그
 */

@Entity
@Getter
@Setter
public class Log {

    @Id @GeneratedValue
    private Long id;
    private String message;

    public Log() {

    }

    public Log(String message) {
        this.message = message;
    }
}
