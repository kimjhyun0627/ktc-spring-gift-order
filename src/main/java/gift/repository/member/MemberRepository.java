package gift.repository.member;

import gift.entity.member.Member;
import gift.entity.member.value.MemberEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail_Email(String email);

}
