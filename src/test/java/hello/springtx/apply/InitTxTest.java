package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@SpringBootTest
public class InitTxTest {

    @Autowired Hello hello;

    @Test
    void go() {
        // 초기화 코드(@PostConstruct)는 스프링이 초기화 시점에 호출한다.
        // 스프링 구동 시 스프링 빈으로 등록 된 Hello 객체 내부의
        // @PostConstruct 를 확인한 스프링은 구동 시 해당 메소드를 호출한다.

    }


    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello(); // Hello 객체 스프링 빈으로 등록
        }
    }


    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active : {}", isActive);
            // Hello init @PostConstruct tx active : false
            // 초기화 코드에 @Transactional 을 함께 사용하면, 트랜잭션 적용이 되지 않는다.
            // 왜? -> 초기화 코드(@PostConstruct)가 먼저 호출된 후 @Transactional 이 호출되기 때문
            // 그래서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다.
        }

        @EventListener(ApplicationReadyEvent.class) // 스프링 컨테이너(트랜잭션 AOP 포함)가 모~두 생성되고 난 후 호출함
        @Transactional
        public void initV2() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active : {}", isActive);

        }

    }

}
