import { useEffect, useRef, useState } from 'react';

interface MoreActionsMenuProps {
  onAddToGroup: () => void;
  className?: string;
}

const MoreActionsMenu = ({ onAddToGroup, className = "" }: MoreActionsMenuProps) => {
  const [showMoreMenu, setShowMoreMenu] = useState(false);
  const moreMenuRef = useRef<HTMLDivElement>(null);

  // 외부 클릭 시 더보기 메뉴 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (moreMenuRef.current && !moreMenuRef.current.contains(event.target as Node)) {
        setShowMoreMenu(false);
      }
    };

    if (showMoreMenu) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showMoreMenu]);

  return (
    <div className={`flex justify-end relative ${className}`} ref={moreMenuRef}>
      <button
        onClick={(e) => {
          e.stopPropagation();
          setShowMoreMenu(!showMoreMenu);
        }}
        className="p-1 hover:bg-gray-100 bg-white rounded-full transition-colors"
        aria-label="더보기"
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle cx="12" cy="5" r="2" fill="#6B7280"/>
          <circle cx="12" cy="12" r="2" fill="#6B7280"/>
          <circle cx="12" cy="19" r="2" fill="#6B7280"/>
        </svg>
      </button>

      {/* 드롭다운 메뉴 */}
      {showMoreMenu && (
        <div className="absolute top-8 right-0 bg-white border border-gray-200 rounded-lg shadow-lg z-20 min-w-[140px] overflow-hidden">
          <button
            onClick={(e) => {
              e.stopPropagation();
              onAddToGroup();
              setShowMoreMenu(false);
            }}
            className="w-full px-4 py-3 text-left text-sm hover:bg-gray-500 transition-colors flex items-center gap-2"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2v20M2 12h20" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
            그룹에 추가
          </button>
        </div>
      )}
    </div>
  );
};

export default MoreActionsMenu;
