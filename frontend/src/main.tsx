import {createRoot} from 'react-dom/client'
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter} from "react-router-dom";
import DevTools from "mobx-react-devtools";
import {appStore} from "./store/example/AppStore.ts";
import {initKakaoSDK} from "./utils/kakaoSDK.ts";
import App from "./App.tsx";
import './index.css';
import './App.css'
import './styles/card.css';
import './styles/map.css';


initKakaoSDK().then(success => {
    if (!success) {
        console.error('카카오 SDK 초기화 실패');
    }
});

declare global {
    interface Window {
        appStore: typeof appStore
    }
}

const queryClient = new QueryClient()
window.appStore = appStore

createRoot(document.getElementById('root')!).render(
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <DevTools/>
                <App/>
            </BrowserRouter>
        </QueryClientProvider>
)



