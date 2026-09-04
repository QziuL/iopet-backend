package github.qziul.iopet.service.impl;

import github.qziul.iopet.domain.repository.TutorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplAuthService implements UserDetailsService {

    private final TutorRepository tutorRepository;

    public ImplAuthService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return tutorRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado com o e-mail: " + username));
    }
}
