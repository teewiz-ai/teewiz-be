//package org.example.tshirtlabbackend.security;
//
//import com.example.user.User;
//import com.example.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//class GoogleOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest request) {
//        OAuth2User user = super.loadUser(request);
//        Map<String, Object> attrs = user.getAttributes();
//
//        String email = (String) attrs.get("email");
//        User localUser = userRepository
//                .findByEmail(email)
//                .orElseGet(() -> userRepository.save(
//                        User.builder()
//                                .email(email)
//                                .name((String) attrs.get("name"))
//                                .picture((String) attrs.get("picture"))
//                                .build()
//                ));
//
//        // Optionally wrap localUser into a custom OAuth2User implementation
//        return user;
//    }
//}
