import type { Place } from './place';

export interface MarkerInfo {
    title: string;
    location: {
        lat?: number;
        lng?: number;
    };
    type: 'me' | 'selectedPlace' | 'place';
    placeCategory?: string;
    placeUrl?: string;
    id?: string;
}

export interface PlaceMarkerInfo {
    type: 'selectedPlace' | 'place';
    place: Place;
}
