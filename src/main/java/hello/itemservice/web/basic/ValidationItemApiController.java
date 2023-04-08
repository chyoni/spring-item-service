package hello.itemservice.web.basic;

import hello.itemservice.web.basic.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    /**
     * 검증의 경우 3가지 케이스가 있다.
     * 1. 검증에 성공해서 로직이 잘 처리된 경우
     * 2. JSON으로 받은 데이터를 객체로 받아야 하는데 JSON으로 받은 데이터를 객체로 변환조차 못한 경우 -> 예를 들면, price라는 Integer 타입에
     * 데이터를 스트링으로 던지면 객체로 만들지 조차 못하고 이러면 컨트롤러 호출도 안되서 바로 에러가 발생 이 경우 @ModelAttribute랑 @RequestBody 차이가 있는데
     * ModelAttribute 같은 경우는 파라미터(queryParameter, request body)로 받은 데이터를 getParamter로 하나하나 가져와서 필드에 담기 때문에 컨트롤러 자체는 호출이 되고 각
     * 필드에서 문제가 생기면 그 필드에 bindingResult가 에러를 담기 때문에 컨트롤러는 호출이 된다만, RequestBody는 객체 단위로 데이터를 받아서 하나의 객체를 만들기 때문에 객체 자체가 형성이 될 수
     * 없으면 컨트롤러를 호출하지도 못하고 에러가 나는 차이가 있다.
     * 3. 데이터를 객체로 변환까지는 했지만 변환된 객체의 데이터에서 검증 오류에 걸린 경우
     * */
    @PostMapping("/add")
    public Object addItem(@Validated @RequestBody ItemSaveForm form, BindingResult bindingResult) {

        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()) {
            log.error("검증 오류 발생 error = {}", bindingResult.getAllErrors());
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
