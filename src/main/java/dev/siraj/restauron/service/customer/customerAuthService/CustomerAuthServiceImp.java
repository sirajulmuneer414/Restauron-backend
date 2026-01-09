package dev.siraj.restauron.service.customer.customerAuthService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dev.siraj.restauron.DTO.customer.OTP.RegisterVerifyRequestDto;
import dev.siraj.restauron.DTO.customer.auth.AuthResponseDto;
import dev.siraj.restauron.DTO.customer.auth.GoogleLoginRequestDto;
import dev.siraj.restauron.DTO.customer.auth.LoginRequestDto;
import dev.siraj.restauron.DTO.customer.auth.RegisterRequestDto;
import dev.siraj.restauron.entity.authentication.RefreshToken;
import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.repository.customerRepo.CustomerRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.repository.userRepo.UserRepository;
import dev.siraj.restauron.service.authentication.interfaces.JwtService;
import dev.siraj.restauron.service.authentication.interfaces.RefreshTokenService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.registrarion.otpService.otpInterface.OtpService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Slf4j
public class CustomerAuthServiceImp implements CustomerAuthService{

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JwtService jwtService;
    @Autowired private OtpService otpService;
    @Autowired private RefreshTokenService refreshTokenService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    @Transactional
    public String register(RegisterRequestDto requestDto, String encryptedRestaurantId) {
        log.info("at the service for registering customer");

        if(userRepository.existsByEmail(requestDto.getEmail())){
            log.warn("User already exists with the same email");
            throw new IllegalArgumentException("Email already exists");
        }

        // for now, it's only setting as long ID and Not encrypted ID - Temporarily
        // Restaurant restaurant = restaurantRepository.findById(idEncryptionService.decryptToLongId(encryptedRestaurantId)).orElseThrow(() ->new EntityNotFoundException("Restaurant not found for ID:"+encryptedRestaurantId));
        Restaurant restaurant = restaurantRepository.findById(Long.parseLong(encryptedRestaurantId)).orElseThrow(() ->new EntityNotFoundException("Restaurant not found for ID:"+encryptedRestaurantId));
        log.info("Found restaurant, name : {}",restaurant.getName());

        UserAll user = new UserAll();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(Roles.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);

        UserAll savedUser = userRepository.save(user);

        log.info("User saved, ID ; {}",savedUser.getId());

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setRestaurant(restaurant);

        customerRepository.save(customer);

        log.info("saved customer to database, name ; {}",customer.getUser().getName());

        return jwtService.generateToken(savedUser.getRole().name(), savedUser.getEmail(), savedUser.getName(),savedUser.getId(),restaurant.getAccessLevel() != null ? restaurant.getAccessLevel() : AccessLevelStatus.FULL);


    }

    @Override
    public AuthResponseDto login(LoginRequestDto requestDto) {

        log.info("Inside the normal login for customer, email : {}",requestDto.getEmail());
        UserAll user = userRepository.findByEmail(requestDto.getEmail());

        if(user == null) {
            log.warn("User not found for email : {}",requestDto.getEmail());
            throw new UsernameNotFoundException("Invalid email or password");
        }

        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword())){
            log.warn("Wrong password for email : {}",requestDto.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        Restaurant restaurant = restaurantRepository.findById(customerRepository.findByUser(user).getRestaurant().getId()).orElseThrow(() -> new EntityNotFoundException("Restaurant not found for customer"));

        String token = jwtService.generateToken(user.getRole().name(),user.getEmail(),user.getName(), user.getId(), restaurant.getAccessLevel() != null ? restaurant.getAccessLevel() : AccessLevelStatus.FULL);

        AuthResponseDto dto = new AuthResponseDto();

        dto.setToken(token);

        Customer customer = customerRepository.findByUser(user);

        dto.setSpecialId(idEncryptionService.encryptLongId(customer.getId()));



        return dto;
    }

    @Override
    @Transactional
    public AuthResponseDto processGoogleLogin(GoogleLoginRequestDto requestDto, String encryptedId) {

        log.info("Inside the google login for customer");
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(requestDto.getToken());

            if (idToken == null) throw new IllegalArgumentException("Invalid Google token");

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

   // Temporarily commented out till setting up encrypted Restaurant id setup
            Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
         //   Long restaurantId = Long.parseLong(encryptedId);
            Long customerId = null;
            UserAll user = userRepository.findByEmail(email);
            if (user == null) {
                UserAll newUser = new UserAll();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setRole(Roles.CUSTOMER);
                newUser.setStatus(AccountStatus.ACTIVE);
                UserAll savedUser = userRepository.save(newUser);

                Customer newCustomer = new Customer();
                Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found for Id : "+restaurantId));

                newCustomer.setUser(savedUser);
                newCustomer.setRestaurant(restaurant);
                Customer customer = customerRepository.save(newCustomer);
                customerId = customer.getId();

                user = savedUser;
            }else{
                Customer customer = customerRepository.findByUser(user);
                customerId = customer.getId();
            }

            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found for Id : "+restaurantId));

            log.info("Verified for user : {}, email : {}", user.getName(), user.getEmail());

            String token =  jwtService.generateToken(user.getRole().name(), user.getEmail(), user.getName(), user.getId(), restaurant.getAccessLevel() != null ? restaurant.getAccessLevel() : AccessLevelStatus.FULL);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            AuthResponseDto authResponseDto = new AuthResponseDto(token, refreshToken);

            authResponseDto.setSpecialId(idEncryptionService.encryptLongId(customerId));

            return authResponseDto;
        } catch (Exception e) {
            log.error("Google sign-in error", e);
            throw new RuntimeException("Google sign-in failed.");
        }
    }

    @Override
    public void sendRegistrationOtp(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        otpService.generateAndSendOtp(email);

    }

    @Transactional
    public AuthResponseDto verifyAndRegister(RegisterVerifyRequestDto requestDto, String encryptedRestaurantId) {
        if (!otpService.verifyOtp(requestDto.getEmail(), requestDto.getOtp())) {
            throw new BadCredentialsException("The OTP you entered is incorrect.");
        }

        System.out.println(requestDto.getOtp());
        System.out.println(requestDto.getName());
        System.out.println(requestDto.getPassword());
        System.out.println(requestDto.getEmail());

        // OTP is valid, now proceed with the original registration logic
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // temporarily commented out till setting up encrypted Id for restaurant link
       // Restaurant restaurant = restaurantRepository.findById(idEncryptionService.decryptToLongId(encryptedRestaurantId))
       //         .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Restaurant restaurant = restaurantRepository.findById(Long.parseLong(encryptedRestaurantId))
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        UserAll user = new UserAll();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(Roles.CUSTOMER);
        user.setPhone(requestDto.getPhone());
        user.setStatus(AccountStatus.ACTIVE);
        UserAll savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setRestaurant(restaurant);
        Customer savedCustomer = customerRepository.save(customer);

        String token = jwtService.generateToken(savedUser.getRole().name(), savedUser.getEmail(), savedUser.getName(), savedUser.getId(), restaurant.getAccessLevel() != null ? restaurant.getAccessLevel() : AccessLevelStatus.FULL);

        AuthResponseDto dto = new AuthResponseDto();
        dto.setToken(token);

        dto.setSpecialId(idEncryptionService.encryptLongId(customer.getId()));

        return dto;

    }

}
