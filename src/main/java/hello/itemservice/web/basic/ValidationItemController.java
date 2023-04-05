package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/items")
@RequiredArgsConstructor
public class ValidationItemController {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    // ! Spring이 제공하는 Validator 인터페이스를 사용해서 만든 Validator를 적용하는 방식은
    // ! 이렇게 특정 컨트롤러에서 InitBinder를 만들어주면 된다. 그럼 이 컨트롤러 안에서는 어떤 Mapping이 호출되든 이 녀석이 먼저 실행되서
    // ! validate할 준비를 한다.
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute(new Item());
        return "validation/addForm";
    }

    //@PostMapping("/add")
    public String save(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam int quantity,
                       Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item", item);

        return "validation/item";
    }

    //@PostMapping("/add")
    public String saveV1(@ModelAttribute Item item, Model model) {

        itemRepository.save(item);
        model.addAttribute("item", item);
        return "validation/item";
    }

    //@PostMapping("/add")
    public String saveV2(@ModelAttribute("item") Item item) {

        itemRepository.save(item);
        return "validation/item";
    }

    // ! BindingResult는 무조건 ModelAttribute 다음에 파라미터로 받아야한다.
    // ! BindingResult는 어떤 에러가 있으면, 사용하고 있는 모델의 정보를 받고 그 모델의 필드를 받아서 에러 메시지를 담아준다.
    // ! BindingResult는 타입을 잘못 입력해서 넣어도 (int값을 String으로 넣는것과 같은) 그것을 에러로 처리해준다.
    //@PostMapping("/add")
    public String saveV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // ! 툭정 필드가 아니라 이렇게 글로벌한 에러인 경우 ObjectError로 담으면 된다.
                bindingResult.addError(new ObjectError("item", "가격 * 수량은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

//    @PostMapping("/add")
    public String saveV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "itemName",
                            item.getItemName(),
                            false,
                            null,
                            null,
                            "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "price",
                            item.getPrice(),
                            false,
                            null,
                            null,
                            "가격은 1,000 ~ 1,000,000까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "quantity",
                            item.getQuantity(),
                            false,
                            null,
                            null,
                            "수량은 최대 9,999까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // ! 툭정 필드가 아니라 이렇게 글로벌한 에러인 경우 ObjectError로 담으면 된다.
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "itemName",
                            item.getItemName(),
                            false,
                            new String[]{"required.item.itemName"},
                            null,
                            null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "price",
                            item.getPrice(),
                            false,
                            new String[]{"range.item.price"},
                            new Object[]{1000, 1000000},
                            null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "quantity",
                            item.getQuantity(),
                            false,
                            new String[]{"max.item.quantity"},
                            new Object[]{9999},
                            null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // ! 툭정 필드가 아니라 이렇게 글로벌한 에러인 경우 ObjectError로 담으면 된다.
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV6(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // ! BindingResult는, 자기보다 바로 앞에 어떤 녀석을 검증할건지를 반드시 강제하기 때문에 사실 BindingResult는 이미 본인이 누굴 검증할지 알고 있는 상태다.
        // ! 그래서 objectName 이런걸 넣을 필요가 없고 아래처럼 깔끔하게 줄일 수 있다.
        // ! 근데 errorCode를 앞부분만 딱 입력했는데 어떻게 저 메시지를 가져올 수 있는가 ? 이건 MessageCodesResolver에 연관이 있다.
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
//            bindingResult.addError(
//                    new FieldError(
//                            "item",
//                            "itemName",
//                            item.getItemName(),
//                            false,
//                            new String[]{"required.item.itemName"},
//                            null,
//                            null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV7(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV8(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // ! 이렇게 @Validated를 파라미터로 받고 그 다음에 검증할 오브젝트를 받으면,
        // ! 바인딩한 밸리데이터가 여러개 있을 땐 support를 통해서 현재 이 오브젝트를 처리할 수 있는 밸리데이터가 누구인지 찾고
        // ! 찾으면 그 밸리데이터의 validate을 호출한다.
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV456(Item item) {
        itemRepository.save(item);
        return "validation/item";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        itemRepository.update(itemId, item);
        redirectAttributes.addAttribute("itemId", itemId);
        return "redirect:/validation/items/{itemId}";
    }


    //@PostConstruct
    public void init() {
        log.info("is reach?");
        itemRepository.save(new Item("item1", 100, 10));
        itemRepository.save(new Item("item2", 2000, 45));
    }
}
