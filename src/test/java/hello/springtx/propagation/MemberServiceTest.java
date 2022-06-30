package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     *
     */
    @Test
    void outerTxOff_success() {

        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON EXCEPTION
     *
     */
    @Test
    void outerTxOff_fail() {

        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then log 데이터는 롤백된다 -> 데이터 정합성이 맞지 않음.
        assertTrue(memberRepository.find(username).isPresent()); // 멤버는 저장이 완료
        assertTrue(logRepository.find(username).isEmpty()); // 로그는 저장 중간에 예외가 터져 저장이 되지 않았기 때문에 empty가 정상이다.
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository    @Transactional:OFF
     *
     */
    @Test
    void singleTx() {

        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     *
     */
    @Test
    void outerTxOn_success() {

        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON EXCEPTION
     *
     */
    @Test
    void outerTxOn_fail() {

        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty()); // 멤버는 저장이 안됨. (하나의 논리 롤백은 최종 물리 롤백으로 이어진다)
        assertTrue(logRepository.find(username).isEmpty()); // 로그는 저장 중간에 예외가 터져 저장이 되지 않았기 때문에 empty가 정상이다.
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON EXCEPTION
     *
     */
    @Test
    void recoverException_fail() {

        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty()); // 서비스에서 예외를 잡았으니 멤버는 저장이 됐겠지? -> NO! 멤버 역시 empty이다.
        assertTrue(logRepository.find(username).isEmpty()); // 로그는 저장 중간에 예외가 터져 저장이 되지 않았기 때문에 empty가 정상이다.

        // 왜 서비스 계층에서 익셉션을 잡았는데도 멤버가 empty일까?
        // 신규 트랜잭션이 아닌 내부 트랜잭션이기 때문에 set rollback only를 설정하고
        // 서비스 계층에서 예외를 잡았지만, set rollback only가 설정된 것을 확인하고
        // UnExpectedRollbackException 예외로 바꿔서 던진다. -> 결국 롤백된다.
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON(REQUIRES_NEW) EXCEPTION
     *
     */
    @Test
    void recoverException_success() {

        // given
        String username = "로그예외_recoverException_success";

        // when
        memberService.joinV2(username);

        // then log 저장은 실패하지만, member 저장은 성공한다.
        assertTrue(memberRepository.find(username).isPresent()); // 서비스에서 예외를 잡았으니 멤버는 저장이 됐겠지? -> YES!
        assertTrue(logRepository.find(username).isEmpty()); // 로그는 저장 중간에 예외가 터져 저장이 되지 않았기 때문에 empty가 정상이다.

        // REQUIRES_NEW 옵션을 통해 회원가입 시도 로그를 남기는데 실패해도 (내부 트랜잭션 롤백)
        // 회원가입은 유지된다. (외부 트랜잭션에 영향 x)

    }

}