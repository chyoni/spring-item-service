package hello.itemservice.web.basic.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemSaveForm {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000, message = "가격은 1,000원에서 1,000,000원 사이여야 합니다.")
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
