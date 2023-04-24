package com.openclassrooms.PayMyBuddy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Test
	public void testAddUserError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "springUser")).andDo(print())
				.andExpect(view().name("register")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAddUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "springuser")
				.param("name", "springuser").param("email", "springuser").param("password", "springuser"))
				.andDo(print()).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	public void testShowForm() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().is2xxSuccessful()).andExpect(view().name("register"))
				.andExpect(model().size(1)).andExpect(model().attributeExists("user"));
	}

	@Test
	public void testdeleteUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/user").param("id", "springuser")).andDo(print())
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

}
