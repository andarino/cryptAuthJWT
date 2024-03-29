package com.spring.serverJWT.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.spring.serverJWT.services.DetalheUserServiceImpl;


//tivemos que remover essa classe do arquivo principal 
@EnableWebSecurity
public class JWTConfiguracao extends WebSecurityConfigurerAdapter{

		private final DetalheUserServiceImpl userService;
		private final PasswordEncoder passwordEncoder;
		
		public JWTConfiguracao(DetalheUserServiceImpl userService, PasswordEncoder passwordEncoder) {
			this.userService = userService;
			this.passwordEncoder = passwordEncoder;
		}

		//fala pro spring secuiryt usar nossas classes base como classes de implementacao 
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		}
		
		//configurar como o security deve entender a pagina
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()	//contra csrf
				.antMatchers(HttpMethod.POST, "/login").permitAll()		//nao pede senha no /login
				.antMatchers(HttpMethod.GET, "/cadastro").permitAll()
				.antMatchers(HttpMethod.GET, "/").permitAll()
				.antMatchers(HttpMethod.POST, "/salvar").permitAll()
				.anyRequest().authenticated()	//qualquer outra solicitacao esteja autenticada 
				.and()	//para adicionar os filtros de ...
				.addFilter(new JWTAutenticarFilter(authenticationManager()))	//autenticacao 
				.addFilter(new JWTValidarFilter(authenticationManager()))	//validacao
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);	//para nao guardar a sessao do usuario no servidor 
			http.csrf().disable();
		}
		
		//permite que app possa receber requisicoes de outros dominios (ex. dispositivos móveis)
	    @Bean
	    CorsConfigurationSource corsConfigurationSource() {
	        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

	        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
	        source.registerCorsConfiguration("/**", corsConfiguration);
	        return source;
	    }

		
}
