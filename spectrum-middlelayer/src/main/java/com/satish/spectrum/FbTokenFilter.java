package com.satish.spectrum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;

public class FbTokenFilter implements Filter {

	@Autowired
	private CacheManager cacheManager;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		
		if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
			chain.doFilter(arg0, arg1);
			return;
		}
		
		chain.doFilter(arg0, arg1);
		
		/*
	
		String fbauth = request.getHeader("fbauth");
		
		try {
			if (fbauth == null) {
				System.out.println("No FBAuth Header found - Sending 401");
				throw new ServletException();
			}

			String secret = "6t3Oh29Gn1smrTGiI0P3PL5pWdNUE5sPcWRLvesB"; //FB Secret - Move it to application.properties
			Claims claims = Jwts.parser().setSigningKey(secret.getBytes("UTF-8")).parseClaimsJws(fbauth).getBody();
			Date issuedDate = claims.getIssuedAt();
			long diff = new Date().getTime() - issuedDate.getTime();
			long hrs = TimeUnit.MILLISECONDS.toHours(diff);
			if (hrs > 24) {
				System.out.println("Expired Token, Please Login Again");
				throw new ServletException();
			}

			chain.doFilter(arg0, arg1);
		} catch (ServletException ex) {
			response.sendError(response.SC_UNAUTHORIZED);

		} catch (Exception e) {
			response.sendError(response.SC_UNAUTHORIZED);
			e.printStackTrace();
		}
		*/
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
