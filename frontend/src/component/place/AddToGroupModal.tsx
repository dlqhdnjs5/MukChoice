import { useState } from 'react';
import CommonModal from '../ui/CommonModal';
import { useFetchGroupsQuery, useCreateGroup, useAddPlaceToGroup } from '../../api/query/useGroup';
import type { GroupListResponse } from '../../types/group';
import type {CATEGORIES} from "../../types/categories.ts";

interface AddToGroupModalProps {
  isOpen: boolean;
  onClose: () => void;
  place?: {
    placeId: string;
    placeName: string;
    placeCategory: CATEGORIES;
    x: number;
    y: number;
  };
}

const AddToGroupModal = ({ isOpen, onClose, place }: AddToGroupModalProps) => {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');

  // API hooks - 실제 API 사용
  const { data: groups = [], isLoading: groupsLoading } = useFetchGroupsQuery({ enabled: isOpen });
  const createGroupMutation = useCreateGroup();
  const addPlaceToGroupMutation = useAddPlaceToGroup();

  // 그룹 생성 제한 확인 (최대 10개)
  const MAX_GROUPS = 10;
  const canCreateGroup = groups.length < MAX_GROUPS;

  const handleCreateGroup = async () => {
    if (newGroupName.trim()) {
      // 그룹 개수 제한 체크
      if (!canCreateGroup) {
        alert(`그룹은 최대 ${MAX_GROUPS}개까지만 생성할 수 있습니다.`);
        return;
      }

      try {
        const newGroup = await createGroupMutation.mutateAsync({
          groupName: newGroupName.trim()
        });

        if (place) {
          await addPlaceToGroupMutation.mutateAsync({
            groupId: newGroup.groupId,
            placeId: place.placeId,
            placeName: place.placeName,
            x: place.x,
            y: place.y,
            placeCategory: place.placeCategory
          });
        }

        setNewGroupName('');
        setShowCreateForm(false);
        onClose();
      } catch (error) {
        console.error('그룹 생성 실패:', error);
      }
    }
  };

  const handleAddToGroup = async (groupId: number) => {
    if (place) {
      try {
        addPlaceToGroupMutation.mutate({
          groupId,
          placeId: place.placeId,
          placeName: place.placeName,
          x: place.x,
          y: place.y,
          placeCategory: place.placeCategory
        });
        onClose();
      } catch (error) {
        console.error('그룹에 장소 추가 실패:', error);
      }
    }
  };

  return (
    <CommonModal open={isOpen} onClose={onClose}>
      <div className="p-6 max-w-md w-full">
        <h2 className="text-xl font-bold mb-4 text-gray-800">그룹에 추가</h2>

        {!showCreateForm ? (
          <>
            {groupsLoading ? (
              <div className="text-center py-4">
                <div className="text-gray-500">그룹 목록을 불러오는 중...</div>
              </div>
            ) : (
              <div className="space-y-3 mb-4 max-h-60 overflow-y-auto">
                {groups.length === 0 ? (
                  <div className="text-center py-4 text-gray-500">
                    아직 그룹이 없습니다. 새 그룹을 만들어보세요!
                  </div>
                ) : (
                  groups?.map((group: GroupListResponse) => (
                    <div
                      key={group.groupId}
                      className="p-3 border rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
                      onClick={() => handleAddToGroup(group.groupId)}
                    >
                      <div className="font-medium text-gray-800">{group.groupName}</div>
                      <div className="text-sm text-gray-500 mt-1">
                        멤버 {group.memberCount}명 · {new Date(group.regTime).toLocaleDateString()}
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}

            <button
              onClick={() => setShowCreateForm(true)}
              disabled={!canCreateGroup}
              className={`w-full p-3 border-2 border-dashed rounded-lg transition-colors ${
                canCreateGroup 
                  ? 'border-gray-300 text-gray-600 hover:border-blue-400 hover:text-blue-600' 
                  : 'border-gray-200 text-gray-400 cursor-not-allowed'
              }`}
              title={!canCreateGroup ? `그룹은 최대 ${MAX_GROUPS}개까지만 생성할 수 있습니다.` : ''}
            >
              {canCreateGroup ? '+ 새 그룹 만들기' : `+ 새 그룹 만들기 (${groups.length}/${MAX_GROUPS})`}
            </button>
          </>
        ) : (
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                그룹 이름
              </label>
              <input
                type="text"
                value={newGroupName}
                onChange={(e) => setNewGroupName(e.target.value)}
                placeholder="그룹 이름을 입력하세요"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                autoFocus
              />
            </div>

            <div className="flex gap-2">
              <button
                onClick={handleCreateGroup}
                disabled={!newGroupName.trim() || createGroupMutation.isPending || addPlaceToGroupMutation.isPending}
                className="px-12 py-2 bg-gradient-to-br bg-[#ff5e62] text-white font-bold shadow-lg hover:scale-110 transition-transform border-2 border-white rounded-lg"
              >
                {createGroupMutation.isPending || addPlaceToGroupMutation.isPending ? '처리중...' : '생성'}
              </button>
              <button
                  onClick={() => {
                    setShowCreateForm(false);
                    setNewGroupName('');
                  }}
                  className="px-12 py-2 bg-gradient-to-br text-gray-700 text-white font-bold shadow-lg hover:scale-110 transition-transform border-2 border-white rounded-lg"
                  disabled={createGroupMutation.isPending || addPlaceToGroupMutation.isPending}
              >
                취소
              </button>
            </div>
          </div>
        )}
      </div>
    </CommonModal>
  );
};

export default AddToGroupModal;
