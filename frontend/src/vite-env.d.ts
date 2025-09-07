/// <reference types="vite/client" />


interface ImportMetaEnv {
    readonly VITE_KAKAO_REST_API_KEY: string;
    readonly VITE_KAKAO_LOGIN_REDIRECT_URI: string;
    readonly VITE_MUKCHOICE_X_TOKEN: string;
    readonly VITE_KAKAO_JS_KEY: string;
    readonly VITE_API_BASE_URL: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
