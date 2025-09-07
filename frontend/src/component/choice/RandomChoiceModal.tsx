import {useEffect, useState} from "react";
import CommonModal from "../ui/CommonModal.tsx";
import KakaoMap from "../map/KakaoMap.tsx";
import type {Place} from "../../types/place";
import PlaceCard from "../place/PlaceCard";
import {CSSTransition} from 'react-transition-group';
import LocationStore from "../../store/LocationStore";
import {observer} from "mobx-react-lite";
import {useRandomShuffle} from "../../utils/useRandomShuffle";
import {createMapInfo} from "../../utils/mapUtils";
import UserStore from '../../store/UserStore.ts';

interface RandomChoiceModalProps {
    open: boolean;
    onClose: () => void;
    places: Place[];
    startShuffleOnOpen?: boolean;
    handleCancelWish?: (placeId: string) => void;
}

const RandomChoiceModal = observer((props: RandomChoiceModalProps) => {
    const {open, onClose, places, startShuffleOnOpen, handleCancelWish} = props;
    const currentLocation = LocationStore.currentLocation;
    const [localPlaces, setLocalPlaces] = useState<Place[]>([]);
    const {
        selectedIndex,
        isShuffling,
        startShuffle,
        resetShuffle,
        clearShuffleTimer,
        setIndex
    } = useRandomShuffle(localPlaces);
    const currentSelectedPlace = selectedIndex !== null ? localPlaces[selectedIndex] : null;

    useEffect(() => {
        setLocalPlaces([...places]);
    }, [places]);

    useEffect(() => {
        if (!open) {
            resetShuffle();
        } else if (startShuffleOnOpen) {
            handleStartShuffle();
        }
    }, [open, startShuffleOnOpen]);

    useEffect(() => {
        if (open && !localPlaces.length) {
            onClose();
        }
    }, [localPlaces, open, onClose]);

    useEffect(() => {
        return clearShuffleTimer;
    }, []);

    const handleStartShuffle = () => {
        if (!localPlaces.length) {
            onClose();
            return;
        }
        startShuffle();
    };

    const handleCancelWishExtend = (placeId: string) => {
        setLocalPlaces(prev => {
            const updatedPlaces = prev.map(place =>
                place.id === placeId ? {...place, isWish: false} : place
            );

            // 좋아요 취소 후 남은 장소가 1개 이하면 모달 닫기
            const isLastWish = updatedPlaces.filter(place => place.isWish).length;
            if (isLastWish <= 1) {
                onClose();
            } else {
                // 취소한후 남은 장소가 2개 이상 있으면 노출되는 카드 인덱스 초기화
                setIndex(0);
            }

            return updatedPlaces;
        });

        if (handleCancelWish) {
            handleCancelWish(placeId);
        }
    };

    const mapInfo = createMapInfo(currentLocation, currentSelectedPlace, localPlaces);
    return (
        <CommonModal open={open} onClose={onClose}>
            <div className="flex flex-col items-center gap-4 p-4">
                <div style={{minHeight: 320, width: '100%'}} className="flex justify-center items-center">
                    {(isShuffling || selectedIndex !== null) && currentSelectedPlace && (
                        <CSSTransition
                            in={!!currentSelectedPlace}
                            timeout={400}
                            classNames={isShuffling ? 'shake-fade-scale' : 'fade-scale'}
                            unmountOnExit
                            key={selectedIndex}
                        >
                            <div style={{width: 350, maxWidth: '95%'}}>
                                <PlaceCard
                                    place={currentSelectedPlace}
                                    handleCancelWish={handleCancelWishExtend}
                                />
                            </div>
                        </CSSTransition>
                    )}
                </div>

                <KakaoMap
                    centerPlaceInfo={mapInfo.centerPlaceInfo}
                    selectedPlacesInfo={mapInfo.selectedPlacesInfo}
                    placesInfos={mapInfo.placesInfos}
                    isShuffling={isShuffling}
                    user={UserStore.user || undefined}
                />

                <button
                    className="btn-random-choice"
                    onClick={handleStartShuffle}
                    disabled={isShuffling || !localPlaces.length}
                >
                    {isShuffling ? "셔플 중..." : "한번 더?!"}
                </button>
            </div>
        </CommonModal>
    );
});

export default RandomChoiceModal;
