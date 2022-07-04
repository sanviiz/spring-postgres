package hitachi.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hitachi.controller.TutorialController;
import hitachi.entity.Tutorial;
import hitachi.repository.TutorialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TutorialController.class)
public class TutorialControllerTest {

  @MockBean
  private TutorialRepository tutorialRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldCreateTutorial() throws Exception {
    Tutorial tutorial = new Tutorial(
        1L, "Spring Boot @WebMvcTest", "Description", true
    );
    mockMvc.perform(post("/api/tutorials")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tutorial)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  public void shouldReturnTutorials() throws Exception {
    long id = 1L;
    Tutorial tutorial = new Tutorial(
        id, "Spring Boot @WebMvcTest", "Description", true
    );
    when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
    mockMvc.perform(get("/api/tutorials/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.title").value(tutorial.getTitle()))
        .andExpect(jsonPath("$.description").value(tutorial.getDescription()))
        .andExpect(jsonPath("$.published").value(tutorial.isPublished()))
        .andDo(print());
  }

  @Test
  public void shouldReturnNotFoundTutorial() throws Exception {
    long id = 1L;
    when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/tutorials/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void shouldReturnListOfTutorials() throws Exception {
    List<Tutorial> tutorials = new ArrayList<>(
        Arrays.asList(
            new Tutorial(1L, "Spring Boot @WebMvcTest 1", "Description 1", true),
            new Tutorial(2L, "Spring Boot @WebMvcTest 2", "Description 2", true),
            new Tutorial(3L, "Spring Boot @WebMvcTest 3", "Description 3", true)
        )
    );
    Page<Tutorial> pageTutorials = new PageImpl<>(tutorials);
    when(tutorialRepository.findAll(any(Pageable.class))).thenReturn(pageTutorials);
    mockMvc.perform(get("/api/tutorials"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tutorials.size()").value(tutorials.size()))
        .andDo(print());
  }

  @Test
  void shouldReturnListOfTutorialsWithFilter() throws Exception {
    List<Tutorial> tutorials = new ArrayList<>(
        Arrays.asList(
            new Tutorial(1, "Spring Boot @WebMvcTest", "Description 1", true),
            new Tutorial(3, "Spring Boot Web MVC", "Description 3", true)
        )
    );
    String title = "Boot";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    Page<Tutorial> pageTutorials = new PageImpl<>(tutorials);
    paramsMap.add("title", title);
    when(tutorialRepository.findByTitleContaining(any(String.class), any(Pageable.class)))
        .thenReturn(pageTutorials);
    mockMvc.perform(get("/api/tutorials").params(paramsMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tutorials.size()").value(tutorials.size()))
        .andDo(print());
  }

  @Test
  public void shouldReturnEmptyListWhenFilter() throws Exception {
    String title = "Hello";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("title", title);
    List<Tutorial> tutorials = Collections.emptyList();
    Page<Tutorial> pageTutorials = new PageImpl<>(tutorials);
    when(tutorialRepository.findByTitleContaining(any(String.class), any(Pageable.class)))
        .thenReturn(pageTutorials);
    mockMvc.perform(get("/api/tutorials").params(paramsMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tutorials.size()").value(0))
        .andDo(print());
  }

  @Test
  public void shouldUpdateTutorial() throws Exception {
    long id = 1L;
    Tutorial tutorial = new Tutorial(id, "Spring Boot @WebMvcTest", "Description", false);
    Tutorial updatedTutorial = new Tutorial(id, "Updated", "Updated", true);
    when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
    when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updatedTutorial);
    mockMvc.perform(put("/api/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatedTutorial)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(updatedTutorial.getTitle()))
        .andExpect(jsonPath("$.description").value(updatedTutorial.getDescription()))
        .andExpect(jsonPath("$.published").value(updatedTutorial.isPublished()))
        .andDo(print());
  }

  @Test
  public void shouldReturnNotFoundUpdateTutorial() throws Exception {
    long id = 1L;
    Tutorial updatedTutorial = new Tutorial(id, "Updated", "Updated", true);
    when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
    when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updatedTutorial);
    mockMvc.perform(put("/api/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatedTutorial)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void shouldDeleteTutorial() throws Exception {
    long id = 1L;
    doNothing().when(tutorialRepository).deleteById(id);
    mockMvc.perform(delete("/api/tutorials/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  public void shouldDeleteAllTutorials() throws Exception {
    doNothing().when(tutorialRepository).deleteAll();
    mockMvc.perform(delete("/api/tutorials"))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

}