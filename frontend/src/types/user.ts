export interface UserResponse {
    userDTO: User;
    jwtToken: string;
}

export interface User {
    userNo?: string;
    email: string;
    nickName: string;
    statusCode: UserStatusCode;
    typeCode: UserTypeCode;
    imgPath?: string;
    lastLoginTime?: string;
    regTime: string;
    modTime: string;
}

type UserStatusCode = 'ACTIVE' | 'INACTIVE' | 'DELETED';

type UserTypeCode = 'KAKAO'


