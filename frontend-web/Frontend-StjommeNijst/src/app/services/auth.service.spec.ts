// auth.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store user credentials on login', () => {
    service.login('testUser', 'redacteur');
    expect(service.getUserName()).toBe('testUser');
    expect(service.getUserRole()).toBe('redacteur');
  });

  it('should check if user is redacteur', () => {
    service.login('testUser', 'redacteur');
    expect(service.isRedacteur()).toBe(true);
  });
});
