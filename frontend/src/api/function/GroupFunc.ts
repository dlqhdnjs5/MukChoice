import {getApi, postApi} from "./index";
import type {
  AddPlaceToGroupRequest,
  CreateGroupInvitationRequest,
  CreateGroupRequest,
  GroupDetailResponse, GroupInvitationResponse
} from "../../types/group";
import axios from 'axios';

// 그룹 목록 조회
export const fetchGroups = async () => {
  const response = await getApi('/api/groups');
  return response.data;
};

// 새 그룹 생성
export const createGroup = async (request: CreateGroupRequest) => {
  const response = await postApi('/api/groups', request);
  return response.data;
};

// 그룹에 장소 추가
export const addPlaceToGroup = async (request: AddPlaceToGroupRequest) => {
  await postApi(`/api/groups/${request.groupId}/places`, {
    placeId: request.placeId,
    placeName: request.placeName,
    x: request.x,
    y: request.y,
    placeCategory: request.placeCategory
  });
};

// 그룹 상세 조회
export const fetchGroupDetail = async (groupId: number): Promise<GroupDetailResponse> => {
  const response = await getApi(`/api/groups/${groupId}`);
  return response.data;
};

// 그룹 초대 생성 API
export const createGroupInvitation = async (request: CreateGroupInvitationRequest): Promise<GroupInvitationResponse> => {
  const response = await axios.post('/api/groups/invitations', request);
  return response.data;
};
