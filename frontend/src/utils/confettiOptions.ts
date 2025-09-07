// 공통 confetti 옵션 및 함수
type ConfettiOptions = {
    particleCount?: number;
    spread?: number;
    origin?: { y: number };
    zIndex?: number;
};

export const defaultConfettiOptions: ConfettiOptions = {
    particleCount: 120,
    spread: 90,
    origin: { y: 0.7 },
    zIndex: 9999,
};

