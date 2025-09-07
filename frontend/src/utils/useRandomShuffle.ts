import { useEffect, useRef, useState } from 'react';
import confetti from 'canvas-confetti';
import { defaultConfettiOptions } from './confettiOptions';
import type { Place } from '../types/place';

/**
 * 랜덤 선택 셔플 로직을 처리하는 커스텀 훅
 */
export const useRandomShuffle = (places: Place[]) => {
    const [index, setIndex] = useState<number | null>(null);
    const [isShuffling, setIsShuffling] = useState(false);
    const timerRef = useRef<number | null>(null);

    // 타이머 정리 함수
    const clearShuffleTimer = () => {
        if (timerRef.current) {
            window.clearInterval(timerRef.current);
            timerRef.current = null;
        }
    };

    const resetShuffle = () => {
        setIndex(null);
        setIsShuffling(false);
        clearShuffleTimer();
    };

    const startShuffle = () => {
        if (!places.length) return false;

        setIsShuffling(true);
        setIndex(null);

        let count = 0;
        const shuffleDuration = 1500;
        const interval = 70;
        const maxCount = Math.floor(shuffleDuration / interval);

        clearShuffleTimer();

        timerRef.current = window.setInterval(() => {
            setIndex(Math.floor(Math.random() * places.length));
            count++;

            if (count > maxCount) {
                clearShuffleTimer();

                // 최종 랜덤 인덱스 선택
                const finalIndex = Math.floor(Math.random() * places.length);
                setIndex(finalIndex);
                setIsShuffling(false);

                setTimeout(() => {
                    confetti(defaultConfettiOptions);
                }, 300);
            }
        }, interval);

        return true;
    };

    // 컴포넌트 언마운트 시 타이머 정리
    useEffect(() => {
        return clearShuffleTimer;
    }, []);

    return {
        selectedIndex: index,
        isShuffling,
        startShuffle,
        resetShuffle,
        clearShuffleTimer,
        setIndex
    };
};
