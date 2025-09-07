import {useCallback, useEffect, useRef, useState} from "react";
import {observer} from "mobx-react-lite";
import {useFetchWishListQuery} from "../../api/query/usePlace";
import PlaceCard from "../../component/place/PlaceCard";
import type {WishListItem} from "../../types/place";
import Spinner from "../../component/ui/Spinner.tsx";
import LocationStore from "../../store/LocationStore.ts";
import "../../styles/slideCard.css";
import FloatingButton from "../../component/common/FloatingButton.tsx";
import RandomChoiceModal from "../../component/choice/RandomChoiceModal";
import WishDongBar from "../../component/common/WishDongBar.tsx";
import PlaceStore from "../../store/PlaceStore.ts";

const LIMIT = 20;

const Wish = observer(() => {
    const currentLocationNo = LocationStore.currentLocation?.locationNo
    const currentDongBcode: string | undefined = PlaceStore.selectedDong?.bcode;
    const [offset, setOffset] = useState(0);
    const [items, setItems] = useState<WishListItem[]>([]);
    const [total, setTotal] = useState<number>(0);
    const [randomModalOpen, setRandomModalOpen] = useState(false);
    const {
        data,
        isLoading,
        isFetching,
        refetch,
    } = useFetchWishListQuery({offset, limit: LIMIT, currentLocationNo: currentLocationNo, bcode: currentDongBcode});

    const loader = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (data?.wishList) {
            setItems((prev) => offset === 0 ? data.wishList : [...prev, ...data.wishList]);
            setTotal(data.total ?? 0);
        }
    }, [data, offset]);

    const handleObserver = useCallback((entries: IntersectionObserverEntry[]) => {
        const target = entries[0];
        const hasMore = items.length < total;

        if (items.length === 0) return;
        if (target.isIntersecting && !isFetching && data?.wishList?.length === LIMIT && hasMore) {
            setOffset((prev) => prev + LIMIT);
        }
    }, [isFetching, data, items.length, total]);

    useEffect(() => {
        const option = {root: null, rootMargin: "20px", threshold: 1.0};
        const observer = new window.IntersectionObserver(handleObserver, option);
        if (loader.current) {
            observer.observe(loader.current);
        }

        return () => {
            if (loader.current) observer.unobserve(loader.current);
        };
    }, [handleObserver]);

    useEffect(() => {
        if (currentLocationNo !== undefined && currentLocationNo !== null) {
            return
        }

        setOffset(0);
    }, [currentLocationNo]);

    // 카드 삭제 핸들러
    const handleRemove = (placeId: string) => {
        setItems((prev) => prev.filter((item) => item.place?.id !== placeId));
        setTotal((prev) => prev - 1);
    };

    const handleRandomChoiceModalClose = () => {
        refetch()
        setRandomModalOpen(false);
    }

    if (isLoading && offset === 0) return <Spinner message="찜한 맛집을 찾아보고 있어요."/>;

    return (
        <>
            <WishDongBar/>
            <div className="mt-[62px]">
                <div className="max-w-xl mx-auto px-2 relative">
                    <RandomChoiceModal
                        open={randomModalOpen}
                        onClose={handleRandomChoiceModalClose}
                        places={items.map(item => item.place)}
                        startShuffleOnOpen={true}
                        handleCancelWish={handleRemove}
                    />
                    {items.length === 0 ? (
                        <div
                            className="flex flex-col items-center justify-center h-[60vh] font-bold text-lg text-gray-500">
                            위시리스트가 비어있어요. 마음에 드는 맛집을 찜해보세요!
                        </div>
                    ) : (
                        <>
                            {items.map((item) => item.place && (
                                <PlaceCard key={item.place.id} place={item.place} handleCancelWish={handleRemove}/>
                            ))}
                            <div ref={loader} style={{height: 30}}/>
                        </>
                    )}
                    {isFetching && <Spinner message="찜한 맛집을 불러오고 있어요."/>}
                    {items.length > 1 && <FloatingButton onClick={() => setRandomModalOpen(true)}/>}
                </div>
            </div>
        </>
    )
        ;
});

export default Wish;