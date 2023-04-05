package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
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
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemController2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute(new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String save(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // ! 바인딩을 사용하지 않고도 그냥 밸리데이션을 사용할 수 있는데 그게 바로 빈 밸리데이션이다.
        // ! 그리고 결국 그 Bean Validation도 이전에 배운 BindingResult를 내부적으로 사용해서 에러 코드를 생성하고
        // ! 그 코드가 있으면 우선순위가 높은 순으로 에러 메시지를 FieldError에 담는 방식으로 구현된다.

        // ! 근데 ObjectError는 Bean Validation으로 처리하기는 좀 애매하기 때문에 이렇게 그냥 자바 코드로 처리하는게 제일 깔끔하다.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String saveV2(
            @Validated(value = SaveCheck.class) @ModelAttribute Item item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // ! 바인딩을 사용하지 않고도 그냥 밸리데이션을 사용할 수 있는데 그게 바로 빈 밸리데이션이다.
        // ! 그리고 결국 그 Bean Validation도 이전에 배운 BindingResult를 내부적으로 사용해서 에러 코드를 생성하고
        // ! 그 코드가 있으면 우선순위가 높은 순으로 에러 메시지를 FieldError에 담는 방식으로 구현된다.

        // ! 근데 ObjectError는 Bean Validation으로 처리하기는 좀 애매하기 때문에 이렇게 그냥 자바 코드로 처리하는게 제일 깔끔하다.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/validation/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    //@PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        redirectAttributes.addAttribute("itemId", itemId);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(value = UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors ={}", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        redirectAttributes.addAttribute("itemId", itemId);
        return "redirect:/validation/v2/items/{itemId}";
    }


    @PostConstruct
    public void init() {
        itemRepository.save(new Item("item1", 100, 10));
        itemRepository.save(new Item("item2", 2000, 45));
    }
}
