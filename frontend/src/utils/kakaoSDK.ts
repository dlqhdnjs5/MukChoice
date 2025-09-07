// 카카오 SDK 타입 정의
import {deleteCookie} from "./cookieUtils.ts";
import { createGroupInvitation, type CreateGroupInvitationRequest } from "../api/function/GroupFunc.ts";

declare global {
    interface Window {
        Kakao: any;
    }
}

// ���카오 SDK 동적 로드 함수
const loadKakaoSDK = (): Promise<void> => {
    return new Promise((resolve, reject) => {
        // 이미 로드된 경우
        if (window.Kakao) {
            resolve();
            return;
        }

        const script = document.createElement('script');
        script.src = 'https://t1.kakaocdn.net/kakao_js_sdk/2.7.5/kakao.min.js';
        script.integrity = import.meta.env.VITE_KAKAO_SDK_INTEGRITY;
        script.crossOrigin = 'anonymous';

        script.onload = () => {
            resolve();
        };

        script.onerror = () => {
            reject(new Error('카카오 SDK 로드 실패'));
        };

        document.head.appendChild(script);
    });
};

// 카카오 SDK 초기화 함수
export const initKakaoSDK = async (): Promise<boolean> => {
    try {
        // SDK 로드
        await loadKakaoSDK();

        if (!window.Kakao) {
            console.error('Kakao SDK가 로드되지 않았습니다.');
            return false;
        }

        const kakaoAppKey = import.meta.env.VITE_KAKAO_JS_KEY;

        if (!kakaoAppKey) {
            return false;
        }

        // 이미 초기화된 경우 재초기화하지 않음
        if (window.Kakao.isInitialized()) {
            return true;
        }

        window.Kakao.init(kakaoAppKey);
        return true;
    } catch (error) {
        console.error('Kakao SDK 초기화 실패:', error);
        return false;
    }
};

// 카카오 SDK 초기화 상태 확인
export const isKakaoSDKInitialized = (): boolean => {
    return window.Kakao && window.Kakao.isInitialized();
};

// 카카오 로그인 함수
export const kakaoLogin = () => {
    if (!isKakaoSDKInitialized()) {
        console.error('Kakao SDK가 초기화되지 않았습니다.');
        return;
    }

    window.Kakao.Auth.login({
        success: (authObj: any) => {
            console.log('카카오 로그인 성공:', authObj);
            // 로그인 성공 후 처리 로직
        },
        fail: (err: any) => {
            console.error('카카오 로그인 실패:', err);
        }
    });
};

// 카카오 로그아웃 함수
export const kakaoLogout = () => {
    if (!isKakaoSDKInitialized()) {
        console.error('Kakao SDK가 초기화되지 않았습니다.');
        return;
    }

    window.Kakao.Auth.logout()
        .then(function(response) {
            deleteCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN)
            console.log('카카오 로그아웃 완료', response);
        })
        .catch(function(error) {
            console.log('Not logged in.');
        });

    /*window.Kakao.Auth.logout(() => {
        deleteCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN)
        console.log('카카오 로그아웃 완료');
    });*/
};

// 사용자 정보 가져오기
export const getKakaoUserInfo = () => {
    if (!isKakaoSDKInitialized()) {
        console.error('Kakao SDK가 초기화되지 않았습니다.');
        return Promise.reject('SDK not initialized');
    }

    return new Promise((resolve, reject) => {
        window.Kakao.API.request({
            url: '/v2/user/me',
            success: (res: any) => {
                resolve(res);
            },
            fail: (error: any) => {
                reject(error);
            }
        });
    });
};

export const shareKakaoTalkInviteFriendCustom = async (groupId: number) => {
    if (!isKakaoSDKInitialized()) {
        alert('죄송합니다 카카오톡 공유 기능을 사용할 수 없습니다. 잠시 후 다시 시도해주세요.');
        return;
    }

    try {
        const invitationResponse = await createGroupInvitation({groupId});
        window.Kakao.Share.sendCustom({
            templateId: 122298,
            templateArgs: {
                invitationId: invitationResponse.id,
            }
        });


    } catch (error) {
        alert('초대 링크 생성에 실패했습니다. 다시 시도해주세요.');
    }
}

// 카카오톡 공유 함수 (기본 템플릿)
export const shareKakaoTalk = (options: {
    title: string;
    description: string;
    imageUrl?: string;
    webUrl?: string;
    mobileWebUrl?: string;
}) => {
    if (!isKakaoSDKInitialized()) {
        console.error('Kakao SDK가 초기화되지 않았습니다.');
        return;
    }

    window.Kakao.Share.sendDefault({
        objectType: 'feed',
        content: {
            title: options.title,
            description: options.description,
            imageUrl: options.imageUrl || '',
            link: {
                webUrl: options.webUrl || window.location.href,
                mobileWebUrl: options.mobileWebUrl || window.location.href
            }
        },
        buttons: [
            {
                title: '웹으로 보기',
                link: {
                    webUrl: options.webUrl || window.location.href,
                    mobileWebUrl: options.mobileWebUrl || window.location.href
                }
            }
        ]
    });
};

// 맛집 정보 카카오톡 공유 함수
export const shareRestaurant = (restaurant: {
    name: string;
    address: string;
    category: string;
    imageUrl?: string;
    webUrl?: string;
}) => {
    shareKakaoTalk({
        title: `맛집 추천: ${restaurant.name}`,
        description: `${restaurant.category} | ${restaurant.address}`,
        imageUrl: restaurant.imageUrl,
        webUrl: restaurant.webUrl
    });
};
