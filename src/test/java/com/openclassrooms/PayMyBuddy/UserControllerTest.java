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
		testUser.setEmail("test@User");
		testUser.setAccountBalance(0);
		assertEquals(-1, testUser.getUserId());
		testUser = userRepository.save(testUser);
		assertNotEquals(-1, testUser.getUserId());
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	public void testAddUserWithMissingParams() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/").param("username", "springUser0")).andDo(print())
				.andExpect(view().name("index")).andExpect(status().is2xxSuccessful());
		Optional<User> user = userRepository.findByUsername("springUser0");
		assertTrue(user.isEmpty());
	}

	@Test
	public void testAddUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/").param("username", "testAddUser").param("name", "testAddUser")
				.param("email", "testAdd@User").param("password", "testAddUser")).andDo(print())
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	public void testAddUserWithSameEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/").param("username", "testAddUserWithSameEmail")
				.param("name", "testAddUserWithSameEmail").param("email", "testAddUserWithSame@Email")
				.param("password", "testAddUserWithSameEmail")).andDo(print());
		try {
			mockMvc.perform(
					MockMvcRequestBuilders.post("/").param("username", "springuser1").param("name", "springuser1")
							.param("email", "testAddUserWithSame@Email").param("password", "springuser1"))
					.andDo(print());
			fail("must fail when same email");
		} catch (Exception e) {
		}
	}

	@Test
	public void testShowForm() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().is2xxSuccessful()).andExpect(view().name("index"))
				.andExpect(model().size(1)).andExpect(model().attributeExists("user"));
	}

