import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service'  // Zorg ervoor dat je AuthService importeert
import { FormsModule } from '@angular/forms';  // Nodig voor ngModel

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],  // Importeren van FormsModule voor ngModel
  templateUrl: './login.component.html',  // Verwijst naar login.component.html
  styleUrls: ['./login.component.css']  // Stijlbestanden
})
export class LoginComponent {
  name: string = '';
  role: string = 'gebruiker';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    if (this.name && this.role) {
      this.authService.login(this.name, this.role);
      this.router.navigate(['/posts']);  // Verwijst naar de posts route na inloggen
    }
  }
}
