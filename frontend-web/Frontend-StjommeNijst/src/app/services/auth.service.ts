import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private name: string = '';
  private role: string = '';

  constructor() {}

  login(name: string, role: string): void {
    this.name = name;
    this.role = role;

    // Sla de naam en rol op in sessionStorage voor persistentie
    sessionStorage.setItem('userName', this.name);
    sessionStorage.setItem('userRole', this.role);
  }

  getUserName(): string {
    return sessionStorage.getItem('userName') || '';
  }

  getUserRole(): string {
    return sessionStorage.getItem('userRole') || '';
  }

  isRedacteur(): boolean {
    return this.getUserRole() === 'redacteur';
  }
}
