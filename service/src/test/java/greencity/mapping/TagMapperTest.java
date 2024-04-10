package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagMapperTest {
    private final TagMapper mapper = new TagMapper();

    @Test
    public void convert_withCorrectData_isTure(){
        var toConvert = ModelUtils.getTagVO();
        var expected = getTag(toConvert);

        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullToConvertObject_throwNullPointer(TagVO tagVO){
        assertThatThrownBy(()-> mapper.convert(tagVO)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullIdAndType_isTure(){
        var toConvert = ModelUtils.getTagVO();
        toConvert.setId(null);
        toConvert.setType(null);

        var expected = getTag(toConvert);
        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void convert_withNullTagTranslationInList_throwNullPointer(){
        var toConvert = ModelUtils.getTagVO();
        toConvert.getTagTranslations().set(0,null);

        assertThatThrownBy(()-> mapper.convert(toConvert)).isInstanceOf(NullPointerException.class);
    }


    @Test
    public void convert_withEmptyULLTagTranslationInList_throwNullPointer(){
        var toConvert = ModelUtils.getTagVO();
        toConvert.setTagTranslations(new ArrayList<>());

        var expected = getTag(toConvert);
        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }



    private Tag getTag(TagVO tagVO) {
        return Tag.builder()
                .id(tagVO.getId())
                .type(tagVO.getType())
                .tagTranslations(getTranslations(tagVO.getTagTranslations()))
                .build();
    }

    private List<TagTranslation> getTranslations(List<TagTranslationVO> tagTranslations) {
        return tagTranslations.stream()
                .map(this::buildTagTranslation)
                .collect(Collectors.toList());
    }

    private TagTranslation buildTagTranslation(TagTranslationVO t) {
        return TagTranslation.builder()
                .id(t.getId())
                .name(t.getName())
                .language(buildLanguage(t))
                .build();
    }

    private Language buildLanguage(TagTranslationVO t) {
        return Language.builder().id(t.getLanguageVO().getId()).code(t.getLanguageVO().getCode()).build();
    }
}