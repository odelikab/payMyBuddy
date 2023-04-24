package com.openclassrooms.PayMyBuddy.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource datasource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().passwordEncoder(passwordEncoder()).dataSource(datasource)
				.usersByUsernameQuery("select username,password,'true' as enabled from user where username=?")
				.authoritiesByUsernameQuery("select username,password from user where username=?");

//		.inMemoryAuthentication().withUser("springuser").password(passwordEncoder().encode("spring123"))
//				.roles("USER").and().withUser("springadmin").password(passwordEncoder().encode("admin123"))
//				.roles("ADMIN", "USER");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests().antMatchers("/register").permitAll().anyRequest().permitAll()
				.and().formLogin();
		// .and().formLogin();
//		.authorizeHttpRequests().antMatchers("/admin").hasRole("ADMIN").antMatchers("/user").hasRole("USER")
//				.anyRequest().permitAll();// .authenticated();// .and();// .formLogin(withDefaults());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
