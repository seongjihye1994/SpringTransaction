package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}