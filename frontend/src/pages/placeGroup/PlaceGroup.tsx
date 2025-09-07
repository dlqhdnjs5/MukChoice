import {useFetchGroupsQuery} from "../../api/query/useGroup.ts";
import type {GroupListResponse} from "../../types/group.ts";
import Spinner from "../../component/ui/Spinner.tsx";
import GroupCard from "../../component/place/GroupCard.tsx";
import GroupDetailModal from "../../component/place/GroupDetailModal.tsx";
import { useState } from "react";

const PlaceGroup = () => {
    const { data: groups = [], isLoading: groupsLoading } = useFetchGroupsQuery();
    const [selectedGroup, setSelectedGroup] = useState<GroupListResponse | null>(null);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

    // 랜덤 그라데이션 색상 배열
    const gradientColors = [
        'bg-gradient-to-br from-pink-400 to-red-400',
        'bg-gradient-to-br from-blue-400 to-indigo-500',
        'bg-gradient-to-br from-green-400 to-teal-500',
        'bg-gradient-to-br from-purple-400 to-pink-500',
        'bg-gradient-to-br from-yellow-400 to-orange-500',
        'bg-gradient-to-br from-indigo-400 to-purple-500',
        'bg-gradient-to-br from-teal-400 to-blue-500',
        'bg-gradient-to-br from-orange-400 to-red-500',
        'bg-gradient-to-br from-cyan-400 to-blue-500',
        'bg-gradient-to-br from-emerald-400 to-green-500'
    ];

    // 그룹별 랜덤 색상 선택
    const getGradientColor = (groupId: number) => {
        return gradientColors[groupId % gradientColors.length];
    };

    // 그룹 카드 클릭 핸들러
    const handleGroupClick = (group: GroupListResponse) => {
        setSelectedGroup(group);
        setIsDetailModalOpen(true);
    };

    const handleCloseDetailModal = () => {
        setIsDetailModalOpen(false);
        setSelectedGroup(null);
    };

    if (groupsLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-gray-500">그룹 목록을 불러오는 중...</div>
                {groupsLoading && <div><Spinner message="그룹 목록을 불러오고 있어요. 곧 보여질 거에요."/></div>}
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-4">
            <div className="max-w-6xl mx-auto">
                {groups.length === 0 ? (
                    <div className="text-center py-12">
                        <span className="text-lg font-bold text-gray-500">그룹이 없네요! 새로운 그룹을 만들어 친구를 초대해 보세요!</span>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {groups.map((group: GroupListResponse) => (
                            <GroupCard
                                key={group.groupId}
                                group={group}
                                gradientColor={getGradientColor(group.groupId)}
                                onClick={() => handleGroupClick(group)}
                            />
                        ))}
                    </div>
                )}
            </div>

            <GroupDetailModal
                isOpen={isDetailModalOpen}
                onClose={handleCloseDetailModal}
                group={selectedGroup}
            />
        </div>
    );
}

export default PlaceGroup;