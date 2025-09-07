import type {ReactNode} from "react";
import {createPortal} from "react-dom";

interface CommonModalProps {
    open: boolean;
    onClose: () => void;
    children: ReactNode;
    className?: string;
}

const CommonModal = ({open, onClose, children, className = ""}: CommonModalProps) => {
    if (!open) return null;
    return createPortal(
        <div
            className={`fixed inset-0 z-[5000] transition-all duration-300 pointer-events-auto`}
            aria-modal="true"
            role="dialog"
        >
            {/* 오버레이 */}
            <div
                className="absolute inset-0 bg-black bg-opacity-40 transition-opacity duration-300 opacity-100 pointer-events-auto"
                onClick={onClose}
            />
            {/* 모달 컨텐츠 */}
            <div
                className={`absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-2xl shadow-xl transition-transform duration-300 scale-100 ${className}`}
                style={{minWidth: 320, minHeight: 120, maxWidth: '90vw', maxHeight: '90vh', overflow: 'auto'}}
            >
                {/* 닫기 버튼 */}
                {/*<button
                    className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 text-2xl font-bold bg-white rounded-full p-1 shadow focus:outline-none"
                    style={{zIndex: 10}}
                    onClick={onClose}
                    aria-label="닫기"
                >
                    ×
                </button>*/}
                {children}
            </div>
        </div>,
        document.body
    );
};

export default CommonModal;
