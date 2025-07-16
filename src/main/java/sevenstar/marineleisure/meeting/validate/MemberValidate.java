package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.exception.CustomException;

import sevenstar.marineleisure.meeting.error.MemberError;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MemberValidate {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Member foundMember(Long memberId){
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberError.MEMBER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public void existMember(Long memberId){
		if(!memberRepository.existsById(memberId)){
			throw new CustomException(MemberError.MEMBER_NOT_EXIST);
		}
	}
}
