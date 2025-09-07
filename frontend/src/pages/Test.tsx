import {observer} from 'mobx-react-lite';
import {appStore} from '../store/example/AppStore';
import {useHelloQuery} from '../api/query/useHelloQuery.ts';
import {fetchPlaces, fetchShopImageTest} from "../api/function/helloFunction.ts";
import {useKakaoLoader} from "react-kakao-maps-sdk";
import CommonModal from "../component/ui/CommonModal.tsx";
import React, {useState} from "react";
import {shareKakaoTalkInviteFriendCustom} from "../utils/kakaoSDK.ts";

// 환경변수 사용 예시
const restApiKey = import.meta.env.VITE_KAKAO_REST_API_KEY;

const Test = observer(() => {
    const {data, isLoading, error, refetch} = useHelloQuery();
    const [modalOpen, setModalOpen] = useState(false);

    const getShopImageApi = async () => {
        await fetchShopImageTest();
    }

    const getPlacesApi = async () => {
        await fetchPlaces()
    }
    useKakaoLoader({ appkey: 'cbc64aff7df2101300ed94fc9e8b0e5c' })

    return (
        <div>
            <div className="bg-blue-500 text-xl text-white p-4 rounded mb-4">
                Tailwind 테스트: 파란 배경, 큰 글씨
            </div>
            <div>Test 페이지입니다.</div>
            {/* store의 count 값 출력 */}
            <div>count: {appStore.count}</div>
            {/* 버튼 클릭 시 store의 increment 함수 호출 */}
            <button onClick={appStore.increment}>store count 증가</button>
            <hr style={{margin: '2em 0'}}/>
            {/* 버튼 클릭 시 /api/sample/hello 호출 */}
            <button onClick={() => refetch()}>
                /sample/hello API 호출
            </button>
            <button onClick={() => getShopImageApi()}>
                /sample/shopImage API 호출
            </button>
            <button onClick={() => getPlacesApi()}>
                /places API 호출
            </button>
            <button onClick={() => shareKakaoTalkInviteFriendCustom()}>
                공유하기
            </button>
            {isLoading && <div>로딩중...</div>}
            {error && <div style={{color: 'red'}}>에러: {String(error)}</div>}
            {data && <div>응답: {data}</div>}
            <hr style={{margin: '2em 0'}}/>
            <div>REST API KEY: {restApiKey}</div>

            <button className="lp-btn" onClick={() => setModalOpen(true)}>
                공통 모달 열기
            </button>
            <CommonModal open={modalOpen} onClose={() => setModalOpen(false)}>
                <div className="p-6 text-center bg-white rounded-2xl">
                    <h2 className="text-xl font-bold mb-4 text-black">공통 모달 예시</h2>
                    <p className="text-black">이곳에 원하는 내용을 자유롭게 넣을 수 있습니다.</p>
                    <button className="lp-btn mt-6" onClick={() => setModalOpen(false)}>
                        닫기
                    </button>
                </div>
            </CommonModal>
        </div>
    );
});

export default Test;

