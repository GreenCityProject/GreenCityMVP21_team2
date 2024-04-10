package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {
    private static final String habitControllerLink = "/habit";
    private static final Boolean isCustomHabit = true;
    private final UserVO userVO = new UserVO();
    private final List<String> tags = Collections.singletonList("str");
    private final List<Integer> complexities = List.of(1);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long id = 1L;
    private MockMvc mockMvc;
    @Mock
    private HabitService habitService;
    @Mock
    private TagsService tagsService;
    @InjectMocks
    private HabitController habitController;
    private Locale locale;
    private Pageable pageable;

    PageableDto<HabitDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);

    @BeforeEach
    void setUp() {
        this.locale = new Locale("en");
        this.pageable = PageRequest.of(1, 1);
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getHabitById_HabitController_shouldReturnStatusIsOk() throws Exception{
        mockMvc.perform(get(habitControllerLink + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locale)))
                .andExpect(status().isOk());
        verify(habitService).getByIdAndLanguageCode(eq(id), eq(locale.getLanguage()));
    }

    @Test
    void getAll_HabitController_shouldReturnIsOkStatus() throws Exception {
        when(habitService.getAllHabitsByLanguageCode(eq(userVO), eq(pageable), eq(locale.getLanguage())))
                .thenReturn(pageableDto);
        mockMvc.perform(get(habitControllerLink)
                        .param("page", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitService).getAllHabitsByLanguageCode(eq(userVO), eq(pageable), eq(locale.getLanguage()));
    }

    @Test
    void getShoppingListItems_HabitController_shouldReturnIsOkStatus() throws Exception{
        when(habitService.getShoppingListForHabit(eq(id), eq(locale.getLanguage())))
                .thenReturn(List.of(new ShoppingListItemDto(1L, "1", "0")));
        mockMvc.perform(get(habitControllerLink + "/{id}/shopping-list", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locale)))
                .andExpect(status().isOk());
        verify(habitService).getShoppingListForHabit(eq(id), eq(locale.getLanguage()));
    }

    @Test
    void getAllByTagsAndLanguageCode_HabitController_shouldReturnIsOkStatus() throws Exception{
        mockMvc.perform(get(habitControllerLink + "/tags/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "1")
                        .param("tags", "str")
                        .content(locale.getLanguage()))
                .andExpect(status().isOk());
        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    void getAllByDifferentParameters_HabitController_validParams_shouldReturnIsOkStatus() throws Exception{
        when(habitService.getAllByDifferentParameters(userVO, pageable, Optional.of(tags),
                        Optional.of(isCustomHabit), Optional.of(complexities), locale.getLanguage()))
                .thenReturn(pageableDto);
        mockMvc.perform(get(habitControllerLink + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tags", "str")
                        .param("isCustomHabit", isCustomHabit.toString())
                        .param("complexities", complexities.get(0).toString())
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());
        verify(habitService).getAllByDifferentParameters(userVO, pageable, Optional.of(tags),
                Optional.of(isCustomHabit), Optional.of(complexities), locale.getLanguage());
    }

    @Test
    void getAllByDifferentParameters_HabitController_invalidParams_shouldThrowBadRequestException() {
        assertThrows(Exception.class, () -> mockMvc.perform(get(habitControllerLink + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertEquals("You should enter at least one parameter",
                        Objects.requireNonNull(result.getResolvedException()).getMessage())));
    }

    @Test
    void findAllHabitsTags_HabitController_shouldReturnStatusIsOk() throws Exception{
        List<String> strings = new ArrayList<>();
        when(tagsService.findAllHabitsTags(eq(locale.getLanguage())))
                .thenReturn(strings);
        mockMvc.perform(get(habitControllerLink + "/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locale)))
                .andExpect(status().isOk());
        verify(tagsService).findAllHabitsTags(eq(locale.getLanguage()));
    }

    //author: Ssn0b (GitHub), modifications: renamed method, variable (habitControllerLink)
    @Test
    void addCustomHabit_HabitController_shouldReturnStatusCreated() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
        String json = """
                        {
                        "complexity": 1,
                         "defaultDuration": 1,
                         "habitTranslations": null,
                        "customShoppingListItemDto": null,
                         "image": null,
                         "tagIds": null
                         }
                """;
        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(STR."\{habitControllerLink}/custom")
                        .file(jsonFile)
                        .principal(principal))
                .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = mapper.readValue(json, AddCustomHabitDtoRequest.class);

        verify(habitService)
                .addCustomHabit(eq(addCustomHabitDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"));
    }

    @Test
    void getFriendsAssignedToHabitProfilePictures_HabitController_shouldReturnStatusIsOk() throws Exception{
        mockMvc.perform(get(habitControllerLink + "/{habitId}/friends/profile-pictures", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk());
        verify(habitService).getFriendsAssignedToHabitProfilePictures(id, null);
    }
}
