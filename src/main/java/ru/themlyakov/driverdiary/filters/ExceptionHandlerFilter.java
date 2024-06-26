package ru.themlyakov.driverdiary.filters;

import ru.themlyakov.driverdiary.controllers.AbstractController;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e) {
            response.setHeader("Content-Type","application/json");
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(AbstractController.responseString(HttpStatus.FORBIDDEN,"response","Токен является просроченным"));
        }catch (MalformedJwtException e) {
            response.setHeader("Content-Type","application/json");
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(AbstractController.responseString(HttpStatus.FORBIDDEN,"response","Поврежденный токен"));
        }catch (UnsupportedJwtException e){
            response.setHeader("Content-Type","application/json");
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(AbstractController.responseString(HttpStatus.FORBIDDEN,"response","Токен не поддерживается"));
        }catch (IllegalArgumentException e){

        }catch(SignatureException e){
            response.setHeader("Content-Type","application/json");
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(AbstractController.responseString(HttpStatus.FORBIDDEN,"response","Неправильная сигнатура токена"));
        }
    }
}