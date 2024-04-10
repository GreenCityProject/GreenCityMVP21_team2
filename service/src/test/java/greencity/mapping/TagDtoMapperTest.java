package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagDtoMapperTest {
    private final TagDtoMapper mapper = new TagDtoMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var toConvert = ModelUtils.getTagTranslations().get(0);
        toConvert.setTag(Tag.builder().id(1L).build());

        var expected = getTagDto(toConvert);
        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullToConvertObject_throwNullPointer(TagTranslation tagTranslation){
        assertThatThrownBy(()-> mapper.convert(tagTranslation)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullName_isTrue(){
        var toConvert = ModelUtils.getTagTranslations().get(0);
        toConvert.setTag(Tag.builder().id(1L).build());

        toConvert.setName(null);

        var expected = getTagDto(toConvert);

        var actual = mapper.convert(toConvert);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void convert_withNullTag_throwNullPointer(){
        var toConvert = ModelUtils.getTagTranslations().get(0);
        toConvert.setTag(null);

        assertThatThrownBy(()-> mapper.convert(toConvert)).isInstanceOf(NullPointerException.class);
    }


    private TagDto getTagDto(TagTranslation toConvert) {
        return TagDto.builder()
                .id(toConvert.getTag().getId())
                .name(toConvert.getName())
                .build();
    }
}