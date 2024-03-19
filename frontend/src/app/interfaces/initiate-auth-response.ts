export interface InitiateAuthResponse {
  mfaTokens: {
    accessToken: string;
    secretCodeUrl: string;
  };
  challengeName: string;
  session: string;
}
