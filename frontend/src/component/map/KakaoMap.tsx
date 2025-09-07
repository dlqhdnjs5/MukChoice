import {CustomOverlayMap, Map, useKakaoLoader} from "react-kakao-maps-sdk"
import {useMemo} from "react";
import type {MarkerInfo, PlaceMarkerInfo} from "../../types/map";
import type {User} from "../../types/user.ts";
import { CATEGORIES_INFO } from "../../types/categories";

interface Props {
    centerPlaceInfo?: MarkerInfo | undefined
    selectedPlacesInfo?: PlaceMarkerInfo | undefined
    placesInfos?: PlaceMarkerInfo[];
    isShuffling?: boolean;
    user?: User
}

const KakaoMap = (props: Props) => {
    useKakaoLoader({appkey: import.meta.env.VITE_KAKAO_JS_KEY});
    const {centerPlaceInfo, selectedPlacesInfo, placesInfos, user} = props;
    const markers = useMemo(() => {
        const allMarkers: (MarkerInfo | PlaceMarkerInfo)[] = [
            ...(centerPlaceInfo ? [centerPlaceInfo] : []),
            ...(selectedPlacesInfo ? [selectedPlacesInfo] : []),
            ...((placesInfos ?? []).filter(
                placeMarker =>
                    !(
                        selectedPlacesInfo &&
                        placeMarker.place.x === selectedPlacesInfo.place.x &&
                        placeMarker.place.y === selectedPlacesInfo.place.y
                    )
            ))
        ];
        return allMarkers;
    }, [centerPlaceInfo, selectedPlacesInfo, placesInfos]);

    const center = useMemo(() => {
        if (selectedPlacesInfo) {
            if (props.isShuffling) {
                return {
                    lat: centerPlaceInfo?.location?.lat ?? 0,
                    lng: centerPlaceInfo?.location?.lng ?? 0,
                };
            }

            return {
                lat: parseFloat(selectedPlacesInfo.place.y) ?? 0,
                lng: parseFloat(selectedPlacesInfo.place.x) ?? 0,
            };
        } else if (centerPlaceInfo && centerPlaceInfo.location.lat !== undefined && centerPlaceInfo.location.lng !== undefined) {
            return {
                lat: centerPlaceInfo.location.lat ?? 0,
                lng: centerPlaceInfo.location.lng ?? 0,
            };
        }
        return {lat: 0, lng: 0};
    }, [centerPlaceInfo, selectedPlacesInfo, props.isShuffling]);

    return (
        <div className="kakao-map-large">
            {(centerPlaceInfo || selectedPlacesInfo) && markers.length > 0 ? (
                <div style={{width: '100%', minWidth: 320, maxWidth: 1000, margin: '0 auto', height: 350}}>
                    <Map
                        id="map"
                        center={center}
                        style={{
                            width: '100%',
                            height: '100%',
                            minWidth: 320,
                            minHeight: 200,
                        }}
                        level={2}
                        isPanto={true}
                    >
                        {markers.map((marker, index) => {
                            // MarkerInfo 타입인지 PlaceMarkerInfo 타입인지 구분
                            const isMarkerInfo = 'location' in marker;
                            if (isMarkerInfo && marker.type === 'me') {
                                // 사용자 마커 - 프로필 이미지 사용 (MarkerInfo)
                                return (
                                    <CustomOverlayMap
                                        key={index}
                                        position={{
                                            lat: marker.location?.lat ?? 0,
                                            lng: marker.location?.lng ?? 0,
                                        }}
                                    >
                                        <div className="flex flex-col items-center">
                                            <div className="relative">
                                                <div className="w-12 h-12 rounded-full border-3 border-blue-500 overflow-hidden shadow-lg bg-white">
                                                    {user?.imgPath ? (
                                                        <img
                                                            src={user.imgPath}
                                                            alt={user.nickName || '나'}
                                                            className="w-full h-full object-cover"
                                                        />
                                                    ) : (
                                                        <div className="w-full h-full bg-blue-500 flex items-center justify-center text-white font-bold text-lg">
                                                            {user?.nickName?.charAt(0)?.toUpperCase() || '나'}
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-2 border-r-2 border-t-4 border-l-transparent border-r-transparent border-t-blue-500"></div>
                                            </div>
                                            <div className="mt-1 px-2 py-1 bg-blue-500 text-white text-xs rounded-full font-semibold shadow-md">
                                                {user?.nickName || '나'}
                                            </div>
                                        </div>
                                    </CustomOverlayMap>
                                );
                            } else if (!isMarkerInfo && marker.type === 'selectedPlace') {
                                return (
                                    <CustomOverlayMap
                                        key={index}
                                        position={{
                                            lat: parseFloat(marker.place.y) ?? 0,
                                            lng: parseFloat(marker.place.x) ?? 0,
                                        }}
                                        zIndex={1000}
                                    >
                                        <div className="flex flex-col items-center cursor-pointer"
                                             onClick={() => {
                                                 if (marker.place.placeUrl) {
                                                     window.open(marker.place.placeUrl, '_blank');
                                                 }
                                             }}>
                                            <div className="relative">
                                                <div className="bg-white border-2 border-yellow-400 px-2 py-1.5 rounded-md shadow-xl text-xs max-w-28 text-center hover:bg-yellow-50 transition-colors">
                                                    <div className="font-medium text-yellow-600 whitespace-nowrap overflow-hidden text-ellipsis">
                                                        {marker.place.placeName || '가게'}
                                                    </div>
                                                    <div className="font-normal text-gray-600 text-xs mt-0.5 whitespace-nowrap overflow-hidden text-ellipsis">
                                                        {CATEGORIES_INFO.find(item => item.name === marker.place.placeCategory)?.displayName || marker.place.categoryGroupName || ''}
                                                    </div>
                                                </div>
                                                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-2 border-r-2 border-t-3 border-l-transparent border-r-transparent border-t-white"></div>
                                            </div>
                                        </div>
                                    </CustomOverlayMap>
                                );
                            } else if (!isMarkerInfo) {
                                return (
                                    <CustomOverlayMap
                                        key={index}
                                        position={{
                                            lat: parseFloat(marker.place.y) ?? 0,
                                            lng: parseFloat(marker.place.x) ?? 0,
                                        }}
                                        zIndex={100}
                                    >
                                        <div className="flex flex-col items-center cursor-pointer hover:z-50 relative group"
                                             onClick={() => {
                                                 if (marker.place.placeUrl) {
                                                     window.open(marker.place.placeUrl, '_blank');
                                                 }
                                             }}>
                                            <div className="relative">
                                                <div className="bg-white border-2 border-[#ff5e62] px-2 py-1.5 rounded-md shadow-lg text-xs max-w-28 text-center hover:bg-[#fff5f5] hover:shadow-xl hover:scale-105 transition-all duration-200 group-hover:z-50">
                                                    <div className="font-medium text-[#ff5e62] whitespace-nowrap overflow-hidden text-ellipsis">
                                                        {marker.place.placeName || '가게'}
                                                    </div>
                                                    <div className="font-normal text-gray-600 text-xs mt-0.5 whitespace-nowrap overflow-hidden text-ellipsis">
                                                        {CATEGORIES_INFO.find(item => item.name === marker.place.placeCategory)?.displayName || marker.place.categoryGroupName || ''}
                                                    </div>
                                                </div>
                                                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-2 border-r-2 border-t-3 border-l-transparent border-r-transparent border-t-white"></div>
                                            </div>
                                        </div>
                                    </CustomOverlayMap>
                                );
                            }
                            return null;
                        })}
                    </Map>
                </div>
            ) : null}
        </div>
    )
}

export default KakaoMap;