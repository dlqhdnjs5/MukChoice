import {createPortal} from "react-dom";

interface Props {
    onClick?: () => void
}

const FloatingButton = (props: Props) =>
    createPortal(
        <button
            className="fixed z-[1] left-1/2 -translate-x-1/2 bottom-24
                   bg-gradient-to-br bg-[#ff5e62]
                   shadow-2xl rounded-full p-3 flex items-center justify-center
                   text-white text-lg font-bold hover:scale-110 active:scale-95
                   transition-all duration-200 border-2 border-white drop-shadow-xl"
            style={{
                boxShadow: '0 8px 24px rgba(0,0,0,0.18), 0 1.5px 4px rgba(255, 180, 80, 0.25)',
            }}
            onClick={props.onClick}
        >
            랜덤 맛집 선택
        </button>,
        document.querySelector('.main-container-wrapper') as HTMLElement
    )
;

export default FloatingButton;