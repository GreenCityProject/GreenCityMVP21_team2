package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShoppingListItemDtoMapperTest {
    private final ShoppingListItemDtoMapper mapper = new ShoppingListItemDtoMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslationObject();

        var expected = getShoppingListItemDto(shoppingListItemTranslation);
        var actual = mapper.convert(shoppingListItemTranslation);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullObject_throwNullPointer(ShoppingListItemTranslation translation){
        assertThatThrownBy(()-> mapper.convert(translation)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullShoppingListItemField_throwNullPointer(){
        var shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslationObject();
        shoppingListItemTranslation.setShoppingListItem(null);

        assertThatThrownBy(()-> mapper.convert(shoppingListItemTranslation)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullSomePrimitiveWrapperFields_isTrue(){
        var shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslationObject();
        shoppingListItemTranslation.setContent(null);
        shoppingListItemTranslation.getShoppingListItem().setId(null);

        var expected = getShoppingListItemDto(shoppingListItemTranslation);
        var actual = mapper.convert(shoppingListItemTranslation);

        assertThat(actual).isEqualTo(expected);
    }

    private ShoppingListItemDto getShoppingListItemDto(ShoppingListItemTranslation shoppingListItemTranslation) {
        return ShoppingListItemDto.builder()
                .id(shoppingListItemTranslation.getShoppingListItem().getId())
                .text(shoppingListItemTranslation.getContent())
                .status(ShoppingListItemStatus.ACTIVE.toString())
                .build();
    }
}