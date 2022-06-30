package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    // 로그 저장
    @Transactional
    public void save(Log logMessage) {
        log.info("log 저장");
        em.persist(logMessage);

        // 로그 저장 시 예외 발생
        if (logMessage.getMessage().contains("로그예외")) {
            log.info("log 저장 시 예외 발생");
            throw new RuntimeException("예외 발생"); // 만약 예외가 발생하면 롤백된다.
        }
    }

    // 로그 조회
    public Optional<Log> find(String message) {
        return em.createQuery("select l from Log l where l.message = :message", Log.class)
                .setParameter("message", message)
                .getResultList().stream().findAny();
    }


}
