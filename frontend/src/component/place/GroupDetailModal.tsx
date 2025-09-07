import {useEffect, useMemo, useState} from "react";
import CommonModal from "../ui/CommonModal";
import type {GroupListResponse} from "../../types/group";
import {useFetchGroupDetailQuery} from "../../api/query/useGroup";
import PlaceCard from "./PlaceCard";
import HorizontalPlaceCardList from "./HorizontalPlaceCardList";
import KakaoMap from "../map/KakaoMap";
import {CSSTransition} from 'react-transition-group';
import LocationStore from "../../store/LocationStore.ts";
import {observer} from "mobx-react-lite";
import { useRandomShuffle } from "../../utils/useRandomShuffle";
import { createMapInfo } from "../../utils/mapUtils";
import { shareKakaoTalkInviteFriendCustom } from "../../utils/kakaoSDK";
import UserStore from "../../store/UserStore.ts";

interface GroupDetailModalProps {
    isOpen: boolean;
    onClose: () => void;
    group: GroupListResponse | null;
}

const GroupDetailModal = observer(({isOpen, onClose, group}: GroupDetailModalProps) => {
    const currentLocation = LocationStore.currentLocation;

    // 그룹 상세 정보 조회
    const { data: groupDetail, isLoading, error } = useFetchGroupDetailQuery(
        group?.groupId || 0,
        { enabled: !!group?.groupId && isOpen }
    );

    // API 데이터를 그대로 사용 (변환 불필요)
    const places = useMemo(() => {
        return groupDetail?.places || [];
    }, [groupDetail?.places]);

    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    const {
        selectedIndex: randomIndex,
        isShuffling,
        startShuffle,
        resetShuffle
    } = useRandomShuffle(places);


    useEffect(() => {
        if (!isOpen) {
            resetShuffle();
        }
    }, [isOpen, resetShuffle]);

    const selectedPlace = useMemo(() => {
        return randomIndex !== null ? places[randomIndex] : null;
    }, [randomIndex, places]);

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

    if (!group) return null;

    // 로딩 중일 때
    if (isLoading) {
        return (
            <CommonModal open={isOpen} onClose={onClose}>
                <div className="flex flex-col gap-4 p-2 sm:p-4" style={{
                    width: 'min(896px, 90vw)',
                    height: '80vh',
                    maxHeight: '800px'
                }}>
                    <div className="flex items-center justify-center flex-1">
                        <div className="text-center">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
                            <div className="text-gray-600">그룹 정보를 불러오는 중...</div>
                        </div>
                    </div>
                </div>
            </CommonModal>
        );
    }

    // 에러가 발생했을 때
    if (error) {
        return (
            <CommonModal open={isOpen} onClose={onClose}>
                <div className="flex flex-col gap-4 p-2 sm:p-4" style={{
                    width: 'min(896px, 90vw)',
                    height: '80vh',
                    maxHeight: '800px'
                }}>
                    <div className="flex items-center justify-center flex-1">
                        <div className="text-center">
                            <div className="text-red-500 text-lg mb-2">오류가 발생했습니다</div>
                            <div className="text-gray-600">그룹 정보를 불러올 수 없습니다.</div>
                        </div>
                    </div>
                </div>
            </CommonModal>
        );
    }

    const displayGroup = groupDetail || group;

    return (
        <CommonModal open={isOpen} onClose={onClose}>
            <div className="flex flex-col gap-4 p-2 sm:p-4" style={{
                width: 'min(896px, 90vw)',
                height: '80vh',
                maxHeight: '800px'
            }}>
                <div className="flex-shrink-0 border-b border-gray-200 pb-4">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-2xl font-bold text-gray-800">{displayGroup.groupName}</h2>
                            <p className="text-gray-500 mt-1">
                                멤버 {displayGroup.memberCount}명 · 장소 {displayGroup.placeCount}개
                            </p>
                            {groupDetail && (
                                <p className="text-gray-400 text-sm mt-1">
                                    생성일: {new Date(groupDetail.regTime).toLocaleDateString()}
                                </p>
                            )}
                        </div>
                        <div className="flex items-center gap-2">
                            <button
                                onClick={() => shareKakaoTalkInviteFriendCustom(displayGroup.groupId)}
                                className="flex items-center gap-2 px-4 py-2 bg-yellow-400 hover:bg-yellow-500 text-black font-semibold rounded-lg transition-colors"
                            >
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M12 2C6.48 2 2 5.58 2 10c0 2.17 1.12 4.12 2.86 5.5L4 20l4.5-.86C9.66 19.64 10.82 20 12 20c5.52 0 10-3.58 10-8s-4.48-10-10-10z" fill="currentColor"/>
                                    <path d="M16 12h-3v3h-2v-3H8v-2h3V7h2v3h3v2z" fill="white"/>
                                </svg>
                                그룹초대
                            </button>
                            <button
                                onClick={onClose}
                                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                            >
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                     xmlns="http://www.w3.org/2000/svg">
                                    <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" strokeWidth="2"
                                          strokeLinecap="round"/>
                                </svg>
                            </button>
                        </div>
                    </div>

                    {groupDetail?.members && (
                        <div className="mt-4">
                            <h3 className="text-sm font-semibold text-gray-700 mb-2">그룹 멤버</h3>
                            <div className="flex flex-wrap gap-2">
                                {groupDetail.members.slice(0, 4).map((member) => (
                                    <div
                                        key={member.userNo}
                                        className="flex items-center gap-2 bg-gray-100 rounded-full px-3 py-1"
                                    >
                                        <div className="w-6 h-6 rounded-full bg-gray-300 flex items-center justify-center text-xs font-semibold">
                                            {member.imgPath ? (
                                                <img
                                                    src={member.imgPath}
                                                    alt={member.nickName}
                                                    className="w-full h-full object-cover rounded-full"
                                                />
                                            ) : (
                                                member.nickName.charAt(0).toUpperCase()
                                            )}
                                        </div>
                                        <span className="text-sm text-gray-700">
                                            {member.nickName}
                                            {member.isOwner && (
                                                <span className="ml-1 text-xs text-blue-600 font-semibold">👑</span>
                                            )}
                                        </span>
                                    </div>
                                ))}
                                {groupDetail.members.length > 4 && (
                                    <div className="flex items-center gap-2 bg-gray-100 rounded-full px-3 py-1">
                                        <span className="text-sm text-gray-600">
                                            +{groupDetail.members.length - 4}명 더
                                        </span>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                <div className="flex-1 overflow-y-auto">
                    {places.length === 0 ? (
                        <div className="flex items-center justify-center h-64">
                            <div className="text-center">
                                <div className="text-gray-500 text-lg mb-2">아직 추가된 장소가 없습니다</div>
                                <div className="text-gray-400 text-sm">장소를 추가해보세요!</div>
                            </div>
                        </div>
                    ) : (
                        <div className="space-y-6">
                            {(!isLoading && !isShuffling && randomIndex === null && places.length > 0) && (
                                <HorizontalPlaceCardList
                                    places={places}
                                    onCardHover={handleCardHover}
                                    onCardLeave={handleCardLeave}
                                />
                            )}
                            {(isShuffling || randomIndex !== null) && (
                                <div className="flex justify-center py-8">
                                    <CSSTransition
                                        in={(places && randomIndex !== null)}
                                        timeout={400}
                                        classNames={isShuffling ? 'shake-fade-scale' : 'fade-scale'}
                                        unmountOnExit
                                        key={randomIndex}
                                    >
                                        {places && randomIndex !== null ? (
                                            <div style={{width: 350, maxWidth: '95%'}}>
                                                <PlaceCard place={places[randomIndex]}/>
                                            </div>
                                        ) : null}
                                    </CSSTransition>
                                </div>
                            )}

                            <div className="h-96">
                                {currentLocation ? (
                                    <KakaoMap
                                        centerPlaceInfo={mapInfo.centerPlaceInfo}
                                        selectedPlacesInfo={mapInfo.selectedPlacesInfo}
                                        placesInfos={mapInfo.placesInfos}
                                        isShuffling={isShuffling}
                                        user={UserStore.user ||  undefined}
                                    />
                                ) : (
                                    <div className="flex items-center justify-center h-full border rounded-lg bg-gray-50">
                                        <div className="text-center text-gray-600">
                                            <div>위치를 등록한 후 랜덤 맛집을 뽑아보세요!</div>
                                        </div>
                                    </div>
                                )}
                            </div>

                            {places && places.length > 1 && (
                                <div className="flex justify-center pb-6">
                                    <button
                                        className="px-6 py-2 bg-gradient-to-br bg-[#ff5e62] text-white font-bold shadow-lg hover:scale-110 transition-transform border-2 border-white rounded-lg"
                                        onClick={startShuffle}
                                        disabled={isShuffling || !places.length}
                                    >
                                        {isShuffling ? '랜덤 돌리는중..' : '오늘은 여기! 랜덤 맛집 선택'}
                                    </button>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </CommonModal>
    );
});

export default GroupDetailModal;
