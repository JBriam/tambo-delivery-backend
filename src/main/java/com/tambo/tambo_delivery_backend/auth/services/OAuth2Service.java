package com.tambo.tambo_delivery_backend.auth.services;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.repositories.UserDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service {

    @Autowired
    UserDetailRepository userDetailRepository;

    @Autowired
    private AuthorityService authorityService;

    // Buscar un usuario por email
    public User getUser(String userName) {
        return userDetailRepository.findByEmail(userName);
    }

    // Crear un nuevo usuario a partir de los datos del proveedor OAuth
    public User createUser(OAuth2User oAuth2User, String provider) {
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String email = oAuth2User.getAttribute("email");

        // Obtener la URL de la imagen de perfil de Google
        String pictureUrl = oAuth2User.getAttribute("picture");

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .provider(provider)
                .enabled(true)
                .authorities(authorityService.getUserAuthority())
                .profileImageUrl(pictureUrl) // Asegúrate de tener este campo en tu entidad User
                .build();
        return userDetailRepository.save(user);
    }

}
