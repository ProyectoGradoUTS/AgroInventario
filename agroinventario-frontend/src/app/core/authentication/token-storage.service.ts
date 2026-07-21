import { Injectable, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { SessionUser } from '../models';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly tokenKey = environment.tokenStorageKey;
  private readonly userKey = environment.userStorageKey;

  readonly token = signal<string | null>(this.readToken());
  readonly user = signal<SessionUser | null>(this.readUser());

  getToken(): string | null {
    return this.token();
  }

  setSession(token: string, user: SessionUser): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.token.set(token);
    this.user.set(user);
  }

  updateUser(user: SessionUser): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.user.set(user);
  }

  clear(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.token.set(null);
    this.user.set(null);
  }

  private readToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private readUser(): SessionUser | null {
    const raw = localStorage.getItem(this.userKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as SessionUser;
    } catch {
      return null;
    }
  }
}