//	@Test
//	public void testdeleteUser() throws Exception {
//		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", testUser.getUserId())).andDo(print())
//				.andExpect(status().isOk());
//	}

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
	}

	@Test
	public void testAssociationWithKnownUser() throws Exception {
		User user2 = new User();
		user2.setEmail("user@2");
		user2.setUsername("user2");
		user2.setName("user2");
		user2.setPassword(passwordEncoder.encode("user2"));
		user2 = userRepository.save(user2);
		Set<User> listAssociateUsers = new HashSet<>();
		mockMvc.perform(post("/addConnection").with(user("testUser").password("testUser")).param("email", "user@2"))
				.andDo(print());
		testUser = userRepository.findByUsername("testUser").get();
		listAssociateUsers = userRepository.findByAssociatesUserId(user2.getUserId());
		assertEquals(1, listAssociateUsers.size());
	}

	@Test
	public void testAssociationWithUnknownUser() throws Exception {

		mockMvc.perform(
				post("/addConnection").with(user("testUser").password("testUser")).param("email", "userUnkwown"))
				.andDo(print()).andExpect(model().attribute("error", "User does not exist in database"));
	}

	@Test
	public void testAssociationUserAlreadyAssociated() throws Exception {
		User user2 = new User();
		user2.setEmail("user@2");
		user2.setUsername("user2");
		user2.setName("user2");
		user2.setPassword(passwordEncoder.encode("user2"));
		user2 = userRepository.save(user2);

		mockMvc.perform(post("/addConnection").with(user("testUser").password("testUser")).param("email", "user@2"))
				.andDo(print());

		mockMvc.perform(post("/addConnection").with(user("testUser").password("testUser")).param("email", "user@2"))
				.andDo(print()).andExpect(status().isOk()).andExpect(model().attributeExists("error"));
	}

	@Test
	public void testHomepage() throws Exception {
		mockMvc.perform(get("/")).andDo(print()).andExpect(view().name("index")).andExpect(model().size(1))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	public void testTransferPageWithAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/transfer").with(user("testUser").password("testUser"))).andDo(print())
				.andExpect(view().name("transfer")).andExpect(model().size(5))
				.andExpect(model().attributeExists("alltransac"));
	}

	@Test
	public void testTransactionWithUnknownUser() throws Exception {

		mockMvc.perform(post("/transfer").with(user("testUser").password("testUser")).param("transferAmount", "100")
				.param("receiverUsername", "user2")).andDo(print())
				.andExpect(model().attribute("error", "Receiver not found"));// .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/transfer"));

	}

	@Test
	public void testTransactionWithKnownUser() throws Exception {
		User user2 = new User();
		user2.setEmail("user@2");
		user2.setUsername("user2");
		user2.setName("user2");
		user2.setPassword(passwordEncoder.encode("user2"));
		user2 = userRepository.save(user2);
		testUser.setAccountBalance(100);
		testUser = userRepository.save(testUser);
		mockMvc.perform(post("/transfer").with(user("testUser").password("testUser")).param("transferAmount", "50")
				.param("receiverUsername", "user2")).andDo(print()).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/transfer"));
		double balanceTestUser = userRepository.findById(testUser.getUserId()).get().getAccountBalance();
		double balanceUser2 = userRepository.findById(user2.getUserId()).get().getAccountBalance();

		assertEquals(49.75, balanceTestUser);
		assertEquals(50, balanceUser2);
	}

	@Test
	public void testTransactionWithWrongAmount() throws Exception {
		User user2 = new User();
		user2.setEmail("user@2");
		user2.setUsername("user2");
		user2.setName("user2");
		user2.setPassword(passwordEncoder.encode("user2"));
		user2 = userRepository.save(user2);
		testUser.setAccountBalance(100);
		testUser = userRepository.save(testUser);
		mockMvc.perform(post("/transfer").with(user("testUser").password("testUser")).param("transferAmount", "100")
				.param("receiverUsername", "user2")).andDo(print())
				.andExpect(model().attribute("error", "Not enough money"));
	}

	@Test
	public void testDepositSuccess() throws Exception {
		mockMvc.perform(post("/profile").with(user("testUser").password("testUser")).param("depositAmount", "100")
				.param("username", "testUser")).andDo(print()).andExpect(redirectedUrl("/profile"));
		double balance = userRepository.findById(testUser.getUserId()).get().getAccountBalance();
		assertEquals(100, balance);
	}

	@Test
	public void testDepositFail() throws Exception {
		mockMvc.perform(post("/profile").with(user("testUser").password("testUser")).param("depositAmount", "1000000"))
				.andDo(print()).andExpect(model().attributeHasErrors("userDepositDTO"))
				.andExpect(view().name("profile"));
		double balance = userRepository.findById(testUser.getUserId()).get().getAccountBalance();
		assertEquals(0, balance);
	}

	@Test
	public void testWithdrawSuccess() throws Exception {
		testUser.setAccountBalance(100);
		testUser = userRepository.save(testUser);
		mockMvc.perform(post("/withdraw").with(user("testUser").password("testUser")).param("depositAmount", "100"))
				.andDo(print()).andExpect(redirectedUrl("/profile"));
		double balance = userRepository.findById(testUser.getUserId()).get().getAccountBalance();
		assertEquals(0, balance);

	}

	@Test
	public void testWithdrawWithWrongAmount() throws Exception {
		testUser.setAccountBalance(100);
		testUser = userRepository.save(testUser);
		mockMvc.perform(post("/withdraw").with(user("testUser").password("testUser")).param("depositAmount", "200")
				.param("username", "testUser")).andDo(print()).andExpect(model().size(3))
				.andExpect(model().attribute("error", "Not enough money"));
	}

	@Test
	public void testProfilePage() throws Exception {
		mockMvc.perform(get("/profile").with(user("testUser").password("testUser"))).andDo(print())
				.andExpect(view().name("profile")).andExpect(model().size(2))
				.andExpect(model().attributeExists("userDepositDTO"));
	}

	@Test
	public void testProfilePageUnauthenticated() throws Exception {
		mockMvc.perform(get("/profile")).andDo(print()).andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	public void testUpdatePassword() throws Exception {
		mockMvc.perform(
				post("/updateUser").with(user("testUser").password("testUser")).param("password", "testUpdateUser"))
				.andDo(print()).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
		mockMvc.perform(formLogin("/login").user("testUser").password("testUpdateUser")).andDo(print())
				.andExpect(authenticated());
	}
}
