import {useEffect, useMemo, useState} from "react";
import {useFetchPlacesMultiCategoryQuery, useSearchPlaces} from "../../api/query/usePlace.ts";
import PlaceCard from "../../component/place/PlaceCard";
import LocationStore from "../../store/LocationStore";
import CategoryStore from "../../store/CategoryStore";
import {observer} from "mobx-react-lite";
import Spinner from "../../component/ui/Spinner.tsx";
import {CSSTransition} from 'react-transition-group';
import confetti from 'canvas-confetti';
import KakaoMap from "../../component/map/KakaoMap.tsx";
import type {Place} from "../../types/place.ts";
import CategoryBar from "../../component/common/CategoryBar.tsx";
import SearchBar from "../../component/common/SearchBar.tsx";
import {defaultConfettiOptions} from "../../utils/confettiOptions.ts";
import HorizontalPlaceCardList from "../../component/place/HorizontalPlaceCardList.tsx";
import UserStore from '../../store/UserStore.ts';
import {createMapInfo} from "../../utils/mapUtils";

const Choice = observer(() => {
    /*const isMobile = /Mobi/i.test(window.navigator.userAgent);*/
    const currentLocation = LocationStore.currentLocation;
    const selectedCategory = CategoryStore.categories;
    const [searchQuery, setSearchQuery] = useState<string>('');
    const [isSearchMode, setIsSearchMode] = useState<boolean>(false);
    const {data: fetchPlacesResponse, isLoading} = useFetchPlacesMultiCategoryQuery(
        {
            coordinateX: String(currentLocation?.x ?? ""),
            coordinateY: String(currentLocation?.y ?? ""),
            categories: selectedCategory.map(value => value.name),
        },
        {enabled: !!currentLocation && !isSearchMode}
    );
    const {data: searchResponse, isLoading: isSearchLoading} = useSearchPlaces(
        {
            coordinateX: String(currentLocation?.x ?? ""),
            coordinateY: String(currentLocation?.y ?? ""),
            query: searchQuery,
        },
        {enabled: !!currentLocation && !!searchQuery && isSearchMode}
    );
    const places: Place[] = isSearchMode
        ? (searchResponse?.places || [])
        : (fetchPlacesResponse?.places || []);

    const [randomIndex, setRandomIndex] = useState<number | null>(null);
    const [isShuffling, setIsShuffling] = useState(false);
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    const selectedPlace = useMemo(() => {
        return randomIndex !== null ? places[randomIndex] : null;
    }, [randomIndex, places]);

    // 검색 핸들러
    const handleSearch = (query: string) => {
        setSearchQuery(query);
        setRandomIndex(null);
        setIsShuffling(false);
    };

    // 검색 초기화 핸들러
    const handleClearSearch = () => {
        setSearchQuery('');
        setRandomIndex(null);
        setIsShuffling(false);
    };

    // 검색 모드 토글 핸들러 추가
    const handleSearchModeToggle = (newIsSearchMode: boolean) => {
        setIsSearchMode(newIsSearchMode);
        if (!newIsSearchMode) {
            setSearchQuery('');
        }
        setRandomIndex(null);
        setIsShuffling(false);
    };

    // 랜덤 돌리기 애니메이션
    const startShuffle = () => {
        if (!places.length) return;
        setIsShuffling(true);
        setRandomIndex(null);
        let count = 0;
        const shuffleDuration = 1500; // 1.5초
        const interval = 70; // 카드 바뀌는 속도(ms)
        const maxCount = Math.floor(shuffleDuration / interval);
        const timer = setInterval(() => {
            setRandomIndex(Math.floor(Math.random() * places.length));
            count++;
            if (count > maxCount) {
                clearInterval(timer);
                const finalIndex = Math.floor(Math.random() * places.length);
                setRandomIndex(finalIndex);
                setIsShuffling(false);
            }
        }, interval);
    }

    // 뽑기 애니메이션 효과 (confetti)
    useEffect(() => {
        if (!isShuffling && randomIndex !== null) {
            confetti(defaultConfettiOptions);
        }
    }, [isShuffling, randomIndex]);

    // 카테고리, 위치 변경 시 상태 초기화
    useEffect(() => {
        setRandomIndex(null);
        setIsShuffling(false);
        if (!isSearchMode) {
            setSearchQuery('');
        }
    }, [selectedCategory, currentLocation]);

    // 마우스 hover 시 해당 카드를 centerPlaceInfo로 지정
    const handleCardHover = (idx: number) => {
        setHoveredIndex(idx);
    };
    const handleCardLeave = () => {
        setHoveredIndex(null);
    };

    // 지도 정보 생성
    const mapInfo = createMapInfo(
        currentLocation,
        hoveredIndex !== null ? places[hoveredIndex] : selectedPlace,
        places
    );

    return (
        <>
            <CategoryBar
                isSearchMode={isSearchMode}
                onSearchModeToggle={handleSearchModeToggle}
            />
            <div className="mt-[62px]">
                {isSearchMode && (
                    <div className="px-4 py-2">
                        <SearchBar
                            onSearch={handleSearch}
                            onClear={handleClearSearch}
                            placeholder="주변 2km내 맛집을 검색해보세요"
                            isLoading={isSearchLoading}
                        />
                    </div>
                )}

                <div className={`max-w-xl mx-auto flex flex-col items-center ${isSearchMode ? 'mt-2' : 'mt-8'}`} style={{minHeight: 350}}>
                    {(isLoading || isSearchLoading) && (
                        <div>
                            <Spinner message={
                                isSearchMode
                                    ? "검색 결과를 불러오고 있어요!"
                                    : "2km 내 주변의 맛집을 불러오고 있어요! 조금만 기다려주세요"
                            }/>
                        </div>
                    )}

                    {!isLoading && !isSearchLoading && currentLocation && places?.length === 0 && (
                        <div className="text-gray-500 font-bold mt-10 pb-10">
                            {isSearchMode
                                ? searchQuery && `'${searchQuery}' 이라는 맛집은 없어보여요. 다른 키워드로 검색해 보는게 어때요?`
                                : `1km 주변에 ${selectedCategory.map(value => value.displayName).join(', ')} 맛집은 없어보여요, 다른 위치를 찾아보는게 어때요?`
                            }
                        </div>
                    )}

                    {(!isLoading && !isSearchLoading && !isShuffling && randomIndex === null && places.length > 0) && (
                        <HorizontalPlaceCardList
                            places={places}
                            onCardHover={handleCardHover}
                            onCardLeave={handleCardLeave}
                        />
                    )}

                    {(isShuffling || randomIndex !== null) && (
                        <div className="centered-place-card-container">
                            <CSSTransition
                                in={(places && randomIndex !== null)}
                                timeout={400}
                                classNames={isShuffling ? 'shake-fade-scale' : 'fade-scale'}
                                unmountOnExit
                                key={randomIndex}
                            >
                                {places && randomIndex !== null ? (
                                    <div style={{width: 900, maxWidth: '95%'}}>
                                        <PlaceCard place={places[randomIndex]}/>
                                    </div>
                                ) : null}
                            </CSSTransition>
                        </div>
                    )}

                    {currentLocation ? (
                        <KakaoMap
                            user={UserStore.user || undefined}
                            centerPlaceInfo={mapInfo.centerPlaceInfo}
                            selectedPlacesInfo={mapInfo.selectedPlacesInfo}
                            placesInfos={mapInfo.placesInfos}
                            isShuffling={isShuffling}
                        />
                    ) : (
                        <div
                            className="flex flex-col items-center justify-center h-[60vh] font-bold text-lg text-gray-500">
                            <div>위치를 등록한 후 맛집을 뽑아보세요!</div>
                        </div>
                    )}

                    {places && places.length > 1 && (
                        <button className="mt-6 px-6 py-2 bg-gradient-to-br bg-[#ff5e62]
                        text-white font-boldshadow-lg hover:scale-110 transition-transform border-2 border-white"
                                onClick={startShuffle} disabled={isShuffling || !places.length}>
                            {isShuffling ? '랜덤 돌리는중..' : '오늘은 여기! 랜덤 맛집 선택'}
                        </button>
                    )}
                </div>
            </div>
        </>
    )
})

export default Choice;
