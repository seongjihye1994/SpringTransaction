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

        // then 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent()); // 멤버는 저장이 완료
        assertTrue(logRepository.find(username).isEmpty()); // 로그는 저장 중간에 예외가 터져 저장이 되지 않았기 때문에 empty가 정상이다.
    }

}