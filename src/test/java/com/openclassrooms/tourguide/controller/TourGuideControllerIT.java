package com.openclassrooms.tourguide.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
public class TourGuideControllerIT {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void index() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void getLocation() throws Exception {

        mockMvc.perform(get("/getLocation")
                        .param("userName", "internalUser2"))
                .andExpect(status().isOk());
    }

    @Test
    public void getNearbyAttractions() throws Exception {
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", "internalUser2"))
                        .andExpect(status().isOk());
    }
    @Test
    public void getRewards() throws Exception {

        mockMvc.perform(post("/getRewards")
                        .param("userName", "internalUser2"))
                .andExpect(status().isOk());
    }

    @Test
    public void getTripDeals() throws Exception {

        mockMvc.perform(get("/getTripDeals")
                        .param("userName", "internalUser2"))
                .andExpect(status().isOk());
    }
    @Test
    public void getUser() throws Exception {

        mockMvc.perform(post("/user")
                        .param("userName", "internalUser2"))
                .andExpect(status().isOk());
    }

}


