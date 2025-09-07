import {useState} from "react";
import {useFetchWishDongListQuery} from "../../api/query/usePlace";
import type {WishDongInfo} from "../../types/place.ts";
import PlaceStore from "../../store/PlaceStore";

const WishDongBar = () => {
    const {data} = useFetchWishDongListQuery();
    const allDong = {bcode: 'ALL', dong: '전체'};
    const dongInfos: WishDongInfo[] = [allDong, ...(data?.dongInfos || [])];
    const [selected, setSelected] = useState<WishDongInfo>(PlaceStore.selectedDong);

    const handleClick = (dong: WishDongInfo) => {
        setSelected(dong);
        PlaceStore.setSelectedDong(dong);
    };

    return (
        <div className="fixed w-full left-0 right-0 bg-white border-b border-gray-200 overflow-x-auto scrollbar-hide">
            <div className="flex items-center min-w-max px-3 py-1.5">
                {dongInfos?.map((dong) => (
                    <button
                        key={dong.bcode}
                        className={`px-3 py-1.5 mx-1 text-base rounded-full border transition-colors duration-150 text-center 
                        whitespace-nowrap ${selected?.bcode === dong.bcode ? 'bg-[#ff5e62] text-white border-[#ff5e62]' : 'bg-gray-100 text-gray-700 border-gray-300 hover:bg-orange-100'}`}
                        onClick={() => handleClick(dong)}
                    >
                        {dong.dong}
                    </button>
                ))}
            </div>
        </div>
    )
}

export default WishDongBar;
