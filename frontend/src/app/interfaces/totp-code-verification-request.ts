export interface TotpCodeVerificationRequest {
  session: string;
  email: string;
  code: string;
}
