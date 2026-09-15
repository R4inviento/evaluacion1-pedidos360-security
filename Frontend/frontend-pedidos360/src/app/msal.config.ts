import {
  BrowserCacheLocation,
  InteractionType,
  IPublicClientApplication,
  PublicClientApplication
} from '@azure/msal-browser';

import {
  MsalGuardConfiguration,
  MsalInterceptorConfiguration
} from '@azure/msal-angular';

import {
  environment
} from '../environments/environment';

const BACKEND_SCOPE =
  'api://ff20868b-f364-439d-a658-8b800bbaf552/OT.create';

const API_BASE =
  'https://h8kncndcyj.execute-api.us-east-1.amazonaws.com/desarrollo/api';

export function MSALInstanceFactory():
  IPublicClientApplication {

  return new PublicClientApplication({

    auth: {

      clientId:
        environment.azure.clientId,

      authority:
        environment.azure.authority,

      redirectUri:
        environment.azure.redirectUri
    },

    cache: {

      cacheLocation:
        BrowserCacheLocation.LocalStorage
    }

  });
}

export function MSALGuardConfigFactory():
  MsalGuardConfiguration {

  return {

    interactionType:
      InteractionType.Redirect,

    authRequest: {

      scopes: [
        BACKEND_SCOPE
      ]
    }

  };
}

export function MSALInterceptorConfigFactory():
  MsalInterceptorConfiguration {

  const protectedResourceMap =
    new Map<string, Array<string>>([

      [
        'https://graph.microsoft.com/v1.0/me',
        [
          'User.Read'
        ]
      ],

      [
        API_BASE,
        [
          BACKEND_SCOPE
        ]
      ],

      [
        `${API_BASE}/*`,
        [
          BACKEND_SCOPE
        ]
      ]

    ]);

  return {

    interactionType:
      InteractionType.Redirect,

    protectedResourceMap
  };
}