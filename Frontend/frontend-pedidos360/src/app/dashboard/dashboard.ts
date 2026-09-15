import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  HttpClient,
  HttpErrorResponse
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
export class Dashboard implements OnInit {

  private readonly apiBase =
    'https://h8kncndcyj.execute-api.us-east-1.amazonaws.com/desarrollo/api';

  private readonly ordersApi =
    `${this.apiBase}/orders`;

  private readonly productsApi =
    `${this.apiBase}/products`;

  private readonly reportsApi =
    `${this.apiBase}/reports/summary`;

  graphName = signal('');
  graphEmail = signal('');

  userRole = signal('Sin rol');
  userRoles = signal<string[]>([]);

  ordersStatus = signal(
    'Pedidos todavía no consultados'
  );

  ordersJson = signal('');

  createStatus = signal(
    'Todavía no se ha creado un pedido desde Angular'
  );

  catalogStatus = signal(
    'Catálogo todavía no consultado'
  );

  products = signal<any[]>([]);

  productActionStatus = signal(
    'Sin operaciones de catálogo'
  );

  reportStatus = signal(
    'Reporte todavía no consultado'
  );

  reportJson = signal('');

  // ==========================================================
  // PRUEBA DE SEGURIDAD
  // ==========================================================

  securityStatus = signal(
    'Prueba de autorización todavía no ejecutada'
  );

  constructor(
    private msal: MsalService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.cargarPerfil();
    this.cargarRol();
  }

  get userName(): string {

    const account =
      this.msal.instance.getActiveAccount()
      ??
      this.msal.instance.getAllAccounts()[0];

    return (
      account?.name
      ??
      account?.username
      ??
      'Usuario'
    );
  }

  esAdmin(): boolean {
    return this.userRoles().includes('Admin');
  }

  esOperator(): boolean {
    return this.userRoles().includes('Operator');
  }

  puedeGestionarCatalogo(): boolean {
    return this.esAdmin() || this.esOperator();
  }

  puedeVerReportes(): boolean {
    return this.esAdmin() || this.esOperator();
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
        },

