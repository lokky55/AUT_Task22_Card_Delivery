import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.Keys.DELETE;

public class CardDeliveryTest {

    @BeforeEach
    void openAndHoldBrowser() {
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = true;
    }

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    String planningDate = generateDate(5);  // 3 дня - мин. срок с актуальной даты (при days < 3, тесты падают)

    @Test
    void validShouldRegister() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id='date'] .input__control").sendKeys(DELETE);
        $("[data-test-id='date'] .input__control").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='notification'] [class='notification__title']").shouldHave(text("Успешно"));
    }

    @Test
    void validShouldRegister_CheckMessageAndDate() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id='date'] .input__control").sendKeys(DELETE);
        $("[data-test-id='date'] .input__control").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    void validShouldSelectCityFromPopupWindow() {
        $("[data-test-id='city'] input").setValue("Ар");
        $$(".popup .popup__content .menu-item").find(matchText("Архангельск")).click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id='date'] .input__control").sendKeys(DELETE);
        $("[data-test-id='date'] .input__control").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='notification'] [class='notification__title']").shouldHave(text("Успешно"));
    }

    @Test
    void invalidShouldBeErrorOfIncorrectCity() {
        $("[data-test-id='city'] input").setValue("Moscow");
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='city'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Доставка в выбранный город недоступна", text);
    }

    @Test
    void invalidShouldBeErrorOfEmptyCityField() {
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='city'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Поле обязательно для заполнения", text);
    }

    @Test
    void invalidShouldBeErrorOfIncorrectName() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='name'] input").setValue("Generalov Alexander");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='name'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы " +
                "и дефисы.", text);
    }

    @Test
    void invalidShouldBeErrorOfEmptyNameField() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='name'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Поле обязательно для заполнения", text);
    }

    @Test
    void invalidShouldBeErrorOfIncorrectPhone() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("89807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='phone'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", text);
    }

    @Test
    void invalidShouldBeErrorOfEmptyPhoneFiled() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='phone'].input_invalid .input__sub").getText();
        Assertions.assertEquals("Поле обязательно для заполнения", text);
    }

    @Test
    void invalidShouldBeErrorOfAgreementAbsent() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='agreement'].input_invalid .checkbox__text").getText();
        Assertions.assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных " +
                "данных", text);
    }

    @Test
    void invalidShouldBeErrorOfEmptyDateFiled() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id='date'] .input__control").sendKeys(DELETE);
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='date'] .input_invalid .input__sub").getText();
        Assertions.assertEquals("Неверно введена дата", text);
    }

    @Test
    void invalidShouldBeErrorOfIncorrectDate() {
        $("[data-test-id='city'] input").setValue("Анадырь");
        $("[data-test-id='name'] input").setValue("Мари-Анет Радонежская");
        $("[data-test-id='phone'] input").setValue("+79807133080");
        $("[data-test-id='agreement']").click();
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id='date'] .input__control").sendKeys(DELETE);
        $("[data-test-id='date'] .input__control").setValue(generateDate(1));
        $$("button").find(exactText("Забронировать")).click();
        String text = $("[data-test-id='date'] .input_invalid .input__sub").getText();
        Assertions.assertEquals("Заказ на выбранную дату невозможен", text);
    }
}
