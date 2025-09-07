import type {Place} from './place';
import type {CATEGORIES} from "./categories.ts";

// 그룹 생성 요청 타입
export interface CreateGroupRequest {
    groupName: string;
}

// 그룹 멤버 타입
export interface GroupMember {
    userNo: number;
    isOwner: boolean;
    email: string;
    nickName: string;
    imgPath?: string;
}

// 그룹 목록 응답 타입 (백엔드 API 응답)
export interface GroupListResponse {
    groupId: number;
    groupName: string;
    regTime: string;
    modTime: string;
    members: GroupMember[];
    memberCount: number;
    placeCount: number;
}

// 그룹 응답 타입 (기존)
export interface GroupResponse {
    id: string;
    groupName: string;
    createdAt: string;
    // 필요에 따라 추가 필드들
}

// 그룹에 장소 추가 요청 타입
export interface AddPlaceToGroupRequest {
    groupId: number;
    placeId: string;
    placeName: string;
    x: number;
    y: number;
    placeCategory: CATEGORIES;
}

// 그룹 상세 응답 타입 (Place 타입 재사용)
export interface GroupDetailResponse {
    groupId: number;
    groupName: string;
    regTime: string;
    modTime: string;
    members: GroupMember[];
    memberCount: number;
    places: Place[];
    placeCount: number;
}


// 그룹 초대 관련 타입
export interface CreateGroupInvitationRequest {
    inviterUserNo?: number;
    inviteeUserNo?: number;
    groupId: number;
    status?: boolean;
}

export interface GroupInvitationResponse {
    id: string;
    inviterUserNo: number;
    inviteeUserNo?: number;
    groupId: number;
    status: boolean;
    regTime?: string;
    modTime?: string;
}