package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShoppingListItemResponseDtoMapperTest {
    private final ShoppingListItemResponseDtoMapper mapper = new ShoppingListItemResponseDtoMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var shoppingListItem = ModelUtils.getShoppingListItem();
        var translations = shoppingListItem.getTranslations();

        var actual = mapper.convert(shoppingListItem);
        var actualTranslations = actual.getTranslations();

        assertThat(actual.getId()).isEqualTo(shoppingListItem.getId());
        assertThat(isTranslationsConvertedCorrectly(actualTranslations,translations)).isTrue();
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNulltoConvertObject_throwNullPointer(ShoppingListItem item){
        assertThatThrownBy(() -> mapper.convert(item)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withEmptyTranslations_isTrue(){
        var shoppingListItem = ModelUtils.getShoppingListItem();
        shoppingListItem.setTranslations(new ArrayList<>());
        var translations = shoppingListItem.getTranslations();

        var actual = mapper.convert(shoppingListItem);
        var actualTranslations = actual.getTranslations();

        assertThat(actual.getId()).isEqualTo(shoppingListItem.getId());
        assertThat(isTranslationsConvertedCorrectly(actualTranslations,translations)).isTrue();
    }

    @Test
    public void convert_withNullId_isTrue(){
        var shoppingListItem = ModelUtils.getShoppingListItem();
        shoppingListItem.setId(null);
        var translations = shoppingListItem.getTranslations();

        var actual = mapper.convert(shoppingListItem);
        var actualTranslations = actual.getTranslations();

        assertThat(actual.getId()).isEqualTo(shoppingListItem.getId());
        assertThat(isTranslationsConvertedCorrectly(actualTranslations,translations)).isTrue();
    }

    @Test
    public void convert_withNullTranslationInList_isTrue(){
        var shoppingListItem = ModelUtils.getShoppingListItem();
        shoppingListItem.getTranslations().set(0,null);

        assertThatThrownBy(() -> mapper.convert(shoppingListItem)).isInstanceOf(NullPointerException.class);
    }


    private boolean isTranslationsConvertedCorrectly(List<ShoppingListItemTranslationDTO> translationDTOS,
                                                     List<ShoppingListItemTranslation> translations){
        return IntStream.iterate(0, i -> i+1)
                .limit(translationDTOS.size())
                .allMatch((i) -> dataMatch(translations.get(i), translationDTOS.get(i)));
    }

    private boolean dataMatch(ShoppingListItemTranslation translation, ShoppingListItemTranslationDTO translationDTO) {
        return translation.getContent().equals(translationDTO.getContent()) &&
                translation.getId().equals(translationDTO.getId());
    }
}