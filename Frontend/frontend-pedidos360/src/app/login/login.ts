import {
  Component,
  OnInit
} from '@angular/core';

import {
  Router
} from '@angular/router';

import {
  MsalService
} from '@azure/msal-angular';

const BACKEND_SCOPE =
  'api://ff20868b-f364-439d-a658-8b800bbaf552/OT.create';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {

  constructor(
    private msal: MsalService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {

    const response =
      await this.msal.instance
        .handleRedirectPromise();

    if (response?.account) {

      this.msal.instance
        .setActiveAccount(
          response.account
        );
    }

    const accounts =
      this.msal.instance
        .getAllAccounts();

    if (accounts.length > 0) {

      if (
        !this.msal.instance
          .getActiveAccount()
      ) {

        this.msal.instance
          .setActiveAccount(
            accounts[0]
          );
      }

      await this.router.navigate([
        '/dashboard'
      ]);
    }
  }

  login(): void {

    this.msal.loginRedirect({

      scopes: [
        'openid',
        'profile',
        'email',
        BACKEND_SCOPE
      ]

    });
  }
}