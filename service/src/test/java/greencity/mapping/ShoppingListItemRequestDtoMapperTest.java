package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShoppingListItemRequestDtoMapperTest {
    private final ShoppingListItemRequestDtoMapper mapper = new ShoppingListItemRequestDtoMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var shoppingListItemRequestDto =  new ShoppingListItemRequestDto(1L);

        var expected = buildUserShoppingListItem(shoppingListItemRequestDto);
        var actual = mapper.convert(shoppingListItemRequestDto);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullObject_throwNullPointer(ShoppingListItemRequestDto dto){
        assertThatThrownBy(()-> mapper.convert(dto)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullSomePrimitiveWrapperFields_isTrue(){
        var shoppingListItemRequestDto =  new ShoppingListItemRequestDto();
        shoppingListItemRequestDto.setId(null);

        var expected = buildUserShoppingListItem(shoppingListItemRequestDto);
        var actual = mapper.convert(shoppingListItemRequestDto);

        assertThat(actual).isEqualTo(expected);
    }


    private UserShoppingListItem buildUserShoppingListItem(ShoppingListItemRequestDto shoppingListItemRequestDto) {
        return UserShoppingListItem.builder()
                .shoppingListItem(buildShoppingListItem(shoppingListItemRequestDto))
                .status(ShoppingListItemStatus.ACTIVE)
                .build();
    }

    private ShoppingListItem buildShoppingListItem(ShoppingListItemRequestDto shoppingListItemRequestDto){
        return ShoppingListItem.builder()
                .id(shoppingListItemRequestDto.getId())
                .build();
    }

}