import type { GroupListResponse } from '../../types/group';

interface GroupCardProps {
  group: GroupListResponse;
  gradientColor: string;
  onClick?: () => void;
}

const GroupCard = ({ group, gradientColor, onClick }: GroupCardProps) => {
  // 멤버 이미지 컴포넌트
  const MemberImages = ({ members }: { members: GroupListResponse['members'] }) => {
    const maxVisible = 4; // 최대 4명까지 표시
    const visibleMembers = members.slice(0, maxVisible);
    const remainingCount = members.length - maxVisible;

    return (
      <div className="flex -space-x-2">
        {visibleMembers.map((member, index) => (
          <div
            key={member.userNo}
            className="w-8 h-8 rounded-full border-2 border-white bg-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600 overflow-hidden"
            style={{ zIndex: maxVisible - index }}
          >
            {member.imgPath ? (
              <img
                src={member.imgPath}
                alt={member.nickName}
                className="w-full h-full object-cover"
              />
            ) : (
              member.nickName.charAt(0).toUpperCase()
            )}
          </div>
        ))}
        {remainingCount > 0 && (
          <div
            className="w-8 h-8 rounded-full border-2 border-white bg-gray-500 flex items-center justify-center text-xs font-semibold text-white"
            style={{ zIndex: 0 }}
          >
            +{remainingCount}
          </div>
        )}
      </div>
    );
  };

  return (
    <div
      className={`${gradientColor} rounded-xl p-6 text-white shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer`}
      onClick={onClick}
    >
      {/* 그룹 제목 */}
      <h3 className="text-lg font-bold mb-3 truncate">
        {group.groupName}
      </h3>

      {/* 통계 정보 */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-4">
          <div className="text-sm">
            <span className="opacity-80">멤버</span>
            <div className="font-semibold">{group.memberCount}명</div>
          </div>
          <div className="text-sm">
            <span className="opacity-80">장소</span>
            <div className="font-semibold">{group.placeCount || 0}개</div>
          </div>
        </div>
      </div>

      {/* 멤버 이미지들 */}
      <div className="flex items-center justify-between">
        <MemberImages members={group.members} />
        <div className="text-xs opacity-80">
          {new Date(group.regTime).toLocaleDateString()}
        </div>
      </div>
    </div>
  );
};

export default GroupCard;
