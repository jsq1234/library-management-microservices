export interface SignInResponse {
  authenticationResults: {
    idToken: string;
    refreshToken: string;
    accessToken: string;
    expiresIn: number;
  };
  email: string;
  userId: string;
  phoneNumber: string;
  role: string;
}
