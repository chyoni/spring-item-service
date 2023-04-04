package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();
    
    @Test
    void messageCodesResolverObject() {
        // ! 결국 bindingResult.rejectValue() 이 녀석이 내부에서 이 아래 MessageCodesResolver를 실행하는 것.
        // ! 이렇게 실행하면 이 녀석이 code들을 만들어 주는데 그게 저 "required.item", "required" 이렇게 되는거고
        // ! 이제 이게 최종적으로 bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        // ! 저 녀석을 만들어주는데 저기 저 new String[]{} 여기에 저 code들을 담는거임. 메커니즘이 이렇게 되어 있음.
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
    }
}
