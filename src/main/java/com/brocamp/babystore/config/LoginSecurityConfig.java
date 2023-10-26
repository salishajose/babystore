package com.brocamp.babystore.config;

import com.brocamp.babystore.handlers.CustomSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class LoginSecurityConfig {
    private UserDetailsService userDetailsService;
    private CustomSuccessHandler customSuccessHandler;

    public LoginSecurityConfig(UserDetailsService userDetailsService,
                               CustomSuccessHandler customSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeHttpRequests(configurer->
                        configurer
                                .requestMatchers("/admin/css/**","/admin/fonts.material-icon/**","/admin/imgs/**","/admin/sass/","/admin/js/**").permitAll()
                                .requestMatchers("/user/css/**", "/user/imgs/**", "/user/js/**", "/user/fonts/**", "/user/sass/**").permitAll()
                                .requestMatchers("/image-product/**","/sizeImages").permitAll()
                                .requestMatchers("/signup/**","/user-registration/**").permitAll()
                                .requestMatchers("/verifyEmail","/forgotpassword").permitAll()
                                .requestMatchers("/sendEmailOTPLogin","/forgotPasswordOTPLogin").permitAll()
                                .requestMatchers("/otpvalidation/**","/validateOTP/**").permitAll()
                                .requestMatchers("/sendVerificationEmailOtp").permitAll()
                                .requestMatchers("/","/shop/**","/index/**").permitAll()
                                .requestMatchers("/admin_panel/**").hasAuthority("ADMIN")
                                .requestMatchers("/user_home/**").hasAuthority("CUSTOMER")
                                .requestMatchers("/user_home/createOrder","/barChart/**").permitAll()
                                .anyRequest().authenticated()
                        )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .formLogin(form->
                            form.loginPage("/login")
                                    .loginProcessingUrl("/authenticateTheUser")
                                    .successHandler(customSuccessHandler)
                                    .permitAll())
                .logout(logout->logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION")
                        .permitAll());
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
