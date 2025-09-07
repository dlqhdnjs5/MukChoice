import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {GROUP_KEY} from "./queryKey";
import {addPlaceToGroup, createGroup, fetchGroups, fetchGroupDetail} from "../function/GroupFunc";
import type {AddPlaceToGroupRequest, CreateGroupRequest} from "../../types/group";

// 그룹 목록 조회 Query
export const useFetchGroupsQuery = (options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: [GROUP_KEY.fetchGroups],
    queryFn: fetchGroups,
    refetchOnWindowFocus: false,
    enabled: options?.enabled ?? true,
  });
};

// 새 그룹 생성 Mutation
export const useCreateGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationKey: [GROUP_KEY.createGroup],
    mutationFn: (request: CreateGroupRequest) => createGroup(request),
    onSuccess: () => {
      // 또는 그룹 목록을 다시 fetch
      queryClient.invalidateQueries({
        queryKey: [GROUP_KEY.fetchGroups]
      });
    },
    onError: (error) => {
      console.error('그룹 생성 실패:', error);
    }
  });
};

// 그룹에 장소 추가 Mutation
export const useAddPlaceToGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationKey: [GROUP_KEY.addPlaceToGroup],
    mutationFn: (request: AddPlaceToGroupRequest) => addPlaceToGroup(request),
    onSuccess: () => {
      // 그룹 목록 다시 fetch (그룹 내 장소 수 등이 업데이트될 수 있음)
      queryClient.invalidateQueries({
        queryKey: [GROUP_KEY.fetchGroups]
      });
    },
    onError: (error) => {
      console.error('그룹에 장소 추가 실패:', error);
    }
  });
};

// 그룹 상세 조회 Query
export const useFetchGroupDetailQuery = (groupId: number, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: [GROUP_KEY.fetchGroupDetail, groupId],
    queryFn: () => fetchGroupDetail(groupId),
    refetchOnWindowFocus: false,
    enabled: options?.enabled ?? true,
  });
};
