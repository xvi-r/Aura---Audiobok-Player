package com.example.audiobooks.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.audiobooks.application.ApplicationState;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SetupModeFilter extends OncePerRequestFilter {

    private final ApplicationState applicationState;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (applicationState.isSetupRequired()
            && !request.getRequestURI().startsWith("/setup")
            && !request.getRequestURI().startsWith("/js/")
            && !request.getRequestURI().startsWith("/css/")) {
            response.sendRedirect("/setup");
            return;
        } 
        if (!applicationState.isSetupRequired() 
                && request.getRequestURI().equals("/setup")) {

            response.sendRedirect("/library");
            return;
        }

         filterChain.doFilter(request, response);
}

   
    }
