import type { Place } from '../types/place';
import type { Location } from '../types/location';
import type { MarkerInfo, PlaceMarkerInfo } from '../types/map';

/**
 * 지도 관련 정보를 생성하는 유틸리티 함수
 */
export const createMapInfo = (currentLocation: Location | null, selectedPlace: Place | null, places: Place[]) => {
    const centerPlaceInfo: MarkerInfo | undefined = currentLocation ? {
        title: '나의 현재 위치',
        location: {
            lng: currentLocation?.x ? Number(currentLocation.x) : undefined,
            lat: currentLocation?.y ? Number(currentLocation.y) : undefined,
        },
        type: 'me' as const,
    } : undefined;

    const selectedPlacesInfo: PlaceMarkerInfo | undefined = selectedPlace ? {
        type: 'selectedPlace' as const,
        place: selectedPlace
    } : undefined;

    const placesInfos: PlaceMarkerInfo[] = places.map(place => ({
        type: 'place' as const,
        place: place
    }));

    return {
        centerPlaceInfo,
        selectedPlacesInfo,
        placesInfos
    };
};
