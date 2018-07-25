import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User, Grant } from '../../base/common/model';
import { MessagesService } from '../../base/services/messages.service';
import { PubmanRestService } from '../../base/services/pubman-rest.service';
import { environment } from '../../../environments/environment';


@Injectable()
export class UsersService extends PubmanRestService {

  usersUrl: string = environment.rest_url + environment.rest_users;
  ous_url = environment.rest_url + environment.rest_ous;
  ctxs_url = environment.rest_url + environment.rest_contexts;

  users: User[] = [];
  user: User;

  constructor(httpc: HttpClient,
    private messageService: MessagesService) {
    super(httpc);
  }

  activate(user: User, token: string): Observable<User> {
    const userUrl = this.usersUrl + '/' + user.objectId + '/activate';
    const body = user.lastModificationDate;
    const headers = this.addHeaders(token, true);
    return this.getResource('PUT', userUrl, headers, body);
  }

  deactivate(user: User, token: string): Observable<User> {
    const userUrl = this.usersUrl + '/' + user.objectId + '/deactivate';
    const body = user.lastModificationDate;
    const headers = this.addHeaders(token, true);
    return this.getResource('PUT', userUrl, headers, body);
  }

  addGrants(user: User, grants: Grant[], token: string): Observable<User> {
    const userUrl = this.usersUrl + '/' + user.objectId + '/add';
    const body = JSON.stringify(grants);
    const headers = this.addHeaders(token, true);
    return this.getResource('PUT', userUrl, headers, body);
  }

  removeGrants(user: User, grants: Grant[], token: string): Observable<User> {
    const userUrl = this.usersUrl + '/' + user.objectId + '/remove';
    const body = JSON.stringify(grants);
    const headers = this.addHeaders(token, true);
    return this.getResource('PUT', userUrl, headers, body);
  }

  changePassword(user: User, token: string): Observable<User> {
    const userUrl = this.usersUrl + '/' + user.objectId + '/password';
    const body = user.password;
    const headers = this.addHeaders(token, false);
    return this.getResource('PUT', userUrl, headers, body);
  }

  addNamesOfGrantRefs(grant) {
    const ref = grant.objectRef;
    if (ref === undefined) {
    } else {
      if (ref.startsWith('ou')) {
        this.get(this.ous_url, ref, null)
          .subscribe(ou => {
            grant.ctxTitle = ou.metadata.name;
          });
      } else {
        if (ref.startsWith('ctx')) {
          this.get(this.ctxs_url, ref, null)
            .subscribe(ctx => {
              grant.ctxTitle = ctx.name;
            });
        }
      }
    }
  }
}
