package com.openclassrooms.PayMyBuddy;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void setUp() {
		userRepository.deleteAll();
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

	}

	@Test
	public void testAddUserWithMissingParams() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "springUser0")).andDo(print())
				.andExpect(view().name("register")).andExpect(status().is2xxSuccessful());
		Optional<User> user = userRepository.findByUsername("springUser0");
		assertTrue(user.isEmpty());
	}

	@Test
	public void testAddUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "testAddUser")
				.param("name", "testAddUser").param("email", "testAddUser").param("password", "testAddUser"))
				.andDo(print()).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	public void testAddUserWithSameEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "testAddUserWithSameEmail")
				.param("name", "testAddUserWithSameEmail").param("email", "testAddUserWithSameEmail")
				.param("password", "testAddUserWithSameEmail")).andDo(print());
		try {
			mockMvc.perform(MockMvcRequestBuilders.post("/register").param("username", "springuser1")
					.param("name", "springuser1").param("email", "testAddUserWithSameEmail")
					.param("password", "springuser1")).andDo(print());
			fail("must fail when same email");
		} catch (Exception e) {
//			e.printStackTrace();
		}

	}

	@Test
	public void testShowForm() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().is2xxSuccessful()).andExpect(view().name("register"))
				.andExpect(model().size(1)).andExpect(model().attributeExists("user"));
	}

	@Test
	public void testdeleteUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", "47")).andDo(print());
	}

	@Test
	public void DefaultLoginMessage() throws Exception {
		mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testLoginWithGoodPassword() throws Exception {
		testAddUser();
		mockMvc.perform(formLogin("/login").user("testAddUser").password("testAddUser")).andExpect(authenticated());
	}

	@Test
	public void testLoginWithWrongPassword() throws Exception {
		testAddUser();
		mockMvc.perform(formLogin("/login").user("testAddUser").password("testpassword")).andExpect(unauthenticated());

	}

}
