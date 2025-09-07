import {forwardRef, useEffect, useState} from "react";
import type {Place} from "../../types/place.ts";
import {useAddWishList} from "../../api/query/usePlace";
import {observer} from "mobx-react-lite";
import { CATEGORIES_INFO } from "../../types/categories.ts";
import AddToGroupModal from "./AddToGroupModal";
import MoreActionsMenu from "../ui/MoreActionsMenu";

interface PlaceCardProps {
    place: Place;
    handleCancelWish?: (placeId: string) => void;
}

const PlaceCard = observer(forwardRef<HTMLDivElement, PlaceCardProps>(({place, handleCancelWish}, ref) => {
    const FAR_DISTANCE = 2000; // 2km
    const [liked, setLiked] = useState(place?.isWish ?? false);
    const [showGroupModal, setShowGroupModal] = useState(false);
    const addWishList = useAddWishList();

    useEffect(() => {
        setLiked(place?.isWish ?? false)
    }, [place?.isWish]);

    const categoryDisplayName = place.placeCategory && place.placeCategory !== "ALL"
        ? CATEGORIES_INFO.find(c => c.name === place.placeCategory)?.displayName
        : undefined;

    const getDisplayDistance = (distance?: string) => {
        if (!distance) return null;
        const num = Number(distance);
        if (isNaN(num)) return null;
        if (num >= 1000) {
            return `${(num / 1000).toFixed(1)}km`;
        }
        return `${num}m`;
    };

    const handleLikeClick = () => {
        const prevLiked = liked;
        setLiked(!prevLiked);

        addWishList.mutate({
            x: place.x,
            y: place.y,
            placeId: place.id,
            placeName: place.placeName,
            placeCategory: place.placeCategory,
            isWish: !prevLiked,
        }, {
            onSuccess: () => {
                // 좋아요 취소일경우 실행.
                if (prevLiked && handleCancelWish) {
                    handleCancelWish(place.id);
                }
            }
        });
    };

    return (
        <div
            ref={ref}
            className="shadow-lg rounded-xl p-5 bg-white mb-6 flex flex-col gap-2 transition-all overflow-hidden hover:scale-105 hover:shadow-2xl">

            <MoreActionsMenu onAddToGroup={() => setShowGroupModal(true)} />

            <div className="flex flex-col gap-1">
                <a href={place.placeUrl} target="_blank" rel="noopener noreferrer">
                    <h2 className="text-lg font-bold text-blue-700 hover:underline">{place.placeName}</h2>
                </a>
                <span className="text-gray-500 text-sm">{place.phone || "전화번호가 없네요"}</span>
                <span className="text-gray-700 text-sm">{place.addressName || "주소 정보가 없네요"}</span>
                {place.distance && (
                  <>
                    <span className="text-xs text-purple-500 font-semibold">거리: {getDisplayDistance(place.distance)}</span>
                    {Number(place.distance) > FAR_DISTANCE && (
                      <span className="text-xs text-purple-300 font-semibold">거리가 너무 멀어요</span>
                    )}
                  </>
                )}
                {categoryDisplayName && (
                    <div className="text-xs text-gray-500 mb-1">{categoryDisplayName}</div>
                )}
            </div>
            <button
                className="self-end transition-colors mt-1 p-0 bg-transparent border-none outline-none"
                style={{lineHeight: 1, background: "none"}}
                title="좋아요"
                onClick={handleLikeClick}
                aria-label="좋아요"
            >
                <svg
                    width="26"
                    height="26"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M12 21c-.6 0-1.2-.2-1.7-.6C6.1 17.2 3 14.4 3 11.2 3 8.7 5 7 7.2 7c1.2 0 2.3.6 3 1.6C11.5 7.6 12.6 7 13.8 7 16 7 18 8.7 18 11.2c0 3.2-3.1 6-7.3 9.2-.5.4-1.1.6-1.7.6z"
                        fill={liked ? "#ff5e62" : "none"}
                        stroke="#ff5e62"
                        strokeWidth="2"
                        style={{transition: 'fill 0.2s'}}
                    />
                </svg>
            </button>

            <AddToGroupModal
                isOpen={showGroupModal}
                onClose={() => setShowGroupModal(false)}
                place={{
                    placeId: place.id,
                    placeName: place.placeName,
                    x: Number(place.x),
                    y: Number(place.y),
                    placeCategory: place.placeCategory || 'ALL',
                }}
            />
        </div>
    );
}));

export default PlaceCard;