        error: (error) => {

          console.error(
            'Error Microsoft Graph:',
            error
          );
        }

      });
  }

  async cargarRol(): Promise<void> {

    const account =
      this.msal.instance.getActiveAccount()
      ??
      this.msal.instance.getAllAccounts()[0];

    if (!account) {
      return;
    }

    try {

      const response =
        await this.msal.instance
          .acquireTokenSilent({

            account,

            scopes: [
              'api://ff20868b-f364-439d-a658-8b800bbaf552/OT.create'
            ]

          });

      const payload =
        this.decodeJwtPayload(
          response.accessToken
        );

      const roles: string[] =
        payload.roles ?? [];

      this.userRoles.set(roles);

      this.userRole.set(
        roles.length > 0
          ? roles.join(', ')
          : 'Sin rol'
      );

    } catch (error) {

      console.error(
        'Error obteniendo rol:',
        error
      );
    }
  }

  // ==========================================================
  // PEDIDOS
  // ==========================================================

  listarPedidos(): void {

    this.ordersStatus.set(
      'Consultando pedidos mediante AWS...'
    );

    this.http
      .get<any[]>(
        this.ordersApi
      )
      .subscribe({

        next: (orders) => {

          this.ordersStatus.set(
            `Pedidos consultados correctamente: ${orders.length}`
          );

          this.ordersJson.set(
            JSON.stringify(
              orders,
              null,
              2
            )
          );
        },

        error: (error) => {

          console.error(
            'Error pedidos:',
            error
          );

          this.ordersStatus.set(
            `Error consultando pedidos: ${error.status}`
          );
        }

      });
  }

  crearPedido(): void {

    const body = {

      customerId:
        this.graphEmail()
        ||
        'usuario-pedidos360',

      description:
        'Pedido creado desde Angular mediante AWS',

      total:
        24990
    };

    this.createStatus.set(
      'Creando pedido...'
    );

    this.http
      .post<any>(
        this.ordersApi,
        body
      )
      .subscribe({

        next: (order) => {

          this.createStatus.set(
            `Pedido creado correctamente. ID: ${order.id}`
          );

          this.listarPedidos();
        },

        error: (error) => {

          console.error(
            'Error creando pedido:',
            error
          );

          this.createStatus.set(
            `Error creando pedido: ${error.status}`
          );
        }

      });
  }

  // ==========================================================
  // CATÁLOGO
  // ==========================================================

  listarProductos(): void {

    this.catalogStatus.set(
      'Consultando catálogo...'
    );

    this.http
      .get<any[]>(
        this.productsApi
      )
      .subscribe({

        next: (products) => {

          this.products.set(products);

          this.catalogStatus.set(
            `Productos encontrados: ${products.length}`
          );
        },

        error: (error) => {

          console.error(
            'Error catálogo:',
            error
          );

          this.catalogStatus.set(
            `Error consultando catálogo: ${error.status}`
          );
        }

      });
  }

  crearProductoPrueba(): void {

    const body = {

      name:
        'Combo Pedidos360',

      description:
        'Producto creado desde Angular',

      price:
        12990,

      stock:
        15,

      active:
        true
    };

    this.productActionStatus.set(
      'Creando producto...'
    );

    this.http
      .post<any>(
        this.productsApi,
        body
      )
      .subscribe({

        next: (product) => {

          this.productActionStatus.set(
            `Producto creado correctamente. ID: ${product.id}`
          );

          this.listarProductos();
        },

        error: (error) => {

          console.error(
            'Error creando producto:',
            error
          );

          this.productActionStatus.set(
            `Error creando producto: ${error.status}`
          );
        }

      });
  }

  actualizarPrimerProducto(): void {

    const product =
      this.products()[0];

    if (!product) {

      this.productActionStatus.set(
        'Primero debes listar los productos'
      );

      return;
    }

    const body = {

      name:
        `${product.name} Actualizado`,

      description:
        product.description
        ??
        'Producto actualizado desde Angular',

      price:
        Number(product.price) + 1000,

      stock:
        Number(product.stock),

      active:
        product.active ?? true
    };

    this.productActionStatus.set(
      `Actualizando producto ${product.id}...`
    );

    this.http
      .put<any>(
        `${this.productsApi}/${product.id}`,
        body
      )
      .subscribe({

        next: () => {

          this.productActionStatus.set(
            `Producto ${product.id} actualizado correctamente`
          );

          this.listarProductos();
        },

        error: (error) => {

          console.error(
            'Error actualizando producto:',
            error
          );

          this.productActionStatus.set(
            `Error actualizando producto: ${error.status}`
          );
        }

      });
  }

  eliminarUltimoProducto(): void {

    const list =
      this.products();

    const product =
      list[list.length - 1];

    if (!product) {

      this.productActionStatus.set(
        'Primero debes listar los productos'
      );

      return;
    }

    this.productActionStatus.set(
      `Eliminando producto ${product.id}...`
    );

    this.http
      .delete(
        `${this.productsApi}/${product.id}`
      )
      .subscribe({

        next: () => {

          this.productActionStatus.set(
            `Producto ${product.id} eliminado correctamente`
          );

          this.listarProductos();
        },

        error: (error) => {

          console.error(
            'Error eliminando producto:',
            error
          );

          this.productActionStatus.set(
            `Error eliminando producto: ${error.status}`
          );
        }

      });
  }

  // ==========================================================
  // REPORTES
  // ==========================================================

  cargarReporte(): void {

    this.reportStatus.set(
      'Consultando reporte...'
    );

    this.http
      .get<any>(
        this.reportsApi
      )
      .subscribe({

        next: (report) => {

          this.reportStatus.set(
            'Reporte obtenido correctamente'
          );

          this.reportJson.set(
            JSON.stringify(
              report,
              null,
              2
            )
          );
        },

        error: (error) => {

          console.error(
            'Error reporte:',
            error
          );

          this.reportStatus.set(
            `Error consultando reporte: ${error.status}`
          );
        }

      });
  }

  // ==========================================================
  // PRUEBA DE AUTORIZACIÓN
  // ==========================================================

  probarAccesoRestringido(): void {

    this.securityStatus.set(
      'Probando acceso al endpoint protegido...'
    );

    this.http
      .get<any>(
        this.reportsApi
      )
      .subscribe({

        next: () => {

          this.securityStatus.set(
            '✅ HTTP 200 - Acceso autorizado correctamente para este rol.'
          );
        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'Resultado prueba seguridad:',
            error
          );

          if (error.status === 403) {

            this.securityStatus.set(
              '✅ HTTP 403 - Acceso denegado correctamente por falta de privilegios.'
            );

          } else if (error.status === 401) {

            this.securityStatus.set(
              '⚠️ HTTP 401 - Token ausente, inválido o vencido.'
            );

          } else {

            this.securityStatus.set(
              `❌ Error inesperado - HTTP ${error.status}`
            );

          }
        }

      });
  }

  // ==========================================================
  // JWT
  // ==========================================================

  decodeJwtPayload(
      token: string): any {

    const base64Url =
      token.split('.')[1];

    const base64 =
      base64Url
        .replace(/-/g, '+')
        .replace(/_/g, '/');

    const padded =
      base64.padEnd(
        base64.length
        +
        (4 - base64.length % 4) % 4,
        '='
      );

    const decoded =
      decodeURIComponent(
        atob(padded)
          .split('')
          .map(
            character =>
              '%' +
              (
                '00'
                +
                character
                  .charCodeAt(0)
                  .toString(16)
              ).slice(-2)
          )
          .join('')
      );

    return JSON.parse(decoded);
  }

  logout(): void {

    this.msal.logoutRedirect({

      postLogoutRedirectUri:
        'http://localhost:4200'

    });
  }
}