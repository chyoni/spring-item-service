package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
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

        return "basic/item";
    }

    //@PostMapping("/add")
    public String saveV1(@ModelAttribute Item item, Model model) {

        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    //@PostMapping("/add")
    public String saveV2(@ModelAttribute("item") Item item) {

        itemRepository.save(item);
        return "basic/item";
    }

    @PostMapping("/add")
    public String saveV3(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // {}로 데이터를 넣은게 아니라면 queryParameter로 나머지 attribute가 들어간다.
        // ex) localhost:8080/basic/items/3?status=true
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    //@PostMapping("/add")
    public String saveV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        itemRepository.update(itemId, item);
        redirectAttributes.addAttribute("itemId", itemId);
        return "redirect:/basic/items/{itemId}";
    }


    @PostConstruct
    public void init() {
        itemRepository.save(new Item("item1", 100, 10));
        itemRepository.save(new Item("item2", 2000, 45));
    }
}
