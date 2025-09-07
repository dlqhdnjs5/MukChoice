import {observer} from "mobx-react-lite";
import {useEffect, useState} from "react";
import DaumPostcodeEmbed, {type Address} from "react-daum-postcode";
import {useAddLocation, useFetchLocationQuery, usePickLocation, useRemoveLocation} from "../api/query/useLocation.ts";
import LocationStore from "../store/LocationStore.ts";
import type {Location} from "../types/location.ts";
import CommonModal from "./ui/CommonModal.tsx";
import Spinner from "./ui/Spinner.tsx";

interface TopNavProps {
    onLocationClick?: () => void;
}

const TopNav = observer((props: TopNavProps) => {
    const [openLocationSet, setOpenLocationSet] = useState(false);
    const [postcodeModalOpen, setPostcodeModalOpen] = useState(false);
    const addLocationMutation = useAddLocation();
    const pickLocationMutation = usePickLocation()
    const removeLocationMutation = useRemoveLocation();
    const {data, isLoading} = useFetchLocationQuery();
    const locations: Location[] = data?.locations || [];
    const handleClickLocation = (data: Address) => {
        let address: string
        if (data.userSelectedType === 'R') {
            address = data.roadAddress
        } else {
            address = data.jibunAddress;
        }


        setPostcodeModalOpen(false);
        setOpenLocationSet(false)
        addLocationMutation.mutate({address: address});
    }

    const handlePickLocation = (location: Location) => {
        pickLocationMutation.mutate({selectedLocationNo: location.locationNo});
        LocationStore.setCurrentLocation(location);
        setPostcodeModalOpen(false);
        setOpenLocationSet(false)
    }

    const handleRemoveLocation = (location: Location) => {
        if (location.locationNo !== undefined) {
            removeLocationMutation.mutate(location.locationNo);
        }
        LocationStore.setCurrentLocation(null);
        setPostcodeModalOpen(false);
        setOpenLocationSet(false);
    }

    // 로그인 이후 선택한 location 초기화
    useEffect(() => {
        if (LocationStore.currentLocation != null) return;

        const selectedLocation = locations.find(loc => loc.isSelected) || null;
        LocationStore.setCurrentLocation(selectedLocation);
    }, [locations]);

    return (
        <>
            <nav
                className="fixed top-0 left-0 right-0 h-16 bg-white shadow flex items-center justify-between px-6 z-50">
                <button
                    className="text-xl font-bold text-[#7c5e99] focus:outline-none"
                    onClick={() => setOpenLocationSet(true)}
                    style={{background: 'none', border: 'none', padding: 0, cursor: 'pointer'}}
                >
                    <span
                        style={{fontSize: '1rem'}}>{locations.find(loc => loc.isSelected)?.userAddress || "위치를 선택해 주세요"}</span>
                </button>
                <div className="flex items-center space-x-4">
                    {/*<button className="text-[#7c5e99] font-semibold hover:text-[#b96f3c]">메뉴1</button>
                    <button className="text-[#7c5e99] font-semibold hover:text-[#b96f3c]">메뉴2</button>*/}
                </div>
            </nav>
            {/* 슬라이드 레이어 */}
            <div
                className={`fixed inset-0 z-[5000] transition-all duration-300 ${openLocationSet ? 'pointer-events-auto' : 'pointer-events-none'}`}
            >
                {/* 오버레이 */}
                <div
                    className={`absolute inset-0 bg-black bg-opacity-40 transition-opacity duration-300 ${openLocationSet ? 'opacity-100' : 'opacity-0'}`}
                    onClick={() => setOpenLocationSet(false)}
                />
                {/* 슬라이드 패널 */}
                <div
                    className={`absolute left-1/2 top-0 -translate-x-1/2 w-full max-w-md bg-white rounded-b-2xl shadow-xl transition-transform duration-300 ${openLocationSet ? 'translate-y-0' : '-translate-y-full'}`}
                    style={{height: '70vh', maxHeight: '80vh', overflow: 'hidden'}}
                >
                    <div className="flex flex-col h-full">
                        <div className="flex-shrink-0 p-4 border-b flex items-center justify-between">
                            <span className="text-lg font-bold text-gray-500">위치를 선택해 주세요</span>
                            <button
                                className="text-gray-400 hover:text-gray-700 text-2xl"
                                onClick={() => setOpenLocationSet(false)}
                                style={{background: 'none', border: 'none', cursor: 'pointer'}}
                                aria-label="닫기"
                            >
                                ×
                            </button>
                        </div>
                        <button
                            className="m-4 px-4 py-2 bg-[#ff5e62] text-white rounded font-semibold"
                            onClick={() => setPostcodeModalOpen(true)}
                        >
                            위치 등록
                        </button>
                        <div className="flex-1 overflow-y-auto px-4 pb-4">
                            {isLoading ? (
                                <Spinner/>
                            ) : (
                                locations.map((loc, idx) => (
                                    <div
                                        key={idx}
                                        className="py-2 border-b last:border-b-0 text-[#7c5e99] flex items-center justify-between"
                                    >
                                        <button
                                            className="text-left w-full bg-transparent border-none p-0 m-0 text-[#7c5e99] hover:underline cursor-pointer"
                                            onClick={() => handlePickLocation(loc)}
                                        >
                                            {loc.addressName}
                                        </button>
                                        <button
                                            className="ml-2 text-gray-400 hover:text-red-500 text-lg"
                                            style={{background: 'none', border: 'none', cursor: 'pointer', padding: 0}}
                                            aria-label="삭제"
                                            onClick={() => handleRemoveLocation(loc)}
                                        > ×
                                        </button>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                </div>
            </div>
            <CommonModal open={postcodeModalOpen} onClose={() => setPostcodeModalOpen(false)}>
                {postcodeModalOpen && <DaumPostcodeEmbed onComplete={handleClickLocation}/>}
            </CommonModal>
        </>
    );
});

export default TopNav;

