package com.openclassrooms.PayMyBuddy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.openclassrooms.PayMyBuddy.model.User;
import com.openclassrooms.PayMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	private User testUser;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setUp() {
		userRepository.deleteAll();
		testUser = new User();
		testUser.setName("testUser");
		testUser.setPassword(passwordEncoder.encode("testUser"));
		testUser.setUsername("testUser");
		testUser.setEmail("testUser");
		assertEquals(-1, testUser.getUserId());
		testUser = userRepository.save(testUser);
		assertNotEquals(-1, testUser.getUserId());
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
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", testUser.getUserId())).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void DefaultLoginMessage() throws Exception {
		mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testLoginWithGoodPassword() throws Exception {
		mockMvc.perform(formLogin("/login").user("testUser").password("testUser")).andDo(print())
				.andExpect(authenticated());
	}

	@Test
	public void testLoginWithWrongPassword() throws Exception {
		mockMvc.perform(formLogin("/login").user("testUser").password("testpassword")).andDo(print())
				.andExpect(unauthenticated());
	}

	@Test
	public void testAccessAssociationForLoggedUser() throws Exception {
		mockMvc.perform(get("/addConnection").with(user("testUser").password("testUser"))).andDo(print())
				.andExpect(view().name("addConnection")).andExpect(model().size(2))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	public void testAccessAssociationForUnloggedUser() throws Exception {
		mockMvc.perform(get("/addConnection")).andDo(print()).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));
//				.andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception));
	}

	@Test
	public void testAssociationWithKnownUser() throws Exception {
		User user2 = new User();
		user2.setEmail("user2");
		user2.setUsername("user2");
		user2.setName("user2");
		user2.setPassword(passwordEncoder.encode("user2"));
		user2 = userRepository.save(user2);
		Set<User> listAssociateUsers = new HashSet<>();
//		listAssociateUsers = testUser.getAssociateTo();
//		assertEquals(0, listAssociateUsers.size());
		mockMvc.perform(post("/addConnection").with(user("testUser").password("testUser")).param("username", "user2")
				.param("depositAmount", "0")).andDo(print()).andExpect(status().isFound());
		testUser = userRepository.findByUsername("testUser").get();
		listAssociateUsers = testUser.getAssociateTo();
		listAssociateUsers = userRepository.findByAssociateToUserId(user2.getUserId());
		assertEquals(1, listAssociateUsers.size());
	}

	@Test
	public void testAssociationWithUnknownUser() throws Exception {
		try {
			mockMvc.perform(post("/addConnection").with(user("testUser").password("testUser"))
					.param("username", "userUnkwown").param("depositAmount", "0")).andDo(print());
			fail("must fail when unkwown user");
//				.andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception));
		} catch (Exception e) {

		}
	}

	@Test
	public void testHomepage() throws Exception {
		mockMvc.perform(get("/")).andDo(print()).andExpect(view().name("index")).andExpect(model().size(3))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	public void testHomepageWithAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/").with(user("testUser").password("testUser"))).andDo(print())
				.andExpect(view().name("index")).andExpect(model().size(5))
				.andExpect(model().attributeExists("alltransac"));
	}

	@Test
	public void testTransactionWithKnownUser() throws Exception {
//		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/").with(user("testUser").password("testUser")).param("transferAmount", "100")
				.param("receiverUsername", "user2")).andDo(print());
//				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	public void testDeposit() throws Exception {
		mockMvc.perform(post("/profile").with(user("testUser").password("testUser")).param("depositAmount", "100")
				.param("username", "testUser")).andDo(print()).andExpect(redirectedUrl("/"));
	}
}
