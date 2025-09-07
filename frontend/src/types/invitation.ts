// 초대 관련 타입 정의

export type InviteType = 'group';

export interface InvitationInfo {
  inviteType: InviteType;
  invitationId: string;
}

export interface ParsedInvitationState {
  isInvitation: boolean;
  invitationInfo?: InvitationInfo;
}

// state 파라미터에서 초대 정보를 파싱하는 함수
export const parseInvitationState = (state: string | null): ParsedInvitationState => {
  if (!state || !state.includes('invite')) {
    return { isInvitation: false };
  }

  // invite-group_{invitationId} 형식으로 파싱
  const parts = state.split('_');
  if (parts.length === 2 && parts[0].startsWith('invite-')) {
    // invite-group에서 group 부분 추출
    const inviteType = parts[0].replace('invite-', '') as InviteType;
    const invitationId = parts[1];

    return {
      isInvitation: true,
      invitationInfo: {
        inviteType,
        invitationId
      }
    };
  }

  return { isInvitation: false };
};
