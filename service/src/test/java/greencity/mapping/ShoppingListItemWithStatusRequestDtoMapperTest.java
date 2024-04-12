package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static greencity.enums.ShoppingListItemStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShoppingListItemWithStatusRequestDtoMapperTest {
    private final ShoppingListItemWithStatusRequestDtoMapper mapper
            = new ShoppingListItemWithStatusRequestDtoMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var toConvert = new ShoppingListItemWithStatusRequestDto(ACTIVE);
        var expected = getUserShoppingListItem(toConvert);

        var actual = mapper.convert(toConvert);
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNulltoConvertObject_throwNullPointer(ShoppingListItemWithStatusRequestDto dto){
        assertThatThrownBy(() -> mapper.convert(dto)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullIdAndStatus_isTrue(){
        var toConvert = new ShoppingListItemWithStatusRequestDto(null);
        toConvert.setId(null);

        var expected = getUserShoppingListItem(toConvert);

        var actual = mapper.convert(toConvert);
        assertThat(actual).isEqualTo(expected);
    }

    private UserShoppingListItem getUserShoppingListItem(ShoppingListItemWithStatusRequestDto dto){
        var userShoppingListItem = new UserShoppingListItem();
        userShoppingListItem.setShoppingListItem(getShoppingListItem(dto));
        userShoppingListItem.setStatus(dto.getStatus());

        return userShoppingListItem;
    }

    private static ShoppingListItem getShoppingListItem(ShoppingListItemWithStatusRequestDto dto) {
        return ShoppingListItem.builder()
                .id(dto.getId())
                .build();
    }
}