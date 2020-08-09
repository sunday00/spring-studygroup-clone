package lec.spring.studygroupclone.config;

import lec.spring.studygroupclone.Services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(
                        "/","/main","/index", "/login", "/sign-up", "/check-email-token",
                        "/email-login", "/check-email-login", "/login-link",
                        "/login-nopassword",
                        "/favicon.ico"
                        ).permitAll()
                .mvcMatchers(
                        HttpMethod.GET, "/profile/read/*"
                    ).permitAll()
                .mvcMatchers(
                        HttpMethod.GET, "/uploads/**"
                ).permitAll()
                .anyRequest().authenticated();

        http.formLogin().loginPage("/login").usernameParameter("email").permitAll();
        http.logout().logoutSuccessUrl("/");

        http.rememberMe().userDetailsService(accountService).tokenRepository(tokenRepository());
    }

    private PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
