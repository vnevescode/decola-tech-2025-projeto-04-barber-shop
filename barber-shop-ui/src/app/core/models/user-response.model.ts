export interface UserResponse {
  id: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN';
}
