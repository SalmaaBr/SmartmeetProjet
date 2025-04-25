package tn.esprit.examen.Smartmeet.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.esprit.examen.Smartmeet.security.jwt.AuthEntryPointJwt;
import tn.esprit.examen.Smartmeet.security.jwt.AuthTokenFilter;
import tn.esprit.examen.Smartmeet.security.jwt.JwtUtils;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsServiceImpl;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration

//@EnableWebSecurity
@EnableMethodSecurity

//(securedEnabled = true,
//jsr250Enabled = true,
//prePostEnabled = true) // by default

public class WebSecurityConfig {

  private final JwtUtils jwtUtils;

  private final UserDetailsServiceImpl userDetailsService;

  private final AuthEntryPointJwt unauthorizedHandler;

  public WebSecurityConfig(JwtUtils jwtUtils,
                           UserDetailsServiceImpl userDetailsService,
                           AuthEntryPointJwt unauthorizedHandler) {
    this.jwtUtils = jwtUtils;
    this.userDetailsService = userDetailsService;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter(jwtUtils, userDetailsService);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/**"
                    ).permitAll()
                    /*.requestMatchers("/api/auth/**",
                            "/InteractivePublication/**",
                            "/api/test/**",
                            "/event/createevent",
                            "/login",
                            "/admin"
                            ).permitAll()*/
//                    .requestMatchers("/dashboard/**").authenticated()
                    .anyRequest().authenticated());
    http.authenticationProvider(authenticationProvider());

    // ✅ Correction ici : on utilise `authenticationJwtTokenFilter()` sans paramètres
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Utilisez Arrays.asList si vous préférez (mais List.of est meilleur pour Java 9+)
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("X-Username","x-user-id","Authorization", "Content-Type"));
    configuration.setExposedHeaders(Arrays.asList("Authorization"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}