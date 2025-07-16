package sevenstar.marineleisure.meeting.security;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import sevenstar.marineleisure.global.jwt.UserPrincipal;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		UserPrincipal principal = UserPrincipal.builder()
			.id(customUser.id())
			.email(customUser.username()) // Use username as email for simplicity
			.authorities(Collections.emptyList()) // Or create authorities based on roles()
			.build();

		Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}
