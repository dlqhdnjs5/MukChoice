export type CATEGORIES = "KOREAN_FOOD" | "JAPANESE_FOOD" | "CHINESE_FOOD" | "WESTERN_FOOD" | "ASIAN_FOOD" | "FAST_FOOD" | "IZAKAYA" | "BAR" | "SNACK_FOOD" | "ETC" | "ALL";

export const CATEGORIES_INFO: { name: CATEGORIES; displayName: string }[] = [
    { name: "KOREAN_FOOD", displayName: "한식" },
    { name: "JAPANESE_FOOD", displayName: "일식" },
    { name: "CHINESE_FOOD", displayName: "중식" },
    { name: "WESTERN_FOOD", displayName: "양식" },
    { name: "ASIAN_FOOD", displayName: "아시아음식" },
    { name: "BAR", displayName: "술집" },
    { name: "SNACK_FOOD", displayName: "분식" },
    { name: "FAST_FOOD", displayName: "패스트푸드" },
    { name: "IZAKAYA", displayName: "이자카야" },
    { name: "ETC", displayName: "기타" },
    { name: "ALL", displayName: "전체" },
];