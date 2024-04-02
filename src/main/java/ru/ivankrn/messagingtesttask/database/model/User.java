package ru.ivankrn.messagingtesttask.database.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @SequenceGenerator(name = "app_user_id_seq", sequenceName = "app_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_seq")
    private Long id;
    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;
    @Column(nullable = false)
    @NotBlank
    private String password;
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    @NotBlank
    private String fio;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    private boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        // Может выглядеть странно, но так генерация id'шников происходит за счет БД, то ради неизменности hashcode
        // при обновлении id сущности используется hashcode класса
        // https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/
        // #using-a-generated-primary-key:~:text=%7D-,Using%20a%20Generated%20Primary%20Key,-As%20I%20teased
        return getClass().hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
