package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.List;

import static greencity.ModelUtils.getTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagVOMapperTest {
    private final TagVOMapper mapper = new TagVOMapper();

    @Test
    public void convert_withCorrectData_isTrue(){
        var toConvert = getTag();
        var expected = buldTagVO(toConvert);

        var actual = mapper.convert(toConvert);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullToConvertObject_throwNullPointer(Tag tag){
        assertThatThrownBy(()-> mapper.convert(tag)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convert_withNullIdAndType_isTure(){
        var toConvert = getTag();
        toConvert.setId(null);
        toConvert.setType(null);

        var expected = buldTagVO(toConvert);
        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void convert_withEmptyTagTranslationList_isTure(){
        var toConvert = getTag();
        toConvert.setTagTranslations(new ArrayList<>());

        var expected = buldTagVO(toConvert);
        var actual = mapper.convert(toConvert);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void convert_withNullTagTranslationInList_throwNullPointer(){
        var toConvert = getTag();
        toConvert.getTagTranslations().set(0,null);

        assertThatThrownBy(()-> mapper.convert(toConvert)).isInstanceOf(NullPointerException.class);
    }

    private TagVO buldTagVO(Tag tag) {
        return TagVO.builder()
                .id(tag.getId())
                .type(tag.getType())
                .tagTranslations(buildTranslationsVO(tag.getTagTranslations()))
                .build();
    }

    private List<TagTranslationVO> buildTranslationsVO(List<TagTranslation> tagTranslations) {
        return tagTranslations.stream()
                .map(this::builtTranslationVO)
                .toList();
    }

    private TagTranslationVO builtTranslationVO(TagTranslation translation) {
        return TagTranslationVO.builder()
                .id(translation.getId())
                .name(translation.getName())
                .languageVO(buildLanguageVO(translation.getLanguage()))
                .build();
    }

    private LanguageVO buildLanguageVO(Language language) {
        return LanguageVO.builder()
                .id(language.getId())
                .code(language.getCode())
                .build();
    }
}