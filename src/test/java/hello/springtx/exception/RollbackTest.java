package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService service;

    // 언체크 예외(RuntimeException)는 롤백되어야 한다.
    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    // 체크 예외(Exception)는 커밋되어야 한다.
    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(MyException.class);
    }

    // 체크 예외(Exception)는 커밋되어야 한다.
    // 하지만, rollbackFor 메소드에는 @Transactional(rollbackFor = MyException.class)
    // 옵션이 지정되어 있기 때문에 롤백된다.
    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(MyException.class);
    }



    @TestConfiguration // 테스트시 스프링 빈으로 등록할 설정 파일
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() { // RollbackService 객체를 스프링 빈으로 등록
            return new RollbackService();
        }
    }


    @Slf4j
    static class RollbackService {

        // 런타입 예외 발생 : 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생 : 커밋
        @Transactional
        public void checkedException() throws MyException { // 예외 던짐
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 지정 : 롤백
        @Transactional(rollbackFor = MyException.class) // 어떤 예외가 발생할 때 롤백할 지 지정 -> MyException 이 발생하면 롤백함!
        public void rollbackFor() throws MyException { // 예외 던짐
            log.info("call rollbackFor");
            throw new MyException();
        }
    }

    static class MyException extends Exception {
    }

}
