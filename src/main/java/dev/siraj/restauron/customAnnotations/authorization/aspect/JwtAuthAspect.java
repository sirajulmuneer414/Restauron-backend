package dev.siraj.restauron.customAnnotations.authorization.aspect;

import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.exceptionHandlers.customExceptions.UnauthorizedException;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Arrays;
import java.util.Objects;


@Aspect
@Component
@Slf4j
public class JwtAuthAspect {

    @Autowired
    private JwtService jwtService;

    @Around("@within(dev.siraj.restauron.RolesAllowed) || @annotation(dev.siraj.restauron.RolesAllowed")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable{

        log.info("The process is at the authorization checker through RolesAllowed annotation");

        HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("The authorization token not found");
            throw new UnauthorizedException("Missing or Invalid Authorization");

        }

        String token = authHeader.substring(7);

        if(!jwtService.isTokenValid(token)){
            log.warn("The token is not valid");
            throw new UnauthorizedException("Invalid JWT token");
        }


        RolesAllowed rolesAllowed = getAnnotation(joinPoint);

        String[] requiredRoles = rolesAllowed.roles();

        String userRole = jwtService.extractUserRole(token);

        if(userRole == null){
            log.warn("The role is not found in the token");
            throw  new UnauthorizedException("No role found in JWT token");
        }

        boolean hasRole = Arrays.asList(requiredRoles).contains(userRole);

        if(!hasRole) {
            log.warn("Wrong role");
            throw new UnauthorizedException("User with role "+userRole+" is not authorized. Required roles are "+ String.join(", ",requiredRoles));

        }

        log.info("The role is authorized");
        return joinPoint.proceed();



    }


    private RolesAllowed getAnnotation(ProceedingJoinPoint jointPoint){

        try {
            var method = jointPoint.getTarget().getClass()
                    .getMethod(jointPoint.getSignature().getName(),((ProceedingJoinPoint)jointPoint).getArgs().getClass());
            if(method.isAnnotationPresent(RolesAllowed.class)){
                return method.getAnnotation(RolesAllowed.class);
            }
        }catch (NoSuchMethodException e){

        }

        return jointPoint.getTarget().getClass().getAnnotation(RolesAllowed.class);

    }
}
