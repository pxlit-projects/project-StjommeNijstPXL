// login.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login and navigate when form is valid', () => {
    component.name = 'testUser';
    component.role = 'gebruiker';
    
    component.login();

    expect(authService.login).toHaveBeenCalledWith('testUser', 'gebruiker');
    expect(router.navigate).toHaveBeenCalledWith(['/posts']);
  });

  it('should not call login when form is invalid', () => {
    component.name = '';
    component.role = 'gebruiker';
    
    component.login();

    expect(authService.login).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
