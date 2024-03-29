package com.spring.serverJWT.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;



public class JWTValidarFilter extends BasicAuthenticationFilter {

	public static final String HEADER_ATRIBUTO = "Authorization"; //atributo do cabecalho
	public static final String ATRIBUTO_PREFIXO = "Bearer ";	
	
	public JWTValidarFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	// step 1: interceptar o cabecalho da requisicao 
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
									HttpServletResponse response, 
									FilterChain chain) throws IOException, ServletException {
		
		String atributo = request.getHeader(HEADER_ATRIBUTO); 
		
		if(atributo == null) {
			chain.doFilter(request, response);
			return;
		}
		
		if(!atributo.startsWith(ATRIBUTO_PREFIXO)) {
			chain.doFilter(request, response);
			return;
		}
		
		String token = atributo.replace(ATRIBUTO_PREFIXO, "");  //limpando o token	
		
		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
		
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		chain.doFilter(request, response);
	}
	
	//metodo para ler o token e determinar que o user e valido 
	private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
		String usuario = JWT.require(Algorithm.HMAC512(JWTAutenticarFilter.TOKEN_SENHA))
				.build()
				.verify(token)
				.getSubject();
		
		if(usuario == null) {
			return null;
		}
		
		return new UsernamePasswordAuthenticationToken(usuario, null, new ArrayList<>());
	}

}