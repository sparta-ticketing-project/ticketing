package com.example.ticketing.domain.concert.controller;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertRedisRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSearchResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSingleResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.service.ConcertService;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.service.UserService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.auth.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConcertController.class)
@TestPropertySource(properties = {
        "jwt.secret.key=5Gk6hibHDtKLFVk4NdBX039rvehSLNjfKsdXpm/pHsU="
})
class ConcertControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ConcertService concertService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("인기 콘서트 검색 api v1 테스트")
    void findPopularConcertV1ApiTest() throws Exception {
        // given
        Integer limit = 10;
        Pageable pageable = PageRequest.of(0, limit);

        List<ConcertRankResponse> concertRankResponses = new ArrayList<>();
        long viewCount = 5;
        for(int i=1;i<=5;i++){
            ConcertRankResponse concertRankResponse = ConcertRankResponse.builder()
                    .rank(i)
                    .viewCount((long)i)
                    .concertId((long)i)
                    .concertName("concert" + i)
                    .build();
            concertRankResponses.add(concertRankResponse);
        }

        given(concertService.findPopularConcertsV1(limit)).willReturn(new PageImpl<>(concertRankResponses, pageable, concertRankResponses.size()));

        // when
        mockMvc.perform(get("/api/v1/concerts/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].rank").value(1))
                .andExpect(jsonPath("$.content[0].viewCount").value(1L))
                .andExpect(jsonPath("$.content[0].concertId").value(1L))
                .andExpect(jsonPath("$.content[0].concertName").value("concert1"));
    }

    @Test
    @DisplayName("인기 콘서트 검색 api v2 테스트")
    void findPopularConcertV2ApiTest() throws Exception {
        // given
        Integer limit = 10;

        List<ConcertRedisRankResponse> concertRedisRankResponses = new ArrayList<>();
        long viewCount = 5;
        for(int i=1;i<=5;i++){
            ConcertRedisRankResponse concertRedisRankResponse = ConcertRedisRankResponse.builder()
                    .rank(i)
                    .viewCount((long)i)
                    .concertId((long)i)
                    .concertName("concert" + i)
                    .build();
            concertRedisRankResponses.add(concertRedisRankResponse);
        }

        given(concertService.findPopularConcertsV2(limit)).willReturn(concertRedisRankResponses);

        // when, then
        mockMvc.perform(get("/api/v2/concerts/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$.[0].rank").value(1))
                .andExpect(jsonPath("$.[0].viewCount").value(1L))
                .andExpect(jsonPath("$.[0].concertId").value(1L))
                .andExpect(jsonPath("$.[0].concertName").value("concert1"));
    }

    @Test
    @DisplayName("콘서트 단건 조회 api 테스트")
    void findSingleConcertTest() throws Exception {
        //given
        Long userId = 1L;
        Long concertId = 1L;
        String concertName = "concert";
        LocalDateTime localDateTime = LocalDateTime.now();
        Integer totalSeatCount = 1000;
        Integer availableSeatCount= 1000;
        Long viewCount = 1000L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);

        ConcertSingleResponse concertSingleResponse = ConcertSingleResponse.builder()
                .id(userId)
                .concertName(concertName)
                .concertType(ConcertType.MUSICAL.getDescription())
                .concertDate(localDateTime)
                .ticketingDate(localDateTime)
                .totalSeatCount(totalSeatCount)
                .availableSeatCount(availableSeatCount)
                .viewCount(viewCount)
                .concertSeatDetailResponses(List.of())
                .build();

        given(concertService.findSingleConcert(userId, concertId)).willReturn(concertSingleResponse);

        String accessToken = jwtUtil.createAccessToken(userId, UserRole.USER);

        // when, then
        mockMvc.perform(get("/api/v1/concerts/1").header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.concertName").value(concertName))
                .andExpect(jsonPath("$.concertType").value(ConcertType.MUSICAL.getDescription()))
                .andExpect(jsonPath("$.concertDate", Matchers.startsWith(formattedDateTime)))
                .andExpect(jsonPath("$.ticketingDate",  Matchers.startsWith(formattedDateTime)))
                .andExpect(jsonPath("$.totalSeatCount").value(totalSeatCount))
                .andExpect(jsonPath("$.availableSeatCount").value(availableSeatCount))
                .andExpect(jsonPath("$.viewCount").value(viewCount))
                .andExpect(jsonPath("$.concertSeatDetailResponses").isEmpty());
    }

    @Test
    @DisplayName("콘서트 검색 api 테스트")
    void findConcertByKeywordTest() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);

        Integer totalSeatCount = 1000;
        Integer availableSeatCount = 1000;
        Long viewCount = 100L;

        String concertName = "concert";
        String concertType = "concertType";

        Pageable pageable = PageRequest.of(0,10);

        List<ConcertSearchResponse> concertSearchResponses = new ArrayList<>();
        for(int i=1;i<=5;i++){
            ConcertSearchResponse concertSearchResponse = ConcertSearchResponse.builder()
                    .id((long)i)
                    .concertName("concert"+i)
                    .concertType(ConcertType.MUSICAL.getDescription())
                    .concertDate(localDateTime)
                    .ticketingDate(localDateTime)
                    .totalSeatCount(totalSeatCount)
                    .availableSeatCount(availableSeatCount)
                    .viewCount(viewCount)
                    .build();

            concertSearchResponses.add(concertSearchResponse);
        }

        given(concertService.findConcertsByKeyword(pageable, concertName, concertType)).willReturn(new PageImpl<>(concertSearchResponses, pageable, concertSearchResponses.size()));

        // when, then
        mockMvc.perform(get("/api/v1/concerts").param("concertName", concertName).param("concertType", concertType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].concertName").value("concert1"))
                .andExpect(jsonPath("$.content[0].concertType").value("뮤지컬"))
                .andExpect(jsonPath("$.content[0].concertDate", Matchers.startsWith(formattedDateTime)))
                .andExpect(jsonPath("$.content[0].ticketingDate", Matchers.startsWith(formattedDateTime)))
                .andExpect(jsonPath("$.content[0].totalSeatCount").value(totalSeatCount))
                .andExpect(jsonPath("$.content[0].availableSeatCount").value(availableSeatCount))
                .andExpect(jsonPath("$.content[0].viewCount").value(viewCount));
    }
}
