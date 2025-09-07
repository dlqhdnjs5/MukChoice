export interface LocationAddress {
    postNo: string
    bcode: string
    bname: string
    sigunguCode: string
    userAddress: string
}

export interface Location {
    locationNo?: number;
    userNo: number;
    addressName: string;
    x: number;
    y: number;
    hcode?: string | null;
    bcode?: string | null;
    postNo?: string | null;
    sigungu?: string | null;
    sido?: string | null;
    dong?: string | null;
    userAddress?: string | null;
    isSelected?: boolean;
    regTime: string;
    modTime: string;
}

