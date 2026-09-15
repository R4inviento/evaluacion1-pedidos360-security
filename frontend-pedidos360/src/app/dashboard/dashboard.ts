import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  MsalService
} from '@azure/msal-angular';


@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard
  implements OnInit {

  graphName =
    signal('');

  graphEmail =
    signal('');

  graphStatus =
    signal(
      'Consultando Microsoft Graph...'
    );

  backendStatus =
    signal(
      'Backend todavía no consultado'
    );


  constructor(
    private msal: MsalService,
    private http: HttpClient
  ) {}


  ngOnInit(): void {

    this.cargarPerfil();
  }


  get userName(): string {

    const account =
      this.msal.instance
        .getActiveAccount()
      ??
      this.msal.instance
        .getAllAccounts()[0];

    return (
      account?.name
      ??
      account?.username
      ??
      'Usuario'
    );
  }


  cargarPerfil(): void {

    this.http
      .get<any>(
        'https://graph.microsoft.com/v1.0/me'
      )
      .subscribe({

        next: (perfil) => {

          this.graphName.set(
            perfil.displayName
            ??
            'Sin nombre'
          );


          this.graphEmail.set(
            perfil.mail
            ??
            perfil.userPrincipalName
            ??
            'Sin correo'
          );


          this.graphStatus.set(
            'Microsoft Graph consultado correctamente'
          );
        },


        error: (error) => {

          console.error(
            'Error Microsoft Graph:',
            error
          );

          this.graphStatus.set(
            'Error al consultar Microsoft Graph'
          );
        }

      });
  }


  probarBackend(): void {

    this.backendStatus.set(
      'Consultando backend protegido...'
    );


    this.http
      .get<any>(
        'http://localhost:8080/api/orders/create'
      )
      .subscribe({

        next: (respuesta) => {

          this.backendStatus.set(
            respuesta.mensaje
          );
        },


        error: (error) => {

          console.error(
            'Error Backend:',
            error
          );

          this.backendStatus.set(
            `Error backend: ${error.status}`
          );
        }

      });
  }


  logout(): void {

    this.msal.logoutRedirect({

      postLogoutRedirectUri:
        'http://localhost:4200'

    });
  }
}