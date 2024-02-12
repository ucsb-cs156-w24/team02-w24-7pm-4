package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase {

        @MockBean
        MenuItemReviewRepository menuItemReviewRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/menuitemreview/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_menuitemreviews() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview review1 = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("wesley@ucsb.edu")
                    .stars(5)
                    .dateReviewed(ldt1)
                    .comments("i likey likey")
                    .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");
                MenuItemReview review2 = MenuItemReview.builder()
                    .itemId(2)
                    .reviewerEmail("wesleyt@ucsb.edu")
                    .stars(3)
                    .dateReviewed(ldt2)
                    .comments("food MID af")
                    .build();
                
                ArrayList<MenuItemReview> expectedReviews = new ArrayList<>();
                expectedReviews.addAll(Arrays.asList(review1,review2));

                when(menuItemReviewRepository.findAll()).thenReturn(expectedReviews);

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/menuitemreview/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_review() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview review1 = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("sdfa")
                    .stars(5)
                    .dateReviewed(ldt1)
                    .comments("dfd")
                    .build();

                when(menuItemReviewRepository.save(eq(review1))).thenReturn(review1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/menuitemreview/post?itemId=1&reviewerEmail=sdfa&stars=5&comments=dfd&dateReviewed=2022-01-03T00:00:00")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).save(review1);
                String expectedJson = mapper.writeValueAsString(review1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/menuitemreview?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/menuitemreview?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview review = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("sdfa")
                    .stars(5)
                    .dateReviewed(ldt)
                    .comments("dfd")
                    .build();

                when(menuItemReviewRepository.findById(eq(7L))).thenReturn(Optional.of(review));

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(review);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(menuItemReviewRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("MenuItemReview with id 7 not found", json.get("message"));
        }

        // Tests for DELETE /api/menuitemreview?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_review() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview review1 = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("sdfa")
                    .stars(5)
                    .dateReviewed(ldt1)
                    .comments("dfd")
                    .build();

                when(menuItemReviewRepository.findById(eq(15L))).thenReturn(Optional.of(review1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(15L);
                verify(menuItemReviewRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 15 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_review_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(menuItemReviewRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 15 not found", json.get("message"));
        }

        // Tests for PUT /api/menuitemreview?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_review() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");
                
                MenuItemReview reviewOrig = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("wesleytruong@ucsb.edu")
                    .stars(1)
                    .dateReviewed(ldt1)
                    .comments("food was horrible")
                    .build();
                
                MenuItemReview reviewEdited = MenuItemReview.builder()
                    .itemId(6)
                    .reviewerEmail("wesley@ucsb.edu")
                    .stars(5)
                    .dateReviewed(ldt2)
                    .comments("food was amazing")
                    .build();
                    
                String requestBody = mapper.writeValueAsString(reviewEdited);

                when(menuItemReviewRepository.findById(eq(67L))).thenReturn(Optional.of(reviewOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(67L);
                verify(menuItemReviewRepository, times(1)).save(reviewEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_review_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview reviewEdited = MenuItemReview.builder()
                    .itemId(1)
                    .reviewerEmail("wesleytruong@ucsb.edu")
                    .stars(1)
                    .dateReviewed(ldt1)
                    .comments("food was horrible")
                    .build();

                String requestBody = mapper.writeValueAsString(reviewEdited);

                when(menuItemReviewRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 67 not found", json.get("message"));

        }
}
