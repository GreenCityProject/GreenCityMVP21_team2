package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;

@ExtendWith(MockitoExtension.class)
public class MultipartBase64ImageMapperTest {
    private final MultipartBase64ImageMapper mapper = new MultipartBase64ImageMapper();

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"mainFile,tempImage.jpg"})
    public void convert_withCorrectData_isTrue(String fileName, String ordinalFileName){
        File image = uploadImage();
        String base64Image = encodeWithBase64(image);

        var file = mapper.convert(base64Image);

        assertThat(file.getName()).isEqualTo(fileName);
        assertThat(file.getContentType()).isEqualTo(IMAGE_JPEG_VALUE);
        assertThat(file.getOriginalFilename()).isEqualTo(ordinalFileName);

        removeImageAfterTest(ordinalFileName);
    }

    @ParameterizedTest
    @NullSource
    public void convert_withNullImageString_throwNullPointer(String image){
        assertThatThrownBy(()->mapper.convert(image))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @CsvSource({"Incorrect image"})
    public void convert_withIncorrectImageString_throwNullPointer(String image){
        assertThatThrownBy(()->mapper.convert(image))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @SneakyThrows
    @Test
    public void convert_withThrownInMethodIOException_throwNotSavedException(){
        File image = uploadImage();
        String base64Image = encodeWithBase64(image);

        var mockedImageIO = mockStatic(ImageIO.class);
        mockedImageIO.when(()-> ImageIO.read((InputStream) any())).thenThrow(IOException.class);

        assertThatThrownBy(()->mapper.convert(base64Image))
                .isInstanceOf(NotSavedException.class)
                .hasMessage("Cannot convert to BASE64 image");

        mockedImageIO.close();
    }

    private String encodeWithBase64(File image) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
    }

    private File uploadImage() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:test.jpg");
    }

    private void removeImageAfterTest(String fileName) throws IOException {
        Files.deleteIfExists(Path.of(fileName));
    }
}