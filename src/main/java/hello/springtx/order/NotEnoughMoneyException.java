package hello.springtx.order;

// 주문 -> 결제 -> 고객 통장 잔고 부족 => 비즈니스 예외
public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
