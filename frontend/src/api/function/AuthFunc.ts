import { getApi } from "./index.ts";

// 카카오 로그아웃 API
export const kakaoLogout = async () => {
    try {
        const response = await getApi('/api/oauth/kakao-logout');
        return response.data;
    } catch (error) {
        console.error('카카오 로그아웃 실패:', error);
        throw error;
    }
};
