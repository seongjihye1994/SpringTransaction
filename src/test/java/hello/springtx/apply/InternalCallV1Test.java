package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
        // InternalCallV1Test$CallService$$EnhancerBySpringCGLIB$$21fbb614
    }

    // 테스트 1
    @Test
    void internalCall() {
        callService.internal();
    }

    // 테스트 2
    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {

        public void external() {
            log.info("call external");
            printTxInfo();
            internal(); // 여기서 문제 발생!
            // 이 internal은 트랜잭션 프록시 객체에서 호출한 것이 아닌, 자기 자신(this)이 호출 한 것이므로
            // 트랜잭션을 적용할 수 있는 프록시 객체가 아니라서, 트랜잭션을 호출할 수 없다.
        }

        @Transactional // Getting transaction for ~, Completing transaction for ~
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active : {}", txActive);
        }
    }


}
