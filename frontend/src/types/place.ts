import type {CATEGORIES} from "./categories.ts";

export interface Place {
  id: string;
  placeName: string;
  categoryName: string;
  categoryGroupCode?: string;
  categoryGroupName?: string;
  phone?: string;
  addressName?: string;
  roadAddressName?: string;
  placeCategory?: CATEGORIES;
  x: string;
  y: string;
  bcode?: string;
  dong?: string;
  placeUrl: string;
  thumbnailUrl?: string;
  distance?: string;
  isWish?: boolean;
  totalCount?: number;
  isEnd?: boolean;
}

export interface FetchPlacesParams {
  coordinateX: string;
  coordinateY: string;
  query?: string;
  page?: number;
}

export interface FetchPlacesMultiCategoryParams {
  coordinateX: string;
  coordinateY: string;
  categories?: string[]; // 여러 카테고리
  page?: number;
}

export interface WishListItem {
  userNo: number;
  placeId: number;
  regTime?: string;
  place?: Place;
}

export interface WishListResponse {
  wishList: WishListItem[];
  total: number;
}

export interface WishDongInfo {
    dong: string;
    bcode: string;
}

export interface WishDongInfoResponse {
    dongInfos?: WishDongInfo[];
}
