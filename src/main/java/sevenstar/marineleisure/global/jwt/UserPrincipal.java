package sevenstar.marineleisure.global.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Builder;

/**
 * Custom UserDetails implementation to hold authenticated user's ID, email, and authorities.
 */
@Builder
public class UserPrincipal implements UserDetails {
	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null; // OAuth 인증이므로 패스워드 사용 안 함
	}

	@Override
	public String getUsername() {
		return email; // principal로 이메일 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
