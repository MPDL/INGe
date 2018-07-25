import { Injectable } from '@angular/core';
import { Router, Resolve, RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { first, map } from 'rxjs/operators';

import { UsersService } from './users.service';
import { User, Grant, BasicRO } from '../../base/common/model';
import { environment } from '../../../environments/environment';

@Injectable()
export class UserDetailsResolverService implements Resolve<User> {

    constructor(private userSvc: UsersService, private router: Router) { }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<User> {
        const url = environment.rest_url + environment.rest_users;
        const id = route.params['id'];
        if (id === 'new user') {
            const user = new User();
            user.loginname = 'new user';
            user.password = 'hard2Remember';
            user.grantList = new Array<Grant>();
            user.affiliation = new BasicRO();
            user.active = false;
            return of(user);
        } else {
            const token = route.queryParams['token'];
            let user: User;
            return this.userSvc.get(url, id, token)
                .pipe(
                    first(),
                    map((response) => {
                        user = response;
                        if (user.grantList) {
                            user.grantList.forEach(grant => this.userSvc.addNamesOfGrantRefs(grant));
                        }
                        return user;
                    })
                );
        }
    }
}
