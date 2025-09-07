import { useNavigate } from "react-router-dom";
import { useKakaoLogout } from "../../api/query/useAuth.ts";
import { deleteCookie } from "../../utils/cookieUtils.ts";
import UserStore from "../../store/UserStore.ts";
import { observer } from "mobx-react-lite";

const My = observer(() => {
    const navigate = useNavigate();
    const kakaoLogoutMutation = useKakaoLogout();
    const { user } = UserStore;

    const handleLogout = async () => {
        if (!confirm('로그아웃하시겠습니까?')) {
            return;
        }

        try {
            await kakaoLogoutMutation.mutateAsync();
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
        } finally {
            UserStore.logout()
            deleteCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN);
            alert('로그아웃 되었습니다.')
            navigate('/login');
        }
    };

    return (
        <div className="p-4">
            <div className="max-w-md mx-auto">
                <h1 className="text-2xl font-bold mb-6 text-gray-800">마이페이지</h1>

                <div className="space-y-4">
                    <div className="bg-white rounded-lg shadow p-4">
                        <div className="flex items-center space-x-4 mb-4">
                            <div className="w-16 h-16 rounded-full overflow-hidden bg-gray-200 flex items-center justify-center">
                                {user?.imgPath ? (
                                    <img
                                        src={user.imgPath}
                                        alt={user.nickName || '사용자'}
                                        className="w-full h-full object-cover"
                                    />
                                ) : (
                                    <div className="w-full h-full bg-blue-500 flex items-center justify-center text-white font-bold text-xl">
                                        {user?.nickName?.charAt(0)?.toUpperCase() || '나'}
                                    </div>
                                )}
                            </div>
                            <div className="flex-1">
                                <p className="text-gray-700 font-medium">{user?.nickName || '사용자'}</p>
                                <p className="text-gray-700">{user?.email || '정보 없음'}</p>
                            </div>
                        </div>
                    </div>

                    {/* 설정 섹션 */}
                    {/*<div className="bg-white rounded-lg shadow p-4">
                        <h2 className="text-lg font-semibold mb-3 text-gray-900">설정</h2>
                        <div className="space-y-3">
                            <button className="w-full text-left p-3 hover:bg-gray-800 rounded-lg border bg-black text-white">
                                개인정보 처리방침
                            </button>
                            <button className="w-full text-left p-3 hover:bg-gray-800 rounded-lg border bg-black text-white">
                                서비스 이용약관
                            </button>
                        </div>
                    </div>*/}

                    {/* 로그아웃 버튼 */}
                    <div className="bg-white rounded-lg shadow p-4">
                        <button
                            onClick={handleLogout}
                            disabled={kakaoLogoutMutation.isPending}
                            className="w-full bg-red-500 hover:bg-red-600 disabled:bg-red-300 text-white font-semibold py-3 px-4 rounded-lg transition-colors"
                        >
                            {kakaoLogoutMutation.isPending ? '로그아웃 중...' : '로그아웃'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
});

export default My;
