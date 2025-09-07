import {useState, useRef, useEffect} from "react";
import CategoryStore from "../../store/CategoryStore.ts";
import {type CATEGORIES, CATEGORIES_INFO} from "../../types/categories";
import "../../styles/categoryBar.css";

interface CategoryBarProps {
    isSearchMode?: boolean;
    onSearchModeToggle?: (isSearchMode: boolean) => void;
}

const CategoryBar = ({ isSearchMode = false, onSearchModeToggle }: CategoryBarProps) => {
    const [selected, setSelected] = useState<CATEGORIES[]>(CategoryStore.categories.map(c => c.name));
    const scrollContainerRef = useRef<HTMLDivElement>(null);
    const handleVerticalWheel = (e: WheelEvent) => {
        if (scrollContainerRef.current) {
            e.preventDefault();
            scrollContainerRef.current.scrollLeft += e.deltaY;
        }
    };

    const handleClick = (cat: typeof CATEGORIES_INFO[number]) => {
        if (cat.name === "ALL") {
            setSelected(["ALL"]);
            CategoryStore.setCategories([{ name: "ALL", displayName: "전체" }]);
            return;
        }
        let newSelected = selected.filter((name) => name !== "ALL");
        if (newSelected.includes(cat.name)) {
            if (newSelected.length === 1) return;
            newSelected = newSelected.filter((name) => name !== cat.name);
        } else {
            if (newSelected.length === 2) {
                newSelected = [...newSelected.slice(1), cat.name];
            } else {
                newSelected = [...newSelected, cat.name];
            }
        }
        setSelected(newSelected);
        CategoryStore.setCategories(CATEGORIES_INFO.filter(c => newSelected.includes(c.name)));

        // 카테고리 선택 시 검색 모드 해제
        if (isSearchMode && onSearchModeToggle) {
            onSearchModeToggle(false);
        }
    };

    const handleSearchToggle = () => {
        if (onSearchModeToggle) {
            if(!isSearchMode) {
                setSelected([]);
                CategoryStore.setCategories([])
            }
            onSearchModeToggle(!isSearchMode);
        }
    };

    useEffect(() => {
        setSelected(["KOREAN_FOOD"]);
        CategoryStore.setCategories([{ name: "KOREAN_FOOD", displayName: "한식" }]);
    }, []);

    return (
        <div className="category-bar-container">
            <div
                ref={scrollContainerRef}
                className="category-bar-scroll"
                onWheel={handleVerticalWheel}
            >
                {/* 검색 아이콘 버튼 */}
                <button
                    className={`category-search-button ${isSearchMode ? 'active' : 'inactive'}`}
                    onClick={handleSearchToggle}
                >
                    <svg
                        className="w-8 h-8"
                        fill="none"
                        strokeWidth={2.5}
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z"
                        />
                    </svg>
                </button>

                {CATEGORIES_INFO.filter(cat => cat.name !== "ETC").map((cat) => (
                    <button
                        key={cat.name}
                        className={`category-button ${
                            !isSearchMode && selected.includes(cat.name) ? 'active' : 'inactive'
                        }`}
                        onClick={() => handleClick(cat)}
                    >
                        {cat.displayName}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default CategoryBar;
